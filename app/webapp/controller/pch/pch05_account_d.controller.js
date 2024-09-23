sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch05_account_d", {
		formatter : formatter,

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

        onDataLoad: function () {
            const oData = this._oDataModel.getData();
            oData.results.forEach(item => {
                if (item.SHKZG === 'H') {
                    item.PRICE_AMOUNT_DISPLAY = (-Math.abs(item.PRICE_AMOUNT)).toString();
                    item.TOTAL_AMOUNT_DISPLAY = (-Math.abs(item.TOTAL_AMOUNT)).toString();
                } else {
                    item.PRICE_AMOUNT_DISPLAY = item.PRICE_AMOUNT.toString();
                    item.TOTAL_AMOUNT_DISPLAY = item.TOTAL_AMOUNT.toString();
                }
            });
        
            // 不调用 refresh()，让视图根据模型的变化自动更新
            this.getView().getModel().setData(oData); // 重新设置数据
        },
        
        

        setBusy: function (busy) {
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

        onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください。")); // 提示未选择数据
                oEvent.preventDefault(); // 取消导出操作
                return;
            }
            // var mExcelSettings = oEvt.getParameter("exportSettings");
            // for (var i = 0; i < mExcelSettings.workbook.columns.length; i++) {
            //     // this.CommTools._setExcelFormatDate(mExcelSettings, i, "VALID_DATE_FROM");
            //     // this.CommTools._setExcelFormatDate(mExcelSettings, i, "VALID_DATE_TO");
            //     // this.CommTools._setExcelFormatDateTime(mExcelSettings, i, "CD_TIME");
            //     // this.CommTools._setExcelFormatDateTime(mExcelSettings, i, "UP_TIME");
            //     // this.CommTools._setExcelFormatDateTime(mExcelSettings, i, "LAST_LOGIN_TIME");
            //     }
          

            var oSettings = oEvent.getParameter("exportSettings");
            if (oSettings) {
                console.log("onBeforeExport called");
                console.log("Export Settings:", oSettings);
                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
                // 设置文件名为当前日期和时间
                oSettings.fileName = `買掛金明細_${sDate}${sTime}.xlsx`;
            }
        },
		
	
	});
});