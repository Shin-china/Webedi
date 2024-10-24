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
                let rowNo = [];
                json.forEach(item => {
                    rowNo.push(item.MAX)
                })

                let maxNum = Math.max.apply(null, rowNo);
                if (maxNum > 3) {
                    // 加列
                    for (let i = 4; i < maxNum + 1; i++) {
                        let str = "PRICE" + i;
                        let column1 = new sap.m.Column({ header: new sap.m.Label({ text: "単価" + i }), });
                        let column2 = new sap.m.Column({ header: new sap.m.Label({ text: "数量" + i }) });
                        this.getView().byId("dataTable").addColumn(column1);
                        this.getView().byId("dataTable").addColumn(column2);
                        var colItems = this.getView().byId("dataTable").getColumns();
                        console.log(colItems)
                        // var value = new sap.m.Text("QTY"+i, { text: "100" });
                        // colItems.addCell(value);
                    }


                }
                json.forEach(item => {

                    
                    data.push(item)

                })






                this.getModel().refresh(true);
            });

        },























        onEdit: function () {
            let data = this.getView().getModel().oData.dataList;
            let params = { param: JSON.stringify(data) };
            console.log(data)
            this._callCdsAction("/PCH08_EDIT_DETAIL", params, this).then(oData => {
                if (oData.PCH08_EDIT_DETAIL == "success") {
                    sap.m.MessageBox.success("修改成功!");
                }
                this.getModel().refresh(true);
            })

        },






    });
});
