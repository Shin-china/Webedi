sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch02_user_l", {
		formatter : formatter,

		onInit: function () {
		// 设置自己的 OData模型为默认模型
		this._setDefaultDataModel("TableService");
		//  设置版本号
		this._setOnInitNo("PCH02", ".20240812.01");
		// 设置选中框 无
		// this._setSelectionNone("detailTable");
		// this._setEditableAuth(true);
		},

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
		
	});
});