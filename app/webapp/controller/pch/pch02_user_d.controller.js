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

            this._oDataModel.attachBatchRequestCompleted(function (oEvent) {
                this.setBusy(false);
                var errors = this._LocalData.getProperty("/errors");
                if (errors) {
                    // 处理错误
                }
                this._LocalData.setProperty("/errors", "");
            }.bind(this));
        },
        
        setBusy: function (busy) {
            if (this._LocalData) { // 检查 _LocalData 是否已初始化
                this._LocalData.setProperty("/busy", busy);
            } else {
                console.error("Local data model is not initialized.");
            }
        },        

        // setBusy: function (busy) {
        //     this._LocalData.setProperty("/busy", busy);
        // },

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

        onBeforeExport: function (oEvent) {
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
                oSettings.fileName = `納期回答照会_${sDate}${sTime}.xlsx`;
            }
        },

        onResend: function () {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください")); // 提示未选择数据
                return;
            }

            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });

            // 检查是否有 STATUS 为 "2.反映済" 的数据
            var bHasReflected = aSelectedData.some(function (oData) {
                return oData.STATUS === "2";
            });

            if (bHasReflected) {
                sap.m.MessageToast.show("反映済された情報なので、再送信できません。"); // 显示错误消息
                return; // 终止后续操作
            }

            //调用po接口
            this._invoPo(aSelectedData);

         
        //     success: function (result) {
        //         // 确保 result 是一个有效的对象
        //         if (result && result.status && result.message) {
        //             var status = result.status;
        //             var message = result.message;
                    
        //             // 根据接口返回的状态判断成功或错误
        //             if (status === "success") {
        //                 sap.m.MessageToast.show(message); // 成功消息
        //             } else {
        //                 sap.m.MessageToast.show(message); // 错误消息
        //             }
        //         } else {
        //             sap.m.MessageToast.show("返回数据格式不正确。");
        //         }
        //     },
        //     error: function (oError) {
        //         // 尝试获取具体的错误消息
        //         var errorMessage = oError.responseText || "发生了一个未知错误。";
        //         sap.m.MessageToast.show(errorMessage);
        //     }
        // });
    },


    });
});
