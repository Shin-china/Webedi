sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch01_pocf_d", {
		formatter : formatter,

		onInit: function () {	
			this._LocalData = this.getOwnerComponent().getModel("local");
			this._oDataModel = this.getOwnerComponent().getModel();
			this._ResourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
			this._oDataModel.attachBatchRequestCompleted(function(oEvent){
				this.setBusy(false);
				var errors = this._LocalData.getProperty("/errors");
				if(errors){
				}
				this._LocalData.setProperty("/errors", "");
			}.bind(this));
		},
		
		setBusy: function(busy){
			this._LocalData.setProperty("/busy", busy, false);
		},
		
		onExport: function () {
			// 根据id值获取table 
			var oTable = this.getView().byId("table");
			// 获取table的绑定路径
			var sPath = oTable.getBindingPath("rows");
			// 获取table数据
			var aExcelSet = this._LocalData.getProperty(sPath);

			var aExcelCol = [];
			// 获取table的columns
			var aTableCol = oTable.getColumns();
			for (var i = 1; i < aTableCol.length; i++) {
				if (aTableCol[i].getVisible()) {
					var sLabelText = aTableCol[i].getAggregation("label").getText();
					var sType = "string";
					// if (sLabelText == this._ResourceBundle.getText("DocumentDate") || sLabelText == this._ResourceBundle.getText("PostingDate") || 
					// 	sLabelText == this._ResourceBundle.getText("ValueDate")){
					// 	sType = "Date";
					// } else {
					// 	sType = "string";
					// }
					var oExcelCol = {
						// 获取表格的列名，即设置excel的抬头
						label: sLabelText,
						// 数据类型，即设置excel该列的数据类型
						type: sType,
						// 获取数据的绑定路径，即设置excel该列的字段路径
						property: aTableCol[i].getAggregation("template").getBindingPath("text"),
						// 获取表格的width属性，即设置excel该列的长度
						width: parseFloat(aTableCol[i].getWidth())
					};
					aExcelCol.push(oExcelCol);
				}
			}
			// 设置excel的相关属性
			var oSettings = {
				workbook: {
					columns: aExcelCol,
					context: {
						version: "1.54",
						hierarchyLevel: "level"
					}
				},
				dataSource: aExcelSet, // 传入参数，数据源
				fileName: "Export_" + this._ResourceBundle.getText("title") + new Date().getTime() + ".xlsx" // 文件名，需要加上后缀
			};
			// 导出excel
			new Spreadsheet(oSettings).build();
		}
	});
});