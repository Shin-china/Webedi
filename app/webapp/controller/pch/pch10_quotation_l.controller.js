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
      //获取并设置权限数据
      // this._setAuthByMenuAndUser("PCH10");
    },

    onPress: function (oEvent) {
      let oItem = oEvent.getSource();
      let oContext = oItem.getBindingContext();
      let object = oContext.getObject();
      let QUO_NUMBER = object.QUO_NUMBER;
      console.log(QUO_NUMBER);
      this._onPress(oEvent, "RouteView_pch10", QUO_NUMBER);
    },

    // onUpnPrint: function (oEvent) {
    //   this.MessageTools._clearMessage();
    //   //初始化系统配置参数
    //   this.setSysConFig().then((sta) => {
    //     if (sta) {
    //       this.onUpnPrintCom(this, "detailTable", "smartTable");
    //     }
    //   });
    // },

    // sendSapFunction: function (isPost) {
    //   var that = this;
    //   this._setBusy(true);
    //   var sHeaderObject = this.getView().getBindingContext().getObject();
    //   var headId = sHeaderObject.ID;
    //   this.onSendDetailCommon("detailTable", "INV34", "/INV34_GR_SEND", isPost).then((oData) => {
    //     //刷新数据
    //     if (oData.INV34_GR_SEND === "success") {
    //       //that._bindViewDataByCreateKey(headId, _objectCommData._entity, _objectCommData._itmes, true, "smdetailTable");
    //       that.getModel().refresh(true); //刷新数据
    //       this.byId("detailTable").clearSelection();
    //       //that.MessageTools._addMessage(that.MessageTools._getI18nText("MSG_SUCCESS_SEND", that.getView()), null, 3, that.getView());
    //     }
    //     that._setBusy(false);
    //   });
    // },
    // /**
    //  * 过账，取消过账
    //  * @param {} oEvent
    //  */

    onSend: function (oEvent) {

      var that = this;
      that._setBusy(true);

      let selectedData = this._getSelectedIndicesDatasByTable("detailTable10");
      if (selectedData.length == 0) {
        MessageToast.show("選択されたデータがありません、データを選択してください。");
        return false;
      }


      var sal_Numset = new Set(selectedData.map(data => data.SALES_NUMBER));
      if (sal_Numset.size != 0) {

        var Sal_Num = sal_Numset.values().next().value;
        var entity = "/PCH_T06_QUOTATION_H";

        this._readHead(Sal_Num, null, entity).then(oHeadData => {

          let H_status = oHeadData.results[0].STATUS;

          if (H_status == "5") {

            MessageToast.show("購買見積番号" + Sal_Num + "に対して、ステータスは完了となりましたので、送信できません。");
          
            return false;

          }
          

          var pList = Array();
          selectedData.forEach(odata => {
            var p = {

              Quo_No: odata.SALES_NUMBER

            };

            pList.push(p);
              
            })

            this._callCdsAction("/PCH10_GR_SEND",  { params: JSON.stringify(pList) }, this).then(oData => {


            });


          
        });

  
      };
      



      
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
				// case "SalesOrderDate":
				// 	oColumn.type = sap.ui.export.EdmType.Date;
				// 	break;
				// case "NetAmount":
				// 	oColumn.type = sap.ui.export.EdmType.Currency;
				// 	oColumn.unitProperty = 'TransactionCurrency';
				// 	oColumn.delimiter = true; //'true':display thousands separators,'false'/'default':no display
				// 	oColumn.width = 20;
				// 	break;
				default:
					break;
				}
			});
    },

    _readHead: function (a,b, entity) {
          var that = this;
          return new Promise(function (resolve, reject) {
            that.getModel().read(entity, {
                filters: [
                  
                new sap.ui.model.Filter({
                  path: "SALES_NUMBER",
                  value1: a,
                  operator: sap.ui.model.FilterOperator.EQ,
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
