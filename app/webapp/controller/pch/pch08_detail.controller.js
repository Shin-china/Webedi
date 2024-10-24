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

        formatter: formatter, // 将格式化器分配给控制器
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
                // 清除旧列 重新生成
                let oldTable = this.getView().byId("dataTable");
                let oldColumns = oldTable.getColumns();
                oldColumns.forEach(c => {
                    oldTable.removeColumn(c)
                })
                let quoNoColumn = new sap.ui.table.Column({ label: new sap.m.Label({ text: "購買見積番号" }), template: new sap.m.Input({ value: '{QUO_NO}', editable: false }) });
                let matColumn = new sap.ui.table.Column({ label: new sap.m.Label({ text: "品名" }), template: new sap.m.Input({ value: '{MAT}', editable: false }) });
                this.getView().byId("dataTable").addColumn(quoNoColumn);
                this.getView().byId("dataTable").addColumn(matColumn);
                // 加列 i:列
                for (let i = 1; i <= maxNum; i++) {
                    let qtyFlag = "QTY_" + i;
                    let priceFlag = "PRICE_" + i;
                    let price = "{PRICE_" + i + "}";
                    let qty = "{QTY_" + i + "}";
                    let column1 = new sap.ui.table.Column({
                        label: new sap.m.Label({ text: "単価" + i }),
                        template: new sap.m.Input({ value: price, editable: "{=${" + priceFlag + "} === undefined || ${" + priceFlag + "} === null || ${" + priceFlag + "} === ''  ? false : true}" })
                    });
                    let column2 = new sap.ui.table.Column({
                        label: new sap.m.Label({ text: "数量" + i }),
                        template: new sap.m.Input({ value: qty, editable: "{=${" + qtyFlag + "} === undefined || ${" + qtyFlag + "} === null || ${" + qtyFlag + "} === ''  ? false : true}" })
                    });
                    this.getView().byId("dataTable").addColumn(column1);
                    this.getView().byId("dataTable").addColumn(column2);

                }


                // 添加数据
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
