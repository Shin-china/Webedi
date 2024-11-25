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
            var aExport = [];
            aSelectedData.forEach(row => {
                if (sKeys === "") {
                    sKeys = row.QUO_NUMBER + '-' + row.QUO_ITEM;
                } else {
                    sKeys = sKeys + "," + row.QUO_NUMBER + '-' + row.QUO_ITEM;
                }
            })

            var that = this;
            var params = { param: sKeys };
            this._callCdsAction("/PCH08_SHOW_DETAIL", params, this).then((oData) => {
                let json = JSON.parse(oData.PCH08_SHOW_DETAIL);
                console.log(json)
                sResult = json;

                //拼接动态对象
                let iMaxPersonSize = 0, iMaxQtySize = 0, iMaxPersonSizeTotal = 0, iMaxQtySizeTotal = 0;
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
                        let iMaxPersonSize = item["PERSON_SIZE"];
                        if (!iMaxPersonSize) {
                            return;
                        }
                        if (iMaxPersonSize > iMaxPersonSizeTotal) {
                            iMaxPersonSizeTotal = iMaxPersonSize;
                        }

                        for (var i = 1; i <= iMaxPersonSize; i++) {
                            var sPerson = 'PERSON_' + i;
                            oExportData[sPerson] = item[sPerson];
                        }

                        let iMaxQtySize = item["QUANTITY_SIZE"];
                        if (!iMaxQtySize) {
                            return;
                        }

                        if (iMaxQtySize > iMaxQtySizeTotal) {
                            iMaxQtySizeTotal = iMaxQtySize;
                        }

                        for (var i = 1; i <= iMaxQtySize; i++) {
                            var sQty = 'QTY_' + i;
                            oExportData[sQty] = item[sQty];

                            var sPrice = 'PRICE_' + i;
                            oExportData[sPrice] = item[sPrice];
                        }
                    })

                    aExport.push(oExportData);
                });

                var aColumns = oTable.getColumns().map(function (oColumn) {
                    var sDateFormat = 'yyyy-MM-dd';
                    var sFormat = '';
                    var sType = '';
                    var sLabel = '';

                    var property = oColumn.getTemplate().getBindingPath("text") || oColumn.getTemplate().mBindingInfos.value.parts[0].path;

                    if (property === 'VALIDATE_START' || property === 'VALIDATE_END') {
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
                for (var i = 1; i <= iMaxPersonSizeTotal; i++) {
                    var sPerson = 'PERSON_' + i;
                    aColumns.push({
                        label: "員数_" + i,
                        type: 'string',
                        property: sPerson,
                        width: 13,
                        format: ''
                    });
                }

                for (var i = 1; i <= iMaxQtySizeTotal; i++) {
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
                        sap.m.MessageToast.show(that._PchResourceBundle.getText("EXPORT_FINISHED"));
                    })
                    .finally(function () {
                        oSheet.destroy();
                    });
            }
            );
        },

        onDownloadTemplate: function () {
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
            aSelectedData.forEach(row => {
                if (sKeys === "") {
                    sKeys = row.QUO_NUMBER + '-' + row.QUO_ITEM;
                } else {
                    sKeys = sKeys + "," + row.QUO_NUMBER + '-' + row.QUO_ITEM;
                }
            })

            var that = this;
            var params = { param: sKeys };
            this._callCdsAction("/PCH08_DOWNLOAD_TEMPLATE", params, this).then((oData) => {
                let json = JSON.parse(oData.PCH08_DOWNLOAD_TEMPLATE);
                console.log(json)

                var aColumns = [
                    { label: '購買見積番号', property: 'QUO_NUMBER', type: 'string', width: 15 },
                    { label: '管理No', property: 'QUO_ITEM', type: 'string', width: 5 },
                    { label: 'NO', property: 'NO', type: 'string', width: 5 },
                    { label: '販売見積番号', property: 'SALES_NUMBER', type: 'string', width: 15 },
                    { label: '販売見積案件明細', property: 'SALES_D_NO', type: 'string', width: 5 },
                    { label: '併記有無', property: 'REFRENCE_NO', type: 'string', width: 15 },
                    { label: '客先', property: 'CUSTOMER', type: 'string', width: 15 },
                    { label: '機種', property: 'MACHINE_TYPE', type: 'string', width: 15 },
                    { label: '製品数量', property: 'QUANTITY', type: 'string', width: 15 },
                    { label: 'アイテム', property: 'ITEM', type: 'string', width: 5 },
                    { label: '時期', property: 'TIME', type: 'string', width: 15 },
                    { label: '量産場所', property: 'LOCATION', type: 'string', width: 15 },
                    { label: '有効開始日付', property: 'VALIDATE_START', type: 'string', width: 15 },
                    { label: '有効終了日付', property: 'VALIDATE_END', type: 'string', width: 15 },
                    { label: 'SAP品番(任意)', property: 'MATERIAL_NUMBER', type: 'string', width: 15 },
                    { label: '顧客品番', property: 'CUST_MATERIAL', type: 'string', width: 15 },
                    { label: 'メーカー品番', property: 'MANUFACT_MATERIAL', type: 'string', width: 15 },
                    { label: 'カスタム品図面 仕様添付', property: 'Attachment', type: 'string', width: 15 },
                    { label: '品名', property: 'Material', type: 'string', width: 15 },
                    { label: 'メーカー', property: 'MAKER', type: 'string', width: 15 },
                    { label: '仕入先連絡先', property: 'UWEB_USER', type: 'string', width: 15 },
                    { label: 'SAP BP', property: 'BP_NUMBER', type: 'string', width: 15 },
                    { label: '依頼品判定', property: 'YLP', type: 'string', width: 15 },
                    { label: '正式メーカ品番', property: 'MANUL', type: 'string', width: 15 },
                    { label: 'Manfact. Code name', property: 'MANUFACT_CODE', type: 'string', width: 15 },
                    { label: '客先型番', property: 'CUSTOMER_MMODEL', type: 'string', width: 15 },
                    { label: '中区分', property: 'MID_QF', type: 'string', width: 15 },
                    { label: '小区分', property: 'SMALL_QF', type: 'string', width: 15 },
                    { label: 'その他区分', property: 'OTHER_QF', type: 'string', width: 15 },
                    { label: '通貨', property: 'CURRENCY', type: 'string', width: 15 },
                    { label: '数量', property: 'QTY', type: 'string', width: 15 },
                    { label: '単価', property: 'PRICE', type: 'string', width: 15 },
                    { label: '員数', property: 'PERSON_NO1', type: 'string', width: 15 },
                    { label: '価格有効日', property: 'PRICE_CONTROL', type: 'string', width: 15 },
                    { label: 'LT(日数)', property: 'LEAD_TIME', type: 'string', width: 15 },
                    { label: 'MOQ', property: 'MOQ', type: 'string', width: 15 },
                    { label: '単位', property: 'UNIT', type: 'string', width: 5 },
                    { label: 'SPQ', property: 'SPQ', type: 'string', width: 15 },
                    { label: '梱包形態', property: 'KBXT', type: 'string', width: 15 },
                    { label: '製品重量(g)', property: 'PRODUCT_WEIGHT', type: 'string', width: 15 },
                    { label: '原産国', property: 'ORIGINAL_COU', type: 'string', width: 15 },
                    { label: 'EOL予定', property: 'EOL', type: 'string', width: 15 },
                    { label: '投資促進制度', property: 'ISBOI', type: 'string', width: 15 },
                    { label: 'Incoterms 1（インコタームズ）', property: 'Incoterms', type: 'string', width: 15 },
                    { label: 'Incoterms 1（納入場所）', property: 'Incoterms_Text', type: 'string', width: 15 },
                    { label: '備考１', property: 'MEMO1', type: 'string', width: 15 },
                    { label: '備考2', property: 'MEMO2', type: 'string', width: 15 },
                    { label: '備考3', property: 'MEMO3', type: 'string', width: 15 },
                    { label: '商流', property: 'SL', type: 'string', width: 15 },
                    { label: '同値', property: 'TZ', type: 'string', width: 15 },
                    { label: '代替品番', property: 'RMATERIAL', type: 'string', width: 15 },
                    { label: '代替品番の通貨', property: 'RMATERIAL_CURRENCY', type: 'string', width: 15 },
                    { label: '代替品番の単価', property: 'RMATERIAL_PRICE', type: 'string', width: 15 },
                    { label: '代替品番のLT(日数)', property: 'RMATERIAL_LT', type: 'string', width: 15 },
                    { label: '代替品番のMOQ', property: 'RMATERIAL_MOQ', type: 'string', width: 15 },
                    { label: '代替品番の梱包形態', property: 'RMATERIAL_KBXT', type: 'string', width: 15 },
                    { label: 'UMC購買コメント１', property: 'UMC_COMMENT_1', type: 'string', width: 15 },
                    { label: 'UMC購買コメント２', property: 'UMC_COMMENT_2', type: 'string', width: 15 },
                    { label: '仕入先品目コード', property: 'SUPPLIER_MAT', type: 'string', width: 15 }
                ];

                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');

                var oSettings = {
                    dataSource: json,
                    workbook: {
                        columns: aColumns
                    },
                    fileName: `購買見積回答_${sDate}${sTime}.xlsx`
                };

                var oSheet = new sap.ui.export.Spreadsheet(oSettings);
                oSheet.build()
                    .then(function () {
                        sap.m.MessageToast.show(that._PchResourceBundle.getText("EXPORT_FINISHED"));
                    })
                    .finally(function () {
                        oSheet.destroy();
                    });
            }
            );
        },

        onEdit: function () {
            this._localModel.setProperty("/show", false);
            this._localModel.setProperty("/save", true);
        },

        __formatDate: function (date) {
            if (!date) {
                return '';
            }

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

            //因为http param 长度有限制，必须循环一条条调用，不要一次性提交
            var oPostData = {}, oPostList = {}, aPostItem = [], iDoCount = 0, bError;
            aSelectedIndices.forEach(iIndex => {
                aPostItem = [];
                oPostData = {};
                oPostList = {};

                var oItem = oTable.getContextByIndex(iIndex).getObject();
                oItem.VALIDATE_START = that.__formatDate(oItem.VALIDATE_START);
                oItem.VALIDATE_END = that.__formatDate(oItem.VALIDATE_END);
                //oItem.TIME = that.__formatDate(oItem.TIME);

                aPostItem.push(oItem);

                oPostList = JSON.stringify({
                    "list": aPostItem
                });

                oPostData = {
                    "str": oPostList
                };




                that._callCdsAction("/PCH08_SAVE_DATA", oPostData, that).then(

                    (oData) => {

                        iDoCount++;
                        var oResult = JSON.parse(oData.PCH08_SAVE_DATA);

                        if (oResult.err) {
                            bError = true;
                        }

                        if (iDoCount === aSelectedIndices.length) {
                            if (bError) {
                                that._localModel.setProperty("/show", false);
                                that._localModel.setProperty("/save", true);
                                that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
                            } else {
                                that._localModel.setProperty("/show", true);
                                that._localModel.setProperty("/save", false);
                                sap.m.MessageToast.show(that._PchResourceBundle.getText("SAVE_SUCCESS"));
                            }
                            that._setBusy(false);
                        }

                    });

            });

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

        getDetail: function (oEvent) {

            let that = this;
            let selectedData = this._getSelectedIndicesDatasByTable("listTable");
            if (selectedData.length == 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
                return false;
            }
            let para = [];
            selectedData.forEach(item => {
                let key = item.QUO_NUMBER + '-' + item.QUO_ITEM;
                para.push(key);
            });
            this._onPress(oEvent, "RouteView_pch08", this.unique(para).join(","));
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
            let sObject = sQuoNumber + '_' + sQuoItem;

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
                let sFilter = sQuoNumber + '_' + sQuoItem;
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
            if (oModel.hasPendingChanges()) {
                oModel.resetChanges();
            }

            var oParameters = oEvent.getParameter("bindingParams");
            var oComboStatus = this.byId("idStatusMultiComboBox");

            //Filter
            if (oComboStatus) {
                var aKeys = oComboStatus.getSelectedKeys();
                if (aKeys && aKeys.length > 0) {
                    aKeys.forEach(e => {
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
