sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter, Spreadsheet) {
	"use strict";
	var _objectCommData = {
		_entity: "/USER_AUTH", //此本页面操作的对象//绑定的数据源视图
		_aFilters: undefined,
		_entity2: "/PCH03_QUEREN",

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
					that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH03_MESSAGE1", this.getView()), null, 1, this.getView());
					
				}
			})
			return boo;

			
		},
		/**
		 * 确认
		 * @param {*} oEvent 
		 */
		onQr: function (oEvent) {
			var that = this;
				//设置通用dialog
				this._AfterDigLogCheck().then((selectedIndices) => {
					var pList = Array();
					if (this._checkType(selectedIndices)) {
						selectedIndices.forEach(odata => {
							if (odata.STATUS != '02') {
								var p = {
									po: odata.PO_NO,
									dNo: odata.D_NO
								}
								pList.push(p)
							}
						})
						this._querenDb(pList)

					}
				})
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
			var that = this;
			//设置通用dialog
			this._AfterDigLogCheck().then((selectedIndices) => {
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
				//完成后是否更新确认,false不更新Y
				that._isQuerenDb(selectedIndices,false);
				that._setBusy(false);
			})

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
			//有输入true则需要判读是否已经发送过邮件，且提示框内容不一样
			this._AfterDigLogCheck(true).then((selectedIndices) => {
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
							that.PrintTool._detailSelectPrint(that, sResponse, "test03/test1", oData, null, null, null, null).then(()=>{
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
								//完成后是否更新确认,false不更新Y
								that._isQuerenDb(selectedIndices,true);
								})
								
							})
							
							
							// that.PrintTool._detailSelectPrintDow(that, sResponse, "test/test", oData, null, null, null, null)
						})
					})
				}
			})
			
			
		
		},
		onPrintNPS: function () {
			var that = this;
			//设置通用dialog
			this._AfterDigLogCheck().then((selectedIndices) => {
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
						that.PrintTool._detailSelectPrintDow(that, sResponse, "test03/test1", oData, null,"納品書", null, null, null).then((oData) => {
							var sapPo = {
								po :PoList.join(","),
								tpye :"PCH03",
								fileName :"納品書",
							}
							that.PrintTool.printBackActionPo(that,sapPo)
								//完成后是否更新确认,false不更新Y
								that._isQuerenDb(selectedIndices,false);
							that._setBusy(false);
						})
					})
				})
			}
			})
			


		},
		onPrintZws: function () {
			var that = this;

			//设置通用dialog
			this._AfterDigLogCheck().then((ObList) => {
				let options = { compact: true, ignoreComment: true, spaces: 4 };

				//所有用户数据
				// var ObList = that._TableList("detailTable")

				// 创建一个新的Map对象  用作判断key值对应po对应的明细数据
				let myMap = new Map();
				// 创建一个新的Map对象  用作判断po是否状态
				let myZABCMap = new Map();
				var PoList = that._TableDataList("detailTable", 'PO_NO')

				//经过去重以后的po号
				var uniqueIdList = [...new Set(PoList)];
				// PoList= PoList.map
				if (uniqueIdList != undefined && uniqueIdList.length > 0) {
					that.setSysConFig().then(res => {
						//根据po号分批次打印，将po数据原分好，将分别调用cds取对应的信息
						ObList.forEach((item) => {
							myMap.get(item.PO_NO) ? myMap.get(item.PO_NO).push(item.ID) : myMap.set(item.PO_NO, [item.ID]);
							myZABCMap.get(item.PO_NO) ? myZABCMap.get(item.PO_NO) : myZABCMap.set(item.PO_NO, item.ZABC);
						})

						var zip = new JSZipSync();
						that.printTaskPdf = {
							completedCount: 0,
							count: myMap.length,
							progress: 0,
							pdfUrl: [],
							zip: zip,
							zipFolder: zip.folder("注文書"),
							zipFile: [],

						};
						//通过去重的po号取map数据进行打印
						uniqueIdList.forEach((item) => {

							//判断是EF,还是sonota还有W
							let obj = myZABCMap.get(item);
							//EFwei全po打印
							if (obj == "E" || obj == "F") {
							    //打印前先获取打印数据
								that.PrintTool._getPrintDataInfo(that, [item], "/PCH_T03_PO_ITEM_PRINT", "PO_NO").then((oData) => {
									let sResponse = json2xml(oData, options);
									console.log(sResponse)
									that.PrintTool._detailSelectPrintDowS(that, sResponse, "test03/test2", oData, null, "注文書", null, null, null).then((oData) => {
										var sapPo = {
											po: PoList.join(","),
											tpye: "PCH03",
											fileName: "納品書",
										}

										//完成后是否更新确认

										// that.PrintTool.printBackActionPo(that,sapPo)
										//完成后是否更新确认,false不更新Y

									})
								})
							}
							//sonota和C部分明细打印
							if (obj == "C" || obj == "W") {
								//打印前先获取打印数据
								that.PrintTool._getPrintDataInfo(that, myMap.get(item), "/PCH_T03_PO_ITEM_PRINT", "ID").then((oData) => {
									let sResponse = json2xml(oData, options);
									console.log(sResponse)
									that.PrintTool._detailSelectPrintDowS(that, sResponse, "test03/test2", oData, null, "注文書", null, null, null).then((oData) => {
										var sapPo = {
											po: PoList.join(","),
											tpye: "PCH03",
											fileName: "納品書",
										}

										//完成后是否更新确认

										// that.PrintTool.printBackActionPo(that,sapPo)
										//完成后是否更新确认,false不更新Y

									})
								})
							    
							}
							
							that._isQuerenDb(ObList, false);


						})
					})
				}


			})



		},
		onPrintZwsNps: function () {
			var that = this;

			//设置通用dialog
			this._AfterDigLogCheck().then((selectedIndices) => {

				let options = { compact: true, ignoreComment: true, spaces: 4 };
				var IdList = that._TableDataList("detailTable", 'ID')
				var PoList = that._TableDataList("detailTable", 'PO_NO')
				// PoList= PoList.map
				if (IdList) {
					that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T03_PO_ITEM_PRINT", "ID").then((oData) => {
						let sResponse = json2xml(oData, options);
						var zip = new JSZipSync();
						that.printTaskPdf = {
						  completedCount: 0,
						  count: 2,
						  progress : 0,
						  pdfUrl: [],
						  zip : zip,
						  zipFolder : zip.folder("注文書・指定納品書"),
						  zipFile : [],
						  
						};
						console.log(sResponse)
						that.setSysConFig().then(res => {
							// that.PrintTool._detailSelectPrint(that, sResponse, "test/test", oData, null, null, null, null)
							that.PrintTool._detailSelectPrintDowS(that, sResponse, "test03/test2", oData, null,"注文書", null, null, null).then((oData) => {
								var sapPo = {
									po :PoList.join(","),
									tpye :"PCH03",
									fileName :"納品書",
								}
								
															//完成后是否更新确认
							
								// that.PrintTool.printBackActionPo(that,sapPo)
							})

							that.PrintTool._detailSelectPrintDowS(that, sResponse, "test03/test1", oData, null,"納品書", null, null, null).then((oData) => {
								var sapPo = {
									po :PoList.join(","),
									tpye :"PCH03",
									fileName :"納品書",
								}
								//完成后是否更新确认,false不更新Y
								that._isQuerenDb(selectedIndices,false);
								// that.PrintTool.printBackActionPo(that,sapPo)
							})
							
						})
					})
				}
			})
		},
		/**
		 * 确认的调用后台
		 * @param {po和明细} pList 
		 */
		_querenDb(pList){
			var that = this;
			that._callCdsAction(_objectCommData._entity2, { parms: JSON.stringify(pList) }, that).then(

				function (odata) {
					that.byId("smartTable").rebindTable();
				},
				function (error) {
					that.MessageTools._addMessage(error.responseText);

				}
			)
		},
		
		/**
		 * 判断是否调用后台
		 */
		_isQuerenDb(tableList,boo){
			var pList = Array();
			tableList.forEach((item) => {
				if ('2' ==  item.USER_TYPE || ('1' ==  item.USER_TYPE && item.ZABC != 'E'&& item.ZABC != 'F'&& item.ZABC != 'W') ) {
					//为true时，为邮件调用，false为下载调用
					if(boo){
						var p = {
							po: item.PO_NO,
							dNo: item.D_NO,
							t:"t"
						}
					}else{
						var p = {
							po: item.PO_NO,
							dNo: item.D_NO,
							
						}
					}
				
					pList.push(p)
				}
			})
			this._querenDb(pList)
		},
	});
});