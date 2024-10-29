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
      let ID = object.QUO_NUMBER;
      console.log(ID);
      this._onPress(oEvent, "RouteView_pch10", ID);
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
    // onSend: function (oEvent) {
    //   this.MessageTools._clearMessage();
    //   this.onSendDetailCommon("detailTable", "INV34", "/INV34_GR_SEND", true, "smartTable",false);
    // },
  });
});
