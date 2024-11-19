sap.ui.define([
    "umc/app/Controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "sap/m/BusyDialog",
    "umc/app/model/formatter",
    "umc/app/model/customFormatter",
    "sap/ui/export/Spreadsheet",
    "sap/ui/model/json/JSONModel",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator"
], function (Controller, MessageToast, MessageBox, BusyDialog, formatter, customFormatter, Spreadsheet, JSONModel, Filter, FilterOperator) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch08_list", {
        formatter: formatter, // 将格式化器分配给控制器
        customFormatter: customFormatter, 

        onInit: function () {
            // 初始化代码
            // 这里可以添加其他初始化逻辑，比如绑定数据等
            console.log("Controller initialized.");

            this.MessageTools._clearMessage();
            this.MessageTools._initoMessageManager(this);
            this._ResourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
            this._PchResourceBundle = this.getOwnerComponent().getModel("pch").getResourceBundle();
            this._localModel = new sap.ui.model.json.JSONModel();
            this._localModel.setData({
                "show": true,
                "save": false
            });
            this.getView().setModel(this._localModel, "localModel");
            this._BusyDialog = new sap.m.BusyDialog();
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

            //动态列
            var sKeys = '';
            var sResult = '';
            var iMaxTotal = 0;
            var aExport = [];
            aSelectedData.forEach(row => {
                if (sKeys === "") {
                    sKeys = row.QUO_NUMBER;
                } else {
                    sKeys = sKeys + "," + row.QUO_NUMBER;
                }
            })

            var params = { param: sKeys };

            this._callCdsAction("/PCH08_SHOW_DETAIL", params, this).then((oData) => {
                let json = JSON.parse(oData.PCH08_SHOW_DETAIL);
                console.log(json)
                sResult = json;

                //拼接动态对象
                aSelectedData.forEach(data => {
                    var oExportData = data;

                    var oQtyInfo = sResult.filter(e => {
                        return e.QUO_NO == data.QUO_NUMBER;
                    });

                    if (!oQtyInfo || oQtyInfo.length == 0) {
                        aExport.push(oExportData);
                        return;
                    }

                    oQtyInfo.forEach(item => {
                        let iMax = item["MAX"];
                        if (!iMax) {
                            return;
                        }

                        if (iMax > iMaxTotal) {
                            iMaxTotal = iMax;
                        }

                        for (var i = 1; i <= iMax; i++) {
                            var sQty = 'QTY_' + i;
                            oExportData[sQty] = item[sQty];

                            var sPrice = 'PRICE_' + i;
                            oExportData[sPrice] = item[sPrice];
                        }
                    })

                    aExport.push(oExportData);
                })

                var aColumns = oTable.getColumns().map(function (oColumn) {
                    var sDateFormat = 'yyyy-MM-dd';
                    var sFormat = '';
                    var sType = '';
                    var sLabel = '';

                    var property = oColumn.getTemplate().getBindingPath("text") || oColumn.getTemplate().mBindingInfos.value.parts[0].path;

                    if (property === 'VALIDATE_START' || property === 'VALIDATE_END' || property === 'TIME') {
                        sFormat = sDateFormat;
                        sType = 'date';
                    } else {
                        sFormat = '';
                        sType = 'string';
                    }


                    return {

                        label: sLabel || oColumn.getLabel().getText(),
                        type: sType,
                        property: oColumn.getTemplate().getBindingPath("text") || oColumn.getTemplate().mBindingInfos.value.parts[0].path,
                        width: parseFloat(oColumn.getWidth()),
                        format: sFormat
                    };
                });

                //动态列数据
                for (var i = 1; i <= iMaxTotal; i++) {
                    var sQty = 'QTY_' + i;
                    var sPrice = 'PRICE_' + i;
                    aColumns.push({
                        label: "数量_" + i,
                        type: 'string',
                        property: sQty,
                        width: 13,
                        format: ''
                    });

                    aColumns.push({
                        label: "単価_" + i,
                        type: 'string',
                        property: sPrice,
                        width: 13,
                        format: ''
                    });

                }

                var oSettings = {
                    dataSource: aExport,
                    workbook: {
                        columns: aColumns,

                        context: {
                            sheetName: "購買見積回答"
                        }
                    }
                };

                var oSheet = new sap.ui.export.Spreadsheet(oSettings);
                oSheet.attachBeforeExport(this.onBeforeExport.bind(this));
                oSheet.build()
                    .then(function () {
                        sap.m.MessageToast.show(this._ResourceBundle.getText("exportFinished"));
                    })
                    .finally(function () {
                        oSheet.destroy();
                    });
            });
        },

        onEdit: function () {
            this._localModel.setProperty("/show", false);
            this._localModel.setProperty("/save", true);
        },

        __formatDate: function (date) {
            let newDate = new Date(date);
            return newDate.toISOString().split('T')[0];
        },

        onSave: function (oEvent) {
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
                oItem.TIME = that.__formatDate(oItem.TIME);

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


                    var oResult = JSON.parse(oData.PCH08_SAVE_DATA);
                    this._localModel.setProperty("/show", true);
                    this._localModel.setProperty("/save", false);
                    if (oResult.err) {
                        this._localModel.setProperty("/show", false);
                        this._localModel.setProperty("/save", true);
                        that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
                    } else {
                        sap.m.MessageToast.show(that._PchResourceBundle.getText("SAVE_SUCCESS"));
                    }
                    that._setBusy(false);

                });



        },

        onConfirm: function () {

        },

        onRefrenceNoChange: function (oEvent) {
            oEvent.oSource.getBindingContext().setProperty("CUSTOMER", "12345")
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
        },

        getDetail: function (oEvent) {

            let that = this;
            let selectedData = this._getSelectedIndicesDatasByTable("listTable");
            if (selectedData.length == 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
                return false;
            }
            let para = [];
            selectedData.forEach(item => {
                let key = item.QUO_NUMBER;
                para.push(key);
            });
            this._onPress(oEvent, "RouteView_pch08", this.unique(para).join(","));

            // let params = { param: para };
            // that._callCdsAction("/PCH08_SHOW_DETAIL", params, that).then((oData) => {
            //     let json = JSON.parse(oData.PCH08_SHOW_DETAIL);
            //     console.log(json)
            //   });
        },

        unique: function (arr) {
            return [...new Set(arr)];
        },

        onCloseAttachmentDialog: function () {
            this._closeDialog(this, 'idAttachmentDialog');
        },

        onFileChange: function (oEvent) {
            var oFile = oEvent.getParameter("files")[0];
            if (!oFile) {
                return;
            }

            var oAttachmentModel = this.getView().getModel("attachment");

            if (!oAttachmentModel) {
                return;
            }

            var sQuoNumber = oAttachmentModel.getProperty("/QUO_NUMBER");
            if (!sQuoNumber) {
                sap.m.MessageToast.show(this._PchResourceBundle.getText("QUO_NUMBER_IS_NULL"));
                return;
            }

            let sQuoItem = oAttachmentModel.getProperty("/QUO_ITEM");
            let sObject = sQuoNumber+'_'+sQuoItem;

            var oReader = new FileReader();
            oReader.readAsDataURL(oFile);
            this._BusyDialog.open();
            var that = this;
            oReader.onload = function (e) {
                let oFileData = e.target.result;
                let sContent = oFileData.substring(oFileData.indexOf("base64,") + 7);
                var oUploadData = {};
                oUploadData = {
                    "object": sObject,
                    "object_type": "PCH08",
                    "file_type": btoa(oFile.type),
                    "file_name": oFile.name,
                    "value": sContent
                };
                let oAttachmentObj = {
                    "attachmentJson": oUploadData
                };

                $.ajax({
                    url: "srv/odata/v4/Common/s3uploadAttachment",
                    type: "POST",
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    async: false,
                    crossDomain: true,
                    responseType: 'blob',
                    data: JSON.stringify(oAttachmentObj),
                    success: function (base64) {
                        that.getView().getModel().refresh();
                        console.log("上传成功");
                    }
                })


                this.byId("idFileUploader").clear();
                this._BusyDialog.close();
            }.bind(this);
        },

        onDestroyAttachmentDialog: function (oEvent) {
            this._destroyDialog(oEvent);
        },

        onOpenAttachmentDialog: function () {
            var oTable = this.byId("listTable");

            if (!oTable) {
                return;
            }

            const aIndex = oTable.getSelectedIndices();

            if (aIndex.length !== 1) {
                sap.m.MessageToast.show(this._PchResourceBundle.getText("SELECT_ONE_RECORD"));
                return;
            }

            let oSelectData = oTable.getContextByIndex(aIndex[0]).getObject();

            var sQuoNumber = oSelectData.QUO_NUMBER;
            if (!sQuoNumber) {
                sap.m.MessageToast.show(this._PchResourceBundle.getText("QUO_NUMBER_IS_NULL"));
                return;
            }

            var oAttachmentModel = new sap.ui.model.json.JSONModel(oSelectData);

            this.getView().setModel(oAttachmentModel, "attachment");

            var oBindingContext = oTable.getContextByIndex(aIndex[0]);

            this.loadFragment({
                name: 'umc.app.view.pch.pch08_list_a'
            }).then(function (oDialog) {
                oDialog.setBindingContext(oBindingContext);
                oDialog.open();
            });
        },

        onDeleteAttachment: function (oEvent) {
            var oTable = this.byId("id.AttachmentTable");

            if (!oTable) {
                return;
            }

            var aIndex = oTable.getSelectedIndices();

            if (aIndex.length < 1) {
                sap.m.MessageToast.show(this._PchResourceBundle.getText("SELECT_AT_LEAST_ONE_RECORD"));
                return;
            }

            var that = this;
            var aKey = [];
            for (var i = 0; i < aIndex.length; i++) {
                var oBindingContext = oTable.getContextByIndex(aIndex[i]);
                var oData = oBindingContext.getObject();
                var sAttachmentId = oData.ID;
                aKey.push(sAttachmentId);
            }

            for (var i = 0; i < aKey.length; i++) {
                var oAttachmentObj = {
                    "key": aKey[i]
                };

                $.ajax({
                    url: "srv/odata/v4/Common/deleteS3Object",
                    type: "POST",
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    async: false,
                    crossDomain: true,
                    data: JSON.stringify(oAttachmentObj),
                    success: function (base64) {
                        that.getView().getModel().refresh();
                    }
                })

            }
        },

        _closeDialog: function (oContext, sDialogId) {
            var oDialog = oContext.getView().byId(sDialogId);
            oDialog.close();
            oDialog.destroy();
        },

        _destroyDialog: function (oEvent) {
            var oSource = oEvent.getSource();
            if (oSource) {
                oSource.destroy();
            }
        },

        onBeforeBindAttachment: function (oEvent) {
            var oParameters = oEvent.getParameter("bindingParams");
            var oAttachmentModel = this.getView().getModel("attachment");

            //Filter
            if (oAttachmentModel) {
                let sQuoNumber = oAttachmentModel.getProperty("/QUO_NUMBER");
                let sQuoItem = oAttachmentModel.getProperty("/QUO_ITEM");
                let sFilter = sQuoNumber+'_'+sQuoItem;
                if (sQuoNumber !== '') {
                    oParameters.filters.push(new sap.ui.model.Filter(
                        "OBJECT",
                        sap.ui.model.FilterOperator.EQ,
                        sFilter
                    ))
                } 
            }

            oParameters.filters.push(new sap.ui.model.Filter(
                "OBJECT_TYPE",
                sap.ui.model.FilterOperator.EQ,
                "PCH08"
            ));

            oParameters.parameters.select = oParameters.parameters.select + ",FILE_TYPE,ID,OBJECT_LINK,OBJECT_TYPE";

        },

        onDownloadAttachment: function (oEvent) {
            var oTable = this.byId("id.AttachmentTable");
            var that = this;

            if (!oTable) {
                return;
            }


            var oBindingContext = oEvent.oSource.getBindingContext();
            var oAttachment = oBindingContext.getObject();
            var sId = oAttachment.ID;
            var sType = '';
            if (sId) {
                var downloadJson = {
                    attachmentJson: [{
                        object: "download",
                        value: oAttachment.OBJECT_LINK
                    }]
                }
                sType = oAttachment.FILE_TYPE;
            }


            $.ajax({
                url: "srv/odata/v4/Common/s3DownloadAttachment",
                type: "POST",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                async: false,
                crossDomain: true,
                responseType: 'blob',
                data: JSON.stringify(downloadJson),
                success: function (base64) {
                    const downloadLink = document.createElement("a");
                    const blob = that._base64Blob(base64.value, sType);
                    const blobUrl = URL.createObjectURL(blob);
                    downloadLink.href = blobUrl;
                    downloadLink.download = oAttachment.FILE_NAME;
                    downloadLink.click();
                }
            })
        },

        onBeforeRebindList: function (oEvent) {

            var oModel = this.getView().getModel();
            if(oModel.hasPendingChanges()){
                oModel.resetChanges();
            }

            var oParameters = oEvent.getParameter("bindingParams");
            var oComboStatus = this.byId("idStatusMultiComboBox");

            //Filter
            if (oComboStatus) {
                var aKeys = oComboStatus.getSelectedKeys();
                if (aKeys && aKeys.length > 0) {
                    aKeys.forEach(e=>{
                        oParameters.filters.push(new sap.ui.model.Filter(
                            "STATUS",
                            sap.ui.model.FilterOperator.EQ,
                            e
                        ))
                    });
                    
                }
            }
           
        },

    });
});
