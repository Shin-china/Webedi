sap.ui.define([
	"umc/app/controller/BaseController",
	"sap/ui/model/odata/v2/ODataModel"
], function(
	Controller,
	ODataModel
) {
	"use strict";

	return Controller.extend("umc.app.controller.sys.sys01_user_l", {
		onInit:function(){
      		// 设置自己的 OData模型为默认模型
			this._setDefaultDataModel("TableService");

		},
		onPress:function(oEvent){
			var oItem = oEvent.getSource();
			var oContext = oItem.getBindingContext();
			var test = oContext.getObject().ID;
			this._onPressNav(oEvent,"RouteEdit_sys01",oContext.getObject().ID);
		}
	});
});