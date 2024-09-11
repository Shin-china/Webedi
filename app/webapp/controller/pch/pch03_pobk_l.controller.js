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
		_csvdow:function(oEvt){
			// 假设你的数据模型是JSONModel，并且已经绑定到了SmartTable  
			var selectedIndices = this._TableList("detailTable");
			if(selectedIndices){
				// 构建CSV内容  
				var csvContent = "data:text/csv;charset=utf-8,";  
				var headers = Object.keys(selectedIndices[0]); // 假设所有条目的结构都相同，取第一条的键作为表头  
				// csvContent += headers.join(",") + "\n";  
				
				selectedIndices.forEach(function(row) {  
					var values = headers.map(function(header) {
						if(header == "__metadata")  {
							return "__metadata";
						}
						return (row[header] === null || row[header] === undefined) ? "" : '"' + row[header] + '"';  
					});  
					if(values != "__metadata")
					csvContent += values.join(",") + "\n";  
				});  
				
				// 触发下载  
				var encodedUri = encodeURI(csvContent);  
				var link = document.createElement("a");  
				link.setAttribute("href", encodedUri);  
				link.setAttribute("download", "data.csv");  
				document.body.appendChild(link);  
				link.click();
			}

		},
		// onCreate: function (oEvent) {
		// var oItem = oEvent.getSource();
		// var oContext = oItem.getBindingContext();
		// this._onPress(oEvent, "RouteCre_sys01");
		// },
		onBeforeExport: function (oEvt) {
			var mExcelSettings = oEvt.getParameter("exportSettings");

			// Disable Worker as Mockserver is used in Demokit sample
			mExcelSettings.worker = false;
		},
		onEmail: function (oEvt) {
			this._callCdsAction("PCH03_SENDEMAIL","",this).then((oData)=>{
				console.log(oData.PCH03_SENDEMAIL);
			});
		},
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
						that.PrintTool._detailSelectPrintDow(that,sResponse, "test/test", oData,null,null,null,null)
					})
				})
			}


        }
	});
});