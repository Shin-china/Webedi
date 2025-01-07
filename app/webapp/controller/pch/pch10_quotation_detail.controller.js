sap.ui.define([
    "umc/app/controller/BaseController",
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
                this._maxPersonSize = 0;
                this._maxQtySize = 0;

                json.forEach(item=>{
                    if(item.PERSON_SIZE > this._maxPersonSize){
                        this._maxPersonSize = item.PERSON_SIZE;
                    }

                    if(item.QUANTITY_SIZE > this._maxQtySize){
                        this._maxQtySize = item.QUANTITY_SIZE;
                    }
                });

                //this._maxNum = maxNum;
                // 清除旧列 重新生成
                let oldTable = this.getView().byId("dataTable");
                let oldColumns = oldTable.getColumns();
                oldColumns.forEach(c => {
                    oldTable.removeColumn(c)
                })
                let quoNoColumn = new sap.ui.table.Column({ label: new sap.m.Label({ text: "購買見積番号" }), template: new sap.m.Input({ value: '{QUO_NO}', editable: false }) });
                let quoItemColumn = new sap.ui.table.Column({ label: new sap.m.Label({ text: "管理No" }), template: new sap.m.Input({ value: '{QUO_ITEM}', editable: false }) });
                let matColumn = new sap.ui.table.Column({ label: new sap.m.Label({ text: "SAP品番" }), template: new sap.m.Input({ value: '{MATERIAL_NUMBER}', editable: false }) });
                let custMatColumn = new sap.ui.table.Column({ label: new sap.m.Label({ text: "顧客品番" }), template: new sap.m.Input({ value: '{CUST_MATERIAL}', editable: false }) });
                let manufactMatColumn = new sap.ui.table.Column({ label: new sap.m.Label({ text: "メーカー品番" }), template: new sap.m.Input({ value: '{MANUFACT_MATERIAL}', editable: false }) });
                this.getView().byId("dataTable").addColumn(quoNoColumn);
                this.getView().byId("dataTable").addColumn(quoItemColumn);
                this.getView().byId("dataTable").addColumn(matColumn);
                this.getView().byId("dataTable").addColumn(custMatColumn);
                this.getView().byId("dataTable").addColumn(manufactMatColumn);

                //员数列
                for (let i = 1; i <= this._maxPersonSize; i++) { 
                    let person = "{PERSON_" + i + "}";
                    let column = new sap.ui.table.Column({
                        label: new sap.m.Label({ text: "員数" + i }),
                        template: new sap.m.Input({ value: person, editable: false })
                    });
                    this.getView().byId("dataTable").addColumn(column);
                }

                // 加列 i:数量列
                for (let i = 1; i <= this._maxQtySize; i++) { 
                    let priceFlag = "PRICE_" + i;
                    let price = "{PRICE_" + i + "}";
                    let qty = "{QTY_" + i + "}";
                    let column1 = new sap.ui.table.Column({
                        label: new sap.m.Label({ text: "単価" + i }),
                        template: new sap.m.Input({ value: price, editable: false })
                    });
                    let column2 = new sap.ui.table.Column({
                        label: new sap.m.Label({ text: "数量" + i }),
                        template: new sap.m.Input({ value: qty, editable: false })
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
                    label: '管理No', 
                    property: 'QUO_ITEM',
                    type: 'string'
                },
                {
                    label: 'SAP品番',
                    property: 'MATERIAL_NUMBER',
                    type:'string'
                },
                {
                    label: '顧客品番',
                    property: 'CUST_MATERIAL',
                    type:'string'
                },
                {
                    label: 'メーカー品番',
                    property: 'MANUFACT_MATERIAL',
                    type:'string'
                }

            ];

            for (let i = 1; i <= this._maxPersonSize; i++) {
                aColumns.push({
                    label: '員数' + i,
                    property: 'PERSON_' + i,
                    type: 'number'
                });
            }

            for (let i = 1; i <= this._maxQtySize; i++) {

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
                    sap.m.MessageBox.success("単価は更新されました。");
                }
                this.getModel().refresh(true);
            })

        },

    });
});
