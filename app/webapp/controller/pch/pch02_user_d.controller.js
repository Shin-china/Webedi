sap.ui.define([
    "umc/app/Controller/BaseController",
    "sap/ui/model/Filter",
    "umc/app/model/formatter",
    "sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter, Spreadsheet) {
    "use strict";
    return Controller.extend("umc.app.controller.pch.pch02_user_d", {
        formatter: formatter,

        onInit: function () {
            this._LocalData = this.getOwnerComponent().getModel("local");
            this._oDataModel = this.getOwnerComponent().getModel();
            this._ResourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();

            this._oDataModel.attachBatchRequestCompleted(function(oEvent) {
                this.setBusy(false);
                var errors = this._LocalData.getProperty("/errors");
                if (errors) {
                    // 处理错误
                }
                this._LocalData.setProperty("/errors", "");
            }.bind(this));
        },

        setBusy: function(busy){
            this._LocalData.setProperty("/busy", busy);
        },

        onExport: function () {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();
            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("noSelection")); // 提示未选择数据
                return;
            }
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });
            if (aSelectedData.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("noSelection")); // 提示没有数据
                return;
            }
            var aColumns = oTable.getColumns().map(function (oColumn) {
                return {
                    label: oColumn.getLabel().getText(),
                    type: "string",
                    property: oColumn.getTemplate().getBindingPath("text"),
                    width: parseFloat(oColumn.getWidth())
                };
            });
            new Spreadsheet({
                dataSource: aSelectedData, // 仅传递选中的数据
                columns: aColumns,
                fileName: "Export.xlsx",
                worker: false,  // Disable web worker for simplicity
                beforeExport: this.onBeforeExport.bind(this)
            }).build().finally(function () {
                sap.m.MessageToast.show(this._ResourceBundle.getText("exportFinished"));
            }.bind(this));
        },

        onBeforeExport: function(oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください。")); // 提示未选择数据
                oEvent.preventDefault(); // 取消导出操作
                return;
            }

            var oSettings = oEvent.getParameter("exportSettings");
            if (oSettings) {
                console.log("onBeforeExport called");
                console.log("Export Settings:", oSettings);
                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
                // 设置文件名为当前日期和时间
                oSettings.fileName = `仕入先コード_納期回答照会_${sDate}_${sTime}.xlsx`;
            }
        },

        onResend: function () {
            // 这里的 `this` 指向当前的 Controller 实例
            this.getModel().callFunction("/PCH02_CONFIRMATION_REQUEST", {
                method: "POST",
                urlParameters: this.getData(),
                // urlParameters: {
                //     parms: "your_parameters_here" // 替换为实际参数
                // },
                success: function (result) {
                    sap.m.MessageToast.show("Action executed successfully.");
                },
                error: function (oError) {
                    sap.m.MessageToast.show("Error executing action.");
                }
            });
        }
    });
});
