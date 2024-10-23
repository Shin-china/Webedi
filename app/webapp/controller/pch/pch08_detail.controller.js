sap.ui.define([
    "umc/app/Controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "umc/app/model/formatter",
    "sap/ui/export/Spreadsheet",
    "sap/ui/model/json/JSONModel",
], function (Controller, MessageToast, MessageBox, formatter, Spreadsheet, JSONModel) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch08_detail", {


        onInit: function () {
            // 初始化JSONModel
            let oModel = new sap.ui.model.json.JSONModel();
            oModel.oData.dataList = [];
            this.getView().setModel(oModel);
            this.getRouter().getRoute("RouteView_pch08").attachPatternMatched(this._onRouteMatched, this);


        },

        _onRouteMatched: function (oEvent) {
            let that = this;
            let key = oEvent.getParameter("arguments").headID;

            let params = { param: key };
            that._callCdsAction("/PCH08_SHOW_DETAIL", params, that).then((oData) => {
                let json = JSON.parse(oData.PCH08_SHOW_DETAIL);
                let data = this.getView().getModel().oData.dataList;
                // 清空
                data.length = 0;
                json.forEach(item => {
                    data.push(item);
                })
                this.getModel().refresh(true);
            });

        }


    });
});
