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
            this._oDataModel.attachBatchRequestCompleted(function(oEvent){
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
        // 
        onBeforeExport: function(oEvent) {
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

        onResend: function() {
            var oSelectedContexts = this.byId("smartTable").getTable().getSelectedContexts();

            if (oSelectedContexts.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("pchNoSelectionMessage"));
                return;
            }

            var aSelectedPOs = oSelectedContexts.map(function(oContext) {
                return oContext.getObject();
            });

            var that = this;
            var oActionParams = {
                "PurchaseOrders": aSelectedPOs.map(function(po) {
                    return po.PO_NO;  // 假设 PO_NO 是采购订单号
                })
            };

            this.setBusy(true);

            this._oDataModel.callFunction("/PCH02_CONFIRMATION_REQUEST", { 
                method: "POST",
                urlParameters: oActionParams,
                success: function(oData, response) {
                    that.setBusy(false);
                    sap.m.MessageToast.show(that._ResourceBundle.getText("pchResendSuccessMessage"));
                },
                error: function(oError) {
                    that.setBusy(false);
                    sap.m.MessageBox.error(that._ResourceBundle.getText("pchResendErrorMessage"));
                }
            });
        }
    });
});