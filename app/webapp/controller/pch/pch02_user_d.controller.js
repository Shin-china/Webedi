sap.ui.define([
	"umc/app/controller/BaseController",
	"sap/ui/model/odata/v2/ODataModel",
	"sap/m/MessageBox"
], function(
	Controller,
	ODataModel,
	MessageBox
) {
	"use strict";

	return Controller.extend("umc.app.controller.pch.pch02_user_d", {
		// onInit: function() {
		// 	// 设置默认 OData 模型
		// 	// this._setCreateAble(false);
		// 	this._setDefaultDataModel("TableService");

		// 	// 清除消息
		// 	this.MessageTools._clearMessage();
		// },

		// onSearch :function(oEvent) {
		// 	console.log('11111')
		// },

		// onRebind:function(oEvent) {
		// 	console.log('11111')
		// },
		

	});
});
