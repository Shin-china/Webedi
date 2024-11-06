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

    return Controller.extend("umc.app.controller.pch.pch10_quotation_detail", {

        formatter: formatter, // 将格式化器分配给控制器
        onInit: function () {
            // 初始化JSONModel
            let oModel = new sap.ui.model.json.JSONModel();
            oModel.oData.dataList = [];
            this.getView().setModel(oModel);

            this.getRouter().getRoute("RouteView_pch10_d").attachPatternMatched(this._onRouteMatched, this);

        },

        _onRouteMatched: function (oEvent) {
            let that = this;
            let key = oEvent.getParameter("arguments").headID;

            let params = { param: key };
            that._callCdsAction("/PCH08_SHOW_DETAIL", params, that).then((oData) => {
                let json = JSON.parse(oData.PCH08_SHOW_DETAIL);
                this._data = json;
                let data = this.getView().getModel().oData.dataList;

                // 清空
                data.length = 0;
                let rowNo = [];
                json.forEach(item => {
                    rowNo.push(item.MAX)
                })

                let maxNum = Math.max.apply(null, rowNo);
                this._maxNum = maxNum;
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


        onExport: function () {

            var data = this._data; 

            // 配置导出字段和格式
            var aColumns = [
                {
                    label: '購買見積番号', 
                    property: 'QUO_NO',
                    type: 'string'
                },
                {
                    label: '品名', 
                    property: 'MAT',
                    type: 'string'
                }

            ];
            for (let i = 1; i <= this._maxNum; i++) {

                aColumns.push({ 
                    label: '数量' + i ,
                    property: 'QTY_' + i,
                    type: 'number'
                });
                aColumns.push({ 
                    label: '単価' + i ,
                    property: 'PRICE_' + i,
                    type: 'number'
                })


            }

            // 创建导出的配置
            var oSettings = {
                workbook: {
                    columns: aColumns
                },
                dataSource: data, // 数据源
                fileName: "ExportData.xlsx", // 导出的文件名
                worker: false // 在本地不使用 web worker
            };

            // 使用 sap.ui.export.Spreadsheet 导出数据为 Excel
            var oSheet = new sap.ui.export.Spreadsheet(oSettings);
            oSheet.build().then(function () {
                sap.m.MessageToast.show("Export success");
            }).catch(function (oError) {
                sap.m.MessageToast.show("Export failed");
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
