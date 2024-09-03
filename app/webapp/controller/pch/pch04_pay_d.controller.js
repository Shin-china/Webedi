sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch04_payment_d", {
		formatter : formatter,

		onInit: function () {

		this._setOnInitNo("PCH04", ".20240812.01");

		},
	
		// onPress: function (oEvent) {
		// var oItem = oEvent.getSource();
		// var oContext = oItem.getBindingContext();
		// this._onPress(oEvent, "RouteEdit_sys01", oContext.getObject().ID);
		// },

		onRebind: function (oEvent) {
		// this._onListRebindDarft(oEvent);
		this._onListRebindDarft(oEvent, true);
		},
	
		onBeforeExport: function (oEvt) {
		var mExcelSettings = oEvt.getParameter("exportSettings");
		for (var i = 0; i < mExcelSettings.workbook.columns.length; i++) {
			// this.CommTools._setExcelFormatDate(mExcelSettings, i, "VALID_DATE_FROM");
			// this.CommTools._setExcelFormatDate(mExcelSettings, i, "VALID_DATE_TO");
			// this.CommTools._setExcelFormatDateTime(mExcelSettings, i, "CD_TIME");
			// this.CommTools._setExcelFormatDateTime(mExcelSettings, i, "UP_TIME");
			// this.CommTools._setExcelFormatDateTime(mExcelSettings, i, "LAST_LOGIN_TIME");
			}
	
		},
		
		// onCreate: function (oEvent) {
		// var oItem = oEvent.getSource();
		// var oContext = oItem.getBindingContext();
		// this._onPress(oEvent, "RouteCre_sys01");
		// },
	
	});
});