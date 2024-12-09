sap.ui.define([
    "umc/app/Controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Controller,A, MessageToast, MessageBox) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch09_forcast_d", {

        onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
                oEvent.preventDefault(); // 取消导出操作
                return;
            }

            var oSettings = oEvent.getParameter("exportSettings");

            oSettings.workbook.columns.forEach(function (oColumn) {

                switch (oColumn.property) {
                    
				case "ARRANGE_START_DATE":
					oColumn.type = sap.ui.export.EdmType.Date;
                        break;
                    
                case "ARRANGE_END_DATE":
                        oColumn.type = sap.ui.export.EdmType.Date;
                            break;
                    
				default:
					break;
				}
            });
            
            if (oSettings) {
                console.log("onBeforeExport called");
                console.log("Export Settings:", oSettings);
                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
                // 设置文件名为当前日期和时间
                oSettings.fileName = `FCレポート_${sDate}${sTime}.xlsx`;
            }
        },

        onResend: function () {
            var that = this;
            that.getView().setBusy(true);

            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            // 检查是否有选中的数据
            if (aSelectedIndices.length === 0) {
                MessageToast.show("選択されたデータがありません、データを選択してください。");
                return;
            }

            var oModel = this.getView().getModel();
            var oCommonModel = this.getView().getModel("Common"); // 获取公共模型
            var aEmailParams = [];
            var mail = "";

            // 遍历选中的行，提取所需数据
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });  
  

            // 检查 SUPPLIER 是否相同
            var supplierSet = new Set(aSelectedData.map(data => data.SUPPLIER));
            if (supplierSet.size > 1) {
                MessageBox.error("複数の仕入先がまとめて配信することができませんので、1社の仕入先を選択してください。");
                return;
            }


            var confirmMsg = "送信しますか？";

     
            sap.m.MessageBox.confirm(confirmMsg, {
               
                actions: ["YES", "NO"],
               
                emphasizedAction: "YES",
                
                onClose: function (sAction) {
                 
                
                    if (sAction == "YES") {

                        if (supplierSet.size === 1) { 
                    
                            var H_CODE = "MM0001";
                            var SUPPLIER = supplierSet.values().next().value;
                            var entity = "/SYS_T08_COM_OP_D";

                    
                            that._readHead(H_CODE, SUPPLIER, entity).then((oHeadData) => {
                                let mail = oHeadData.results && oHeadData.results.length > 0 ? 
                                    oHeadData.results.map(result => result.VALUE02).join(", ") : '';
                                let absama = oHeadData.results && oHeadData.results.length > 0 ? 
                                    oHeadData.results.map(result => result.VALUE03 + " 様").join("  ") : '';
                    
                                let mailobj = {
                                    emailJson: {
                                        TEMPLATE_ID: "UWEB_M001",
                                        MAIL_TO: [mail].join(", "), 
                                        MAIL_BODY: [
                                            { object: "vendor", value: supplierName },
                                            { object: "yyyy", value: year },
                                            { object: "mm", value: month },
                                            { object: "absama", value: absama }
                                        ]
                                    }
                                };
           
                                that._sendEmail(mailobj); 
                                that.getView().setBusy(false);

                            });
                        }

                    } else {
                        that.getView().setBusy(false);
                        return;
                    }
                }
            });    
                
            // 假设您在这里定义邮件内容模板
            var supplierName = "";
            var year = "";
            var month = "";

            aSelectedData.map(function (data) {
                supplierName = data.NAME1 || "未指定"; // 默认值
                
                // 使用当前年月
                var currentDate = new Date();
                year = currentDate.getFullYear().toString(); // 当前年份
                month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // 当前月份，确保是两位数格式
            });
        },

        _readHead: function (a,b, entity) {
          var that = this;
          return new Promise(function (resolve, reject) {
            that.getModel().read(entity, {
                filters: [
                  
                new sap.ui.model.Filter({
                  path: "H_CODE",
                  value1: a,
                  operator: sap.ui.model.FilterOperator.EQ,
                }),
                new sap.ui.model.Filter({
                  path: "VALUE01",
                  value1: b,
                  operator: sap.ui.model.FilterOperator.EQ,
                }),

                new sap.ui.model.Filter({
                  path: "DEL_FLAG",
                  value1: "X",
                  operator: sap.ui.model.FilterOperator.NE,
                }),

              ],
              success: function (oData) {
                resolve(oData);
              },
              error: function (oError) {
                reject(oError);
              },
            });
          });
        },
        
    });
});
