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

    return Controller.extend("umc.app.controller.pch.pch08_list", {
        formatter: formatter, // 将格式化器分配给控制器

        onInit: function () {
            // 初始化代码
            // 这里可以添加其他初始化逻辑，比如绑定数据等
            console.log("Controller initialized.");

              this.MessageTools._clearMessage();
			  this.MessageTools._initoMessageManager(this);
            this._ResourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
            this._localModel = new sap.ui.model.json.JSONModel();
            this._localModel.setData({
                "show": true,
                "save": false
            });
            this.getView().setModel(this._localModel, "localModel");

        },

        onExport: function () {
            var oTable = this.getView().byId("listTable");
            var aSelectedIndices = oTable.getSelectedIndices();
            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
                return;
            }
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });
            if (aSelectedData.length === 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
                return;
            }

            var aColumns = oTable.getColumns().map(function (oColumn) {
                var sDateFormat = 'yyyy-MM-dd';
                var sFormat = '';
                var sType = '';

                var property = oColumn.getTemplate().getBindingPath("text") || oColumn.getTemplate().mBindingInfos.value.parts[0].path;

                if (property === 'VALIDATE_START' || property === 'VALIDATE_END' || property === 'LEAD_TIME') {
                    sFormat = sDateFormat;
                    sType = 'date';
                } else {
                    sFormat = '';
                    sType = 'string';
                }


                return {
                    label: oColumn.getLabel().getText(),
                    type: sType,
                    property: oColumn.getTemplate().getBindingPath("text") || oColumn.getTemplate().mBindingInfos.value.parts[0].path,
                    width: parseFloat(oColumn.getWidth()),
                    format: sFormat
                };
            });

            var oSettings = {
                dataSource: aSelectedData,
                workbook: {
                    columns: aColumns,

                    context: {
                        sheetName: "購買見積回答"
                    }
                }
            };

            var oSheet = new Spreadsheet(oSettings);
            oSheet.attachBeforeExport(this.onBeforeExport.bind(this));
            oSheet.build()
                .then(function () {
                    sap.m.MessageToast.show(this._ResourceBundle.getText("exportFinished"));
                })
                .finally(function () {
                    oSheet.destroy();
                });
        },

        onEdit: function () {
            this._localModel.setProperty("/show", false);
            this._localModel.setProperty("/save", true);
        },

        __formatDate: function(date){
            let newDate = new Date(date);
            return newDate.toISOString().split('T')[0];
        },

        onSave: function () {
            var that = this;
            that._setBusy(true);

            var oTable = this.getView().byId("listTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
                oEvent.preventDefault(); // 取消导出操作
                that._setBusy(false);
                return;
            }

            var oPostData = {}, oPostList = {}, aPostItem = [];
            aSelectedIndices.map(function (iIndex) {
                var oItem = oTable.getContextByIndex(iIndex).getObject();
                oItem.VALIDATE_START = that.__formatDate(oItem.VALIDATE_START);
                oItem.VALIDATE_END = that.__formatDate(oItem.VALIDATE_END);
                oItem.LEAD_TIME = that.__formatDate(oItem.LEAD_TIME);


                aPostItem.push(oItem);
            });

            oPostList = JSON.stringify({
                "list": aPostItem
            });

            oPostData = {
                "str": oPostList
            };


            this._callCdsAction("/PCH08_SAVE_DATA", oPostData, this).then(

                (oData) => {


                    var myArray = JSON.parse(oData.PCH08_SAVE_DATA);
                    this._localModel.setProperty("/show", true);
                    this._localModel.setProperty("/save", false);
                    if (myArray.err) {
                        this._localModel.setProperty("/show", false);
                        this._localModel.setProperty("/save", true);
                        that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("pch", myArray.reTxt, this.getView()), null, 1, this.getView());
                    }
                    that._setBusy(false);

                });



        },

        onConfirm: function () {

        },

        onRefrenceNoChange:function(oEvent){
            oEvent.oSource.getBindingContext().setProperty("CUSTOMER","12345")
        },

        onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("listTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
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
                oSettings.fileName = `購買見積回答_${sDate}${sTime}.xlsx`;
            }
        },

        onExportToPDF: function () {
            var that = this;

            // 调用检查逻辑
            if (!this.checkSelectedSuppliers()) {
                return; // 如果检查不通过，则终止执行
            }

            let options = { compact: true, ignoreComment: true, spaces: 4 };
            var IdList = that._TableDataList("detailTable", 'SUPPLIER');

            if (IdList) {
                that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T05_ACCOUNT_DETAIL_EXCEL", "SUPPLIER").then((oData) => {
                    let sResponse = json2xml(oData, options);
                    console.log(sResponse);
                    that.setSysConFig().then(res => {
                        // 调用打印方法
                        that.PrintTool._detailSelectPrintDow(that, sResponse, "test2/test3", oData, null, null, null, null);
                    });
                });
            }
        },

        checkSelectedSuppliers: function () {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            // 检查是否有选中的数据
            if (aSelectedIndices.length === 0) {
                MessageToast.show("選択されたデータがありません、データを選択してください。");
                return false;
            }

            // 遍历选中的行，提取所需数据
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });

            // 检查 SUPPLIER 是否相同
            var supplierSet = new Set(aSelectedData.map(data => data.SUPPLIER));
            if (supplierSet.size > 1) {
                MessageBox.error("複数の取引先がまとめて配信することができませんので、1社の取引先を選択してください。");
                return false;
            }

            return true; // 检查通过
        }



    });
});
