sap.ui.define(["umc/app/Controller/BaseController", "sap/m/MessageToast"], function (Controller, MessageToast) {
  "use strict";
  return Controller.extend("umc.app.controller.pch.pch10_quotation_l", {

    onInit: function () {
      //  设置版本号
      this._setOnInitNo("PCH10", "20241029");
      //显示页面
      this.getRouter().getRoute("RouteList_pch10").attachPatternMatched(this._onObjectMatched, this);

      
    },
    // _onObjectMatched: function () {
    //   //获取并设置权限数据
    //   this._setAuthByMenuAndUser("INV34");
    //   //初始化 msg
    //   this.MessageTools._clearMessage();
    //   this.MessageTools._initoMessageManager(this);
    // },

    onRebind: function (oEvent) {
      this._rowNoMap == null;
      let sorts = ["QUO_NUMBER"];
      let ascs = [false]; //true desc false asc
      //手动添加排序
      this._onListRebinSort(oEvent, sorts, ascs);
    },

    onPress: function (oEvent) {
      let oItem = oEvent.getSource();
      let oContext = oItem.getBindingContext();
      let object = oContext.getObject();
      let QUO_NUMBER = object.QUO_NUMBER || "";  // 如果为空，使用 "EMPTY"
      let QUO_ITEM = object.QUO_ITEM || "";
      let SALESNO = object.SALES_NUMBER || "";
      let QUO_V = object.QUO_VERSION || "";

      let headID = QUO_NUMBER + "|" + QUO_ITEM + "|" + SALESNO + "|" + QUO_V;

      this._onPress(oEvent, "RouteView_pch10", headID);
    },

    onResend: function () {
			var that = this;
            var oTable = this.getView().byId("detailTable10");
            var aSelectedIndices = oTable.getSelectedIndices();

            // 检查是否有选中的数据
            if (aSelectedIndices.length === 0) {
                MessageToast.show("選択されたデータがありません、データを選択してください。");
                return;
            }
  
            // 遍历选中的行，提取所需数据
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });  
  

            // 检查 SUPPLIER 是否相同
            var CUSTOMERSet = new Set(aSelectedData.map(data => data.CUSTOMER));
            if (CUSTOMERSet.size > 1) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
                return;
            }

            if (CUSTOMERSet.size === 1) { 
                var H_CODE = "MM0002";
                var CUSTOMER = CUSTOMERSet.values().next().value;
                var entity = "/SYS_T08_COM_OP_D";

                this._readHeademail(H_CODE, CUSTOMER, entity).then((oHeadData) => {
                    let mail = oHeadData.results && oHeadData.results.length > 0 ? 
                        oHeadData.results.map(result => result.VALUE02).join(", ") : '';            
                    let mailobj = {
                        emailJson: {
                            TEMPLATE_ID: "UWEB_M002",
                            MAIL_TO: [mail].join(", "), 
                            MAIL_BODY: [
                                { object: "vendor", value: supplierName },
                            ]
                        }
                    };

                    // 确保 this.getView() 是正确的
                    let newModel = this.getView().getModel("Common");
                    let oBind = newModel.bindList("/sendEmail");

                    oBind.create(mailobj, {
                        success: function (oData) {
                            if (oData && oData.result && oData.result === "sucess") {
                                MessageToast.show("メールが正常に送信されました。");
                            } else {
                                sap.m.MessageToast.show("メール送信に失敗しました。エラー: " + (oData.result || "不明なエラー"));
                            }
                        },
                        error: function (oError) {
                            sap.m.MessageToast.show("メール送信に失敗しました。エラー: " + oError.message);
                        }
                    });
                });

            }
                
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

        _readHeademail: function (a,b, entity) {
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

    onBeforeExport: function (oEvent) {
			var mExcelSettings = oEvent.getParameter("exportSettings");
      
      // 设置文件名
			var oDate = new Date();
			var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
			var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
      mExcelSettings.fileName = `購買見積依頼管理_ ${sDate}${sTime}.xlsx`;
      
			mExcelSettings.workbook.columns.forEach(function (oColumn) {
				switch (oColumn.property) {

				default:
					break;
				}
			});
    },

    // _readHead: function (a,b, entity) {
    //       var that = this;
    //       return new Promise(function (resolve, reject) {
    //         that.getModel().read(entity, {
    //             filters: [
                  
    //             new sap.ui.model.Filter({
    //               path: "SALES_NUMBER",
    //               value1: a,
    //               operator: sap.ui.model.FilterOperator.EQ,
    //             }),
                  
    //           ],
    //           success: function (oData) {
    //             resolve(oData);
    //           },
    //           error: function (oError) {
    //             reject(oError);
    //           },
    //         });
    //       });
    // },
    
  });
});
