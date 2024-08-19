sap.ui.define([
	"umc/app/controller/BaseController",
	"sap/ui/model/odata/v2/ODataModel",
], function(
	Controller,
	ODataModel
) {
	"use strict";

	return Controller.extend("umc.app.controller.sys.sys05_mail_l", {

		/**
		 * @override
		 */
		onInit: function() {
			this._setCreateAble(false);
			this._setDefaultDataModel("TableService");

			//Clear Message
			this.MessageTools._clearMessage();
		},

		onPress:function(oEvent){
			var oItem = oEvent.getSource();
			var oContext = oItem.getBindingContext();
			var test = oContext.getObject().TEMPLATE_ID;
			this._onPressNav(oEvent,"RouteEdit_sys05",oContext.getObject().TEMPLATE_ID);
		},
		onCreate:function(oEvent){
			var oItem = oEvent.getSource();
			this._onPressNav(oEvent,"RouteCre_sys05");
		}
	});
});