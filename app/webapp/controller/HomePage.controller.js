sap.ui.define([
    "umc/app/controller/BaseController",
    "sap/ui/model/json/JSONModel",
    "sap/base/i18n/ResourceBundle",
    "sap/ui/model/resource/ResourceModel",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator",
    "sap/ui/core/UIComponent"
],
    /**
     * @param {typeof sap.ui.core.mvc.Controller} Controller
     */
    function (Controller,
	JSONModel,
	ResourceModel,
	Filter,
	ResourceBundle,FilterOperator,UIComponent) {
        "use strict";

        return Controller.extend("umc.app.controller.HomePage", {


              onInit: function () {
                var that = this;
                this.getView().unbindElement();
                // oTable.setSelectionMode("None");
                this.MessageTools._clearMessage();
          
                that.getRouter().getRoute("RouteMainView").attachPatternMatched(that._onRouteMatched, that);
                //  设置版本号
                this._setOnInitNo("PCH03", ".202512250123");
              },

              homeOnPress: function (oEvent) {
                //功能跳转
                var oItem = oEvent.getSource();
                var oContext = oItem.getBindingContext();
                var obj = oContext.getObject();
                this.getRouter().navTo(obj.MENU_ROUTE_ID, {}, true);
              },

              _onRouteMatched: function (oEvent) {
                // 整理参数 传递后台
               
                // 调用后台
                this._readEntryData("/USER_CODE").then((oData) => {
                  
                  // 测试结果赋值到页面
                 
                  this.byId("_IDGenLabel3").setText(oData.USER_CODE.user);
                });
              },
        });
    });
