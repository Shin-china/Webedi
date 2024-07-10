sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/core/routing/History",
    "sap/ui/core/UIComponent",
    "umc/app/model/formatter",
    "sap/ui/core/Fragment",
    "sap/ui/model/odata/v2/ODataModel",
], function(
	Controller, History, UIComponent, formatter, Fragment, ODataModel
) {
	"use strict";

	return Controller.extend("umc.app.controller.BaseController", {
        formatter:formatter,

        navTo: function (psTarget, pmParameters, pbReplace) {
            this.getRouter().navTo(psTarget, pmParameters, pbReplace);
        },
        getRouter: function () {
            return UIComponent.getRouterFor(this);
        },
        _setDefaultDataModel:function(_mName){
            this.setModel(this.getModel(_mName), "defaultModel");
        },
        setModel:function(oModel,sName){
            return this.getView().setModel(oModel,sName);
        },
        getModel:function(sName){
            var jsonModel = this.getView().getModel(sName);
            if(!jsonModel){
                jsonModel = new sap.ui.model.json.JSONModel();
                this.getView().setModel(jsonModel,sName);
            }
            return jsonModel;
        },
      /**
       *  一栏画面跳转到明细页面
       * @param {*} oEvent
       * @param {*} _navTo  要跳转路径
       * @param {*} _infoId  参数
       */
        _onPressNav:function(oEvent,_navTo,_infoId){
            this.getRouter().navTo(_navTo, { headId: _infoId });
        }

	});
});