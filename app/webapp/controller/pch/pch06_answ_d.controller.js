sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch06_answ_d", {
		formatter : formatter,

		onInit: function () {
			// 设置自己的 OData模型为默认模型
			this._setDefaultDataModel("TableService");
			//  设置版本号
			this._setOnInitNo("PCH01", ".20240812.01");
			this.MessageTools._clearMessage();
			this.MessageTools._initoMessageManager(this);

			this.getRouter().getRoute("RouteCre_pch06").attachPatternMatched(this._onRouteMatched, this);
		},
		/*==============================
		删除
		==============================*/
		onDelete: function (oEvent) {
			
			this._setEditable(true);
		},	
		/*==============================
		复制
		==============================*/
		onCop: function (oEvent) {
			var that = this;
			var selectedIndices = this._TableList("detailTable"); // 获取选中行
			

			
			this._setEditable(true);
		},
		/*==============================
		编辑
		==============================*/
		onEdi: function (oEvent) {
			
			this._setEditable(true);
		},
		/*==============================
		保存
		==============================*/
		onSav: function (oEvent) {
			
			this._setEditable(false);
		},
		/*==============================
		==============================*/
		_onRouteMatched: function (oEvent) {
			this._setEditableAuth(true);
			this._setEditable(false);
		},
		onRebind: function (oEvent) {

		// this._onListRebindDarft(oEvent, true);
		},
	
		onBeforeExport: function (oEvt) {
		var mExcelSettings = oEvt.getParameter("exportSettings");
		for (var i = 0; i < mExcelSettings.workbook.columns.length; i++) {

			}
	
		},
	
	});
});