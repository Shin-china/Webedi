sap.ui.define([
    "umc/app/controller/BaseController",
    "sap/ui/model/Filter",
    "umc/app/model/formatter",
    "sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter, Spreadsheet) {
    "use strict";

    var _objectCommData = {
        _entity: "/PCH_T02_USER",
    };

    return Controller.extend("umc.app.controller.pch.pch02_user_d", {
        formatter: formatter,
  

        onInit: function () {
            this.getView().unbindElement();
			const oTable = this.byId("detailTable");
			// oTable.setSelectionMode("None");
			//  设置版本号
			this._setOnInitNo("PCH02");
			this.MessageTools._clearMessage();
			// this.MessageTools._initoMessageManager(this);

			// this.getRouter().getRoute("RouteCre_pch03").attachPatternMatched(this._onRouteMatched, this);
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

        onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();
        
            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください。"));
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
                oSettings.fileName = `納期回答照会_${sDate}${sTime}.xlsx`;
        
                // 设置 DELIVERY_DATE 列格式
                oSettings.workbook.columns.forEach(function (oColumn) {
                    if (oColumn.property === "DELIVERY_DATE") {
                        oColumn.type = sap.ui.export.EdmType.Date; // 设置为日期类型
                        oColumn.format = "yyyy/M/d"; // 设置日期格式，去掉时间
                    }
                });
            }
        },        

        onResend: function () {
            var that = this;
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください")); // 提示未选择数据
                return;
            }
            var IdList = that._TableDataList("detailTable", 'NO_DETAILS');
            if (IdList) {
            that._getDataInfo(that, IdList, "/PCH_T02_USER", "NO_DETAILS").then((J) => {

            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });

            // 检查J.results中的数据是否有STATUS为"2"
            var bHasReflected = J.results.some(function (oData) {
                return oData.STATUS === "2"; // 检查J.results中的每项数据的STATUS字段
            });

            if (bHasReflected) {
                sap.m.MessageToast.show("反映済された情報なので、再送信できません。"); // 显示错误消息
                return; // 终止后续操作
            }

            //调用po接口
            this._invoPo(J.results);
            });
            };
    },


    });
});
