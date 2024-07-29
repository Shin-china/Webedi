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
              this.MessageTools._clearMessage();
              //this.MessageTools._initoMessageManager(this);

              },
              homeOnPress: function (oEvent) {
                //功能跳转
                var oItem = oEvent.getSource();
                var oContext = oItem.getBindingContext();
                var obj = oContext.getObject();
                this.getRouter().navTo(obj.MENU_ROUTE_ID, {}, true);
              }
        });
    });
