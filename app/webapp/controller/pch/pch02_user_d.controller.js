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

            this._oDataModel.attachBatchRequestCompleted(function (oEvent) {
                this.setBusy(false);
                var errors = this._LocalData.getProperty("/errors");
                if (errors) {
                    // 处理错误
                }
                this._LocalData.setProperty("/errors", "");
            }.bind(this));
        },
        
        setBusy: function (busy) {
            if (this._LocalData) { // 检查 _LocalData 是否已初始化
                this._LocalData.setProperty("/busy", busy);
            } else {
                console.error("Local data model is not initialized.");
            }
        },        

        // setBusy: function (busy) {
        //     this._LocalData.setProperty("/busy", busy);
        // },

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

        // onBeforeExport: function (oEvent) {
        //     var oTable = this.getView().byId("detailTable");
        //     var aSelectedIndices = oTable.getSelectedIndices();

        //     if (aSelectedIndices.length === 0) {
        //         sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください。")); // 提示未选择数据
        //         oEvent.preventDefault(); // 取消导出操作
        //         return;
        //     }

        //     var oSettings = oEvent.getParameter("exportSettings");
        //     if (oSettings) {
        //         console.log("onBeforeExport called");
        //         console.log("Export Settings:", oSettings);
        //         var oDate = new Date();
        //         var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
        //         var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
        //         // 设置文件名为当前日期和时间
        //         oSettings.fileName = `納期回答照会_${sDate}${sTime}.xlsx`;
        //     }
        // },

        onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();
        
            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください。")); // 提示未选择数据
                oEvent.preventDefault(); // 取消导出操作
                return;
            }
        
            var oSettings = oEvent.getParameter("exportSettings");
            if (oSettings) {
                console.log("onBeforeExport called");
                console.log("Export Settings:", oSettings);
        
                // 设置文件名为当前日期和时间
                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
                oSettings.fileName = `納期回答照会_${sDate}${sTime}.xlsx`;
        
                // 获取选中的行数据
                var aSelectedRows = aSelectedIndices.map(function(index) {
                    return oTable.getContextByIndex(index).getObject();
                });
        
                console.log("Selected Rows:", aSelectedRows); // 调试输出
        
                // 确保 aSelectedRows 是有效的数组
                if (Array.isArray(aSelectedRows)) {
                    // 直接遍历并格式化 DELIVERY_DATE 字段
                    aSelectedRows.forEach(function(row) {
                        if (row.DELIVERY_DATE) {
                            var date = new Date(row.DELIVERY_DATE);
                            row.DELIVERY_DATE = this.formatDate(date); // 格式化日期为 'yyyy/MM/dd'
                        }
                    }, this);
        
                    // 将处理后的数据直接赋值给 oSettings.data
                    oSettings.data = aSelectedRows; // 设置导出的数据
                } else {
                    console.error("No selected rows found.");
                }
            }
        },        
        
        formatDate: function(date) {
            var year = date.getFullYear();
            var month = (date.getMonth() + 1).toString().padStart(2, '0'); // 月份从0开始
            var day = date.getDate().toString().padStart(2, '0');
            return year + '/' + month + '/' + day; // 返回格式为 'yyyy/MM/dd'
        },
        

        onResend: function () {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください")); // 提示未选择数据
                return;
            }

            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });

            // 检查是否有 STATUS 为 "2.反映済" 的数据
            var bHasReflected = aSelectedData.some(function (oData) {
                return oData.STATUS === "2";
            });

            if (bHasReflected) {
                sap.m.MessageToast.show("反映済された情報なので、再送信できません。"); // 显示错误消息
                return; // 终止后续操作
            }

            //调用po接口
            this._invoPo(aSelectedData);

    },


    });
});
