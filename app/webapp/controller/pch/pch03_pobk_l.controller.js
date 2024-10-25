sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter, Spreadsheet) {
	"use strict";
	var _objectCommData = {
		_entity: "/USER_AUTH", //此本页面操作的对象//绑定的数据源视图
		_aFilters: undefined

	};
	return Controller.extend("umc.app.controller.pch.pch03_pobk_l", {
		formatter: formatter,

		onInit: function () {
			this.getView().unbindElement();
			const oTable = this.byId("detailTable");
			// oTable.setSelectionMode("None");
			//  设置版本号
			this._setOnInitNo("MST01");
			this.MessageTools._clearMessage();
			this.MessageTools._initoMessageManager(this);

			this.getRouter().getRoute("RouteCre_pch03").attachPatternMatched(this._onRouteMatched, this);

		},

		// onPress: function (oEvent) {
		// var oItem = oEvent.getSource();
		// var oContext = oItem.getBindingContext();
		// this._onPress(oEvent, "RouteEdit_sys01", oContext.getObject().ID);
		// },
				/*==============================
		init
		==============================*/
		_onRouteMatched: function (oEvent) {
			var that = this;
			//取得权限
			this._readEntryData(_objectCommData._entity).then((odata)=>{
				that._setEditableAuth(odata.results[0].USER_TYPE);
			})


		},
		onRebind: function (oEvent) {
			// this._onListRebindDarft(oEvent);
			// this._onListRebindDarft(oEvent, true);
		},
		_checkType: function (oEvent) {
			let boo = true;
			oEvent.forEach(odata=>{
				if(odata.STATUS == '02'){
					that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("pch", "PCH03_MESSAGE1", this.getView()), null, 1, this.getView());
					
				}
			})
			return boo;

			
		},
		/**
		 * 确认
		 * @param {*} oEvent 
		 */
		onQr:function (oEvent) {
			var that = this
			// 假设你的数据模型是JSONModel，并且已经绑定到了SmartTable  
			var selectedIndices = this._TableList("detailTable");
			if (selectedIndices) {
				if(this._checkType(selectedIndices)){
					selectedIndices.forEach(odata=>{
						var p ={ 
							po:odata.PO_NO,
							dNo:odata.D_NO
						}
					})
					this._callCdsAction(PCH03_QUEREN,{parms:JSON.stringify(p)}).then(odata=>{
						that.byId("smartTable").rebindTable();
					})

					
				}
			}
			
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
		_csvdow: function (oEvt) {
			// 假设你的数据模型是JSONModel，并且已经绑定到了SmartTable  
			var selectedIndices = this._TableList("detailTable");
			if (selectedIndices) {
				// 构建CSV内容  
				var csvContent = "data:text/csv;charset=utf-8,";
				var headers = Object.keys(selectedIndices[0]); // 假设所有条目的结构都相同，取第一条的键作为表头  
				headers.shift();
				// csvContent += headers.join(",") + "\n";  

				selectedIndices.forEach(function (row) {
					var values = headers.map(function (header) {

						return (row[header] === null || row[header] === undefined) ? "" : '"' + row[header] + '"';


					});

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
			var that = this;
			var selectedIndices = this._TableList("detailTable");
				if (selectedIndices) {
					// 构建CSV内容  
					// var csvContent = "data:text/csv;charset=utf-8,";
					var csvContent = "";
					var headers = Object.keys(selectedIndices[0]); // 假设所有条目的结构都相同，取第一条的键作为表头  
					headers.shift();
					// csvContent += headers.join(",") + "\n";  

					selectedIndices.forEach(function (row) {
						var values = headers.map(function (header) {

							return (row[header] === null || row[header] === undefined) ? "" : '"' + row[header] + '"';


						});

						csvContent += values.join(",") + "\n";


					});
				}

				let options = { compact: true, ignoreComment: true, spaces: 4 };
				var IdList = that._TableDataList("detailTable", 'ID')
				if (IdList) {
					that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T03_PO_ITEM_PRINT", "ID").then((oData) => {
						let sResponse = json2xml(oData, options);
						console.log(sResponse)
						that.setSysConFig().then(res => {
							that.PrintTool._detailSelectPrint(that, sResponse, "test/test", oData, null, null, null, null).then(()=>{
								that.PrintTool.getImageBase64(that._blob).then((odata)=>{
								//20240820
								var mailobj = {
									emailJson: {
										TEMPLATE_ID: "test",
										MAIL_TO: "2644715037@qq.com",
										MAIL_BODY: [{
											object: "content",
											value: "hello"
										},
										{
											object: "recipient",
											value: "aa"
										},
										{
											object: "filename_1",
											value: "aa.pdf"
										},
										{
											object: "filecontent_1",
											value: odata
										},
										
										{
											object: "filename_2",
											value: "test.csv"
										},
										{
											object: "filecontent_2",
											value: csvContent
										}
									]
									}
								};




								let newModel = this.getView().getModel("Common");
								let oBind = newModel.bindList("/sendEmail");
								oBind.create(mailobj);
								})
								
							})
							
							
							// that.PrintTool._detailSelectPrintDow(that, sResponse, "test/test", oData, null, null, null, null)
						})
					})
				}
			
			
		
		},
		onPrint: function () {
			var that = this;
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			var IdList = that._TableDataList("detailTable", 'ID')
			var PoList = that._TableDataList("detailTable", 'PO_NO')
			// PoList= PoList.map
			if (IdList) {
				that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T03_PO_ITEM_PRINT", "ID").then((oData) => {
					let sResponse = json2xml(oData, options);
					console.log(sResponse)
					that.setSysConFig().then(res => {
						// that.PrintTool._detailSelectPrint(that, sResponse, "test/test", oData, null, null, null, null)
						that.PrintTool._detailSelectPrintDow(that, sResponse, "test/test", oData, null,"納品書", null, null, null).then((oData) => {
							var sapPo = {
								po :PoList.join(","),
								tpye :"PCH03",
								fileName :"納品書",
							}
							that.PrintTool.printBackActionPo(that,sapPo)
						})
					})
				})
			}


		}
	});
});