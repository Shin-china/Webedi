sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch03_pobk_l", {
		formatter : formatter,

		onInit: function () {
			this.getView().unbindElement();
			const oTable = this.byId("detailTable");
			// oTable.setSelectionMode("None");
			//  设置版本号
			this._setOnInitNo("MST01");
			this.MessageTools._clearMessage();
			this.MessageTools._initoMessageManager(this);

		},
	
		// onPress: function (oEvent) {
		// var oItem = oEvent.getSource();
		// var oContext = oItem.getBindingContext();
		// this._onPress(oEvent, "RouteEdit_sys01", oContext.getObject().ID);
		// },

		onRebind: function (oEvent) {
		// this._onListRebindDarft(oEvent);
		// this._onListRebindDarft(oEvent, true);
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

		onPrint: function () {
			var that = this;
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			var IdList = that._TableDataList("detailTable",'ID')
			if(IdList){
				that.PrintTool._getPrintDataInfo(that,IdList,"/PCH_T03_PO_ITEM_PRINT","ID").then((oData)=>{
					let sResponse = json2xml(oData, options);
                    console.log(sResponse)
					that.setSysConFig().then(res => {
						that.PrintTool._detailSelectPrint(that,sResponse, "test/test", oData,null,null,null,null)

					})
				})
			}


        }
	});
});