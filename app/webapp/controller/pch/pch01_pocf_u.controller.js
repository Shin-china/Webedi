sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, xlsx, formatter, 
	 Spreadsheet) {
	"use strict";

	return Controller.extend("umc.app.controller.pch.pch01_pocf_u", {
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
			// 绑定模板附件path
			var oDownCollection = this.byId("DownCollection");
			var sPath = "Attach>/A_DocumentInfoRecordAttch(DocumentInfoRecordDocType='INF'," 
					  + "DocumentInfoRecordDocNumber='WASTESHIPPINGUPLOAD'," 
					  + "DocumentInfoRecordDocVersion='00'," 
					  + "DocumentInfoRecordDocPart='000')";
			// oDownCollection.bindElement(sPath);
			
		},
		
		onFileChange: function (oEvent) {
			/*global XLSX*/
			this._LocalData.setProperty("/logInfo", "");
			var oFile = oEvent.getParameter("files")[0];
			if (!oFile) {
				this._LocalData.setProperty("/excelSet", []);
				return;
			}
			
			var aExcelSet = [];
			var oItem;
			var oReader = new FileReader();
			oReader.readAsBinaryString(oFile); // 将文件读取为二进制编码
			oReader.onload = function (e) {
				// 获取excel内容，此时是乱码
				var sResult = e.target.result;
				// 解码excel内容
				var oWB = XLSX.read(sResult, {
					type: "binary",
					cellDates: true,
					dateNF: 'yyyy/mm/dd;@'
				});
				// 获取每个单元格的内容
				var oSheet = oWB.Sheets[oWB.SheetNames[0]];
				// 将单元格的内容转换成数组的形式（自动将第一行作为抬头）
				var aSheet = XLSX.utils.sheet_to_row_object_array(oSheet);
				// for循环每一行的内容添加到数据集当中,数据从第excel的3行开始（第一行为技术字段，第二行为说明行）
				for (var i = 2; i < aSheet.length; i++) {
					oItem = {
						"Type": "",
						"Message": "",
						"Operate": aSheet[i]["Operate"] || "",
						"SalesOrg": aSheet[i]["SalesOrg"] || "",
						"CustomerMaterial": aSheet[i]["CustomerMaterial"] || "",
						"StorageLocation": aSheet[i]["StorageLocation"] || "",
						"Partner": aSheet[i]["Partner"] || "",
						"Plant1": aSheet[i]["Plant1"] || "",
						"Plant2": aSheet[i]["Plant2"] || "",
					};
					aExcelSet.push(oItem);
				}
				//存在性检查
				this.existenceCheck(aExcelSet);
				
				this._LocalData.setProperty("/excelSet", aExcelSet);
			}.bind(this);
		},
		
		// getMediaUrl: function (sUrlString) {
		// 	if (sUrlString) {
		// 		var sUrl = new URL(sUrlString);
		// 		var iStart = sUrl.href.indexOf(sUrl.origin);
		// 		var sPath = sUrl.href.substring(iStart + sUrl.origin.length, sUrl.href.length);
		// 		return jQuery.sap.getModulePath("SD.wasteshippingreport") + "/S4" + sPath;
		// 	} else {
		// 		return "";
		// 	}
		// },
		
		existenceCheck: function(aExcelSet) {
			this.setBusy(true);
			for (var i = 0; i < aExcelSet.length; i++) {
				if (aExcelSet[i].Type != "E") {
					this.postCreate(aExcelSet[i], i, false);
				}
			}
		},
		
		onCreate: function () {
			this.setBusy(true);
			var aExcelSet = this._LocalData.getProperty("/excelSet");
			for (var i = 0; i < aExcelSet.length; i++) {
				this.postCreate(aExcelSet[i], i, true);
			}
		},
		postCreate: function(postData, i, bSaveFlag) {
			postData.SaveFlag = bSaveFlag;
			var mParameters = {
				groupId: "logisticsFeeProcess" + Math.floor(i / 100),
				changeSetId: i,
				success: function (oData) {
					this._LocalData.setProperty("/excelSet/" + i, oData);
					this.getView().byId("Create").setEnabled(!bSaveFlag);
				}.bind(this),
				error: function (oError) {
					this._LocalData.setProperty("/excelSet/" + i + "/Type", "E");
					// this._LocalData.setProperty("/excelSet/" + i + "/Message", .parseErrors(oError));
					this.getView().byId("Create").setEnabled(false);
				}.bind(this),
			};
			this.getOwnerComponent().getModel().create("/ZzWasteShipUploadSet", postData, mParameters);
		},
		
		setBusy: function(busy){
			this._LocalData.setProperty("/busy", busy, false);
		},
		
		onExport: function (sId) {
			if (!sId) {
				sId = "wasteshipping";
			}
			// 根据id值获取table 
			var oTable = this.getView().byId(sId);
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
					var oExcelCol = {
						// 获取表格的列名，即设置excel的抬头
						label: sLabelText,
						// 数据类型，即设置excel该列的数据类型
						type: "string",
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
		},
		
		// onBeforeExport: function() {
		// 	this.onExport("smartTable");
		// }

	});

});