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
		_entity3: "/PCH03_GETTYPE",
		_entity4: "/PCH03_GETTYPE",
		_EmailPdfLen: 0,
		_EmailPdfCount: 0,
	};

	return Controller.extend("umc.app.controller.pch.pch03_pobk_l", {
		formatter: formatter,

		onInit: function () {
			this.getView().unbindElement();
			const oTable = this.byId("detailTable");
			// oTable.setSelectionMode("None");
			//  设置版本号
			this._setOnInitNo("MST01");
			// this.MessageTools._clearMessage();
			// this.MessageTools._initoMessageManager(this);

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
			this._readEntryData(_objectCommData._entity).then((odata) => {
				that._setEditableAuth(odata.results[0].USER_TYPE);
			})

			that.setSysConFig().then(res => {
			})

		},
		onRebind: function (oEvent) {
			// this._onListRebindDarft(oEvent);
			// this._onListRebindDarft(oEvent, true);
		},
		_checkType: function (oEvent) {
			let boo = true;
			oEvent.forEach(odata => {
				if (odata.STATUS == '02') {
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
				that._isQuerenDb(selectedIndices, false);
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
				var csvContent = that._getCsvData(selectedIndices);
				var tempName = "";
				// 创建一个新的Map对象  用作判断key值对应po对应的明细数据
				let myMap = new Map();
				// 创建一个新的Map对象  用作判断po是否状态
				let myZABCMap = new Map();
				// 创建一个新的Map对象  用作判断po是否状态
				let mySuppliMap = new Map();
				// 创建一个新的Map对象  用作判断po是否状态
				let myPlantMap = new Map();
				var PoList = that._TableDataList("detailTable", 'PO_NO')
				//经过去重以后的po号
				var uniqueIdList = [...new Set(PoList)];
				//设置要打印pdf个数
				_objectCommData._EmailPdfLen = 2

				var IdList = that._TableDataList("detailTable", 'ID')
				that._readSys("MM0002-1", "/SYS_T08_COM_OP_D").then((sys) => {
					selectedIndices.forEach((item) => {
						myMap.get(item.PO_NO) ? myMap.get(item.PO_NO).push(item.ID) : myMap.set(item.PO_NO, [item.ID]);
						myZABCMap.get(item.PO_NO) ? myZABCMap.get(item.PO_NO) : myZABCMap.set(item.PO_NO, item.ZABC);
						mySuppliMap.get(item.PO_NO) ? mySuppliMap.get(item.PO_NO) : mySuppliMap.set(item.PO_NO, item.SUPPLIER);
						myPlantMap.get(item.PO_NO) ? myPlantMap.get(item.PO_NO) : myPlantMap.set(item.PO_NO, item.PLANT_ID,);
					})

					//通过去重的po号取map数据进行打印
					uniqueIdList.forEach((item) => {
						//重置以打印个数
						_objectCommData._EmailPdfCount = 0
						that._callCdsAction(_objectCommData._entity4, { parms: item }, that).then((data) => {
							if("1400" == myPlantMap.get(item)){
								tempName = "UWEB_PCH03_P";
							}else{
							    if ("IUSSE" == data.PCH03_GETTYPE) {
									tempName = "UWEB_PCH03_C";
								}
								if ("REIUSSE" == data.PCH03_GETTYPE) {
									tempName = "UWEB_PCH03_U";
								}
								if ("CANCEL" == data.PCH03_GETTYPE) {
									tempName = "UWEB_PCH03_D";
								}
							}
							
							var mailobj = {
								emailJson: {
									TEMPLATE_ID: tempName,
									MAIL_TO: sys.results[0].VALUE02,
									MAIL_BODY: [
										{
											object: "仕入先名称",
											value: mySuppliMap.get(item) // 使用替换后的邮件内容
										},

										// {
										// 	object: "filename_2",
										// 	value: "test.csv"
										// },
										// {
										// 	object: "filecontent_2",
										// 	value: csvContent
										// }
									]
								}
							};

							let obj = myZABCMap.get(item);
							//EFwei全po打印
							if (obj == "E" || obj == "F") {
	
								that._printZWSPrintEmail(that, [item], "/PCH_T03_PO_ITEM_PRINT", "PO_NO",mailobj).then((oData) => {

									that._newNPSprinEmil(myMap, that, item, mailobj).then((oData) => {

										this._sendEmail(mailobj);
										this._setBusy(false);
									})
	
								})
	
							}
							//sonota和C部分明细打印
							if (obj == "C" || obj == "W") {
								that._printZWSPrintEmail(that, myMap.get(item), "/PCH_T03_PO_ITEM_PRINT", "ID",mailobj).then((oData) => {

									that._newNPSprinEmil(myMap, that, item, mailobj).then((oData) => {

										this._sendEmail(mailobj);
										this._setBusy(false);
									})
	
								})
	
	
							}

							

						})
					})
					that._isQuerenDb(selectedIndices, true);
					// {
					// 	object: "recipient",
					// 	value: "aa"
					// },
					// {
					// 	object: "filename_1",
					// 	value: "aa.pdf"
					// },
					// {
					// 	object: "filecontent_1",
					// 	value: odata
					// },


				})








			})



		},
		_getCsvData: function (selectedIndices) {
			var that = this;
			var csvContent = "";
			if (selectedIndices) {
				// 构建CSV内容  
				// var csvContent = "data:text/csv;charset=utf-8,";

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
			return csvContent;
		},
		onPrintNPS: function () {
			var that = this;
			//设置通用dialog
			this._AfterDigLogCheck().then((selectedIndices) => {
				// 创建一个新的Map对象  用作判断key值对应po对应的明细数据
				let myMap = new Map();
				// 创建一个新的Map对象  用作判断po是否状态
				let myZABCMap = new Map();
				var PoList = that._TableDataList("detailTable", 'PO_NO')
				//经过去重以后的po号
				var uniqueIdList = [...new Set(PoList)];
				// PoList= PoList.map
				if (uniqueIdList != undefined && uniqueIdList.length > 0) {

					selectedIndices.forEach((item) => {
						myMap.get(item.PO_NO) ? myMap.get(item.PO_NO).push(item.ID) : myMap.set(item.PO_NO, [item.ID]);
						myZABCMap.get(item.PO_NO) ? myZABCMap.get(item.PO_NO) : myZABCMap.set(item.PO_NO, item.ZABC);
					})

					var zip = new JSZipSync();
					that.printTaskPdf = {
						completedCount: 0,
						count: myMap.size,
						progress: 0,
						pdfUrl: [],
						zip: zip,
						zipFolder: zip.folder("納品書"),
						zipFile: [],

					};
					that._onNPSprintTy(selectedIndices, uniqueIdList, myMap, myZABCMap, that, false);

				}

			})


		},
		onPrintZws: function () {
			var that = this;

			//设置通用dialog
			this._AfterDigLogCheck().then((ObList) => {
				// 创建一个新的Map对象  用作判断key值对应po对应的明细数据
				let myMap = new Map();
				// 创建一个新的Map对象  用作判断po是否状态
				let myZABCMap = new Map();
				var PoList = that._TableDataList("detailTable", 'PO_NO')
				//经过去重以后的po号
				var uniqueIdList = [...new Set(PoList)];
				// PoList= PoList.map
				if (uniqueIdList != undefined && uniqueIdList.length > 0) {

					ObList.forEach((item) => {
						myMap.get(item.PO_NO) ? myMap.get(item.PO_NO).push(item.ID) : myMap.set(item.PO_NO, [item.ID]);
						myZABCMap.get(item.PO_NO) ? myZABCMap.get(item.PO_NO) : myZABCMap.set(item.PO_NO, item.ZABC);
					})

					var zip = new JSZipSync();
					that.printTaskPdf = {
						completedCount: 0,
						count: myMap.size,
						progress: 0,
						pdfUrl: [],
						zip: zip,
						zipFolder: zip.folder("納品書"),
						zipFile: [],

					};
					that._onZWSprintTy(ObList, uniqueIdList, myMap, myZABCMap, that);

				}
			})

		},
		_printZWSPrintEmail(that, item, cds, key,list) {
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			return new Promise(function (resolve, reject) {
			//打印前先获取打印数据
			that.PrintTool._getPrintDataInfo(that, item, cds, key).then((oData) => {
				let sResponse = json2xml(oData, options);
				console.log(sResponse)

				that.PrintTool._detailSelectPrintEmil(that, sResponse, "test03/test2", oData, null, "注文書", null, null, null).then((oData) => {
					// var sapPo = {
					// 	po: PoList.join(","),
					// 	tpye: "PCH03",
					// 	fileName: "納品書",
					// }

					//完成后是否更新确认

					// that.PrintTool.printBackActionPo(that,sapPo)
					//完成后是否更新确认,false不更新Y
					that.PrintTool.getImageBase64(oData).then((odata2) => {
						list.emailJson.MAIL_BODY.push({
							object: "filename_2",
							value: "注文書.pdf"
						});
						list.emailJson.MAIL_BODY.push({
							object: "filecontent_2",
							value: odata2
						});
						resolve(true);
					});
				})

				
			})
			})
		

		},
		_printZWSPrint(that, item, cds, key) {
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			return new Promise(function (resolve, reject) {
			//打印前先获取打印数据
			that.PrintTool._getPrintDataInfo(that, item, cds, key).then((oData) => {
				let sResponse = json2xml(oData, options);
				console.log(sResponse)

				that.PrintTool._detailSelectPrintDowS(that, sResponse, "test03/test2", oData, null, "注文書", null, null, null).then((oData) => {
					// var sapPo = {
					// 	po: PoList.join(","),
					// 	tpye: "PCH03",
					// 	fileName: "納品書",
					// }

					//完成后是否更新确认

					// that.PrintTool.printBackActionPo(that,sapPo)
					//完成后是否更新确认,false不更新Y
					
				})

				resolve();
			})
			})
		

		},



		_onZWSprintTy: function (ObList, uniqueIdList, myMap, myZABCMap, that, boo) {
			let options = { compact: true, ignoreComment: true, spaces: 4 };


			return new Promise(function (resolve, reject) {




					//通过去重的po号取map数据进行打印
					uniqueIdList.forEach((item) => {

						//判断是EF,还是sonota还有W
						let obj = myZABCMap.get(item);
						//EFwei全po打印
						if (obj == "E" || obj == "F") {

							that._printZWSPrint(that, [item], "/PCH_T03_PO_ITEM_PRINT", "PO_NO")

						}
						//sonota和C部分明细打印
						if (obj == "C" || obj == "W") {
							that._printZWSPrint(that, myMap.get(item), "/PCH_T03_PO_ITEM_PRINT", "ID")


						}
						resolve();

					})
				
					that._isQuerenDb(ObList, false);
					that._setBusy(false);
				
			})

		},
		/* 全部取得后发送邮件 */
		_checkEmailaskPdf: function (mailobj) {
			_objectCommData._EmailPdfCount++;

			if (_objectCommData._EmailPdfCount == _objectCommData._EmailPdfLen) {
				this._sendEmail(mailobj);
			}

		},
		/**发送邮件 */
		_sendEmail: function (mailobj) {
			let newModel = this.getView().getModel("Common");
			let oBind = newModel.bindList("/sendEmail");
			oBind.create(mailobj);

		},
		_newNPSprinEmil(myMap, that, item, list) {
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			var that = this;
			return new Promise(function (resolve, reject) {
			
			that.PrintTool._getPrintDataInfo(that, myMap.get(item), "/PCH_T03_PO_ITEM_PRINT", "ID").then((oData) => {
				let sResponse = json2xml(oData, options);
				console.log(sResponse);

				// that.PrintTool._detailSelectPrint(that, sResponse, "test/test", oData, null, null, null, null)
				that.PrintTool._detailSelectPrintEmil(that, sResponse, "test03/test1", oData, null, "納品書", null, null, null).then((oData) => {
					// var sapPo = {
					// 	po :PoList.join(","),
					// 	tpye :"PCH03",
					// 	fileName :"納品書",
					// }
					// that.PrintTool.printBackActionPo(that,sapPo)
					//完成后是否更新确认,false不更新Y
					that.PrintTool.getImageBase64(oData).then((odata2) => {
						list.emailJson.MAIL_BODY.push({
							object: "filename_1",
							value: "納品書.pdf"
						});
						list.emailJson.MAIL_BODY.push({
							object: "filecontent_1",
							value: odata2
						});
						resolve(true);
					});

				});
			});
			});
		},
		_newNPSprinDowS(myMap, that, item) {
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			var that = this;
			that.PrintTool._getPrintDataInfo(that, myMap.get(item), "/PCH_T03_PO_ITEM_PRINT", "ID").then((oData) => {
				let sResponse = json2xml(oData, options);
				console.log(sResponse)

				// that.PrintTool._detailSelectPrint(that, sResponse, "test/test", oData, null, null, null, null)
				that.PrintTool._detailSelectPrintDowS(that, sResponse, "test03/test1", oData, null, "納品書", null, null, null).then((oData) => {
					// var sapPo = {
					// 	po :PoList.join(","),
					// 	tpye :"PCH03",
					// 	fileName :"納品書",
					// }
					// that.PrintTool.printBackActionPo(that,sapPo)
					//完成后是否更新确认,false不更新Y


				})

			})
		},
		_onNPSprintTy: function (ObList, uniqueIdList, myMap, myZABCMap, that, boo, list) {

			return new Promise(function (resolve, reject) {


				if (boo) {
					uniqueIdList.forEach((item) => {

						that._newNPSprinEmil(myMap, that, item, list);
						resolve(true);

					})
				} else {//通过去重的po号取map数据进行打印
					uniqueIdList.forEach((item) => {
						that._newNPSprinDowS(myMap, that, item, list);
						resolve(true);


					})
				}
				that._isQuerenDb(ObList, false)
				that._setBusy(false);

			})


		},

		onPrintZwsNps: function () {
			var that = this;

			//设置通用dialog
			this._AfterDigLogCheck().then((selectedIndices) => {
				// 创建一个新的Map对象  用作判断key值对应po对应的明细数据
				let myMap = new Map();
				// 创建一个新的Map对象  用作判断po是否状态
				let myZABCMap = new Map();


				let options = { compact: true, ignoreComment: true, spaces: 4 };
				var IdList = that._TableDataList("detailTable", 'ID')
				var PoList = that._TableDataList("detailTable", 'PO_NO')
				//经过去重以后的po号
				var uniqueIdList = [...new Set(PoList)];
				// PoList= PoList.map
				if (IdList) {

					selectedIndices.forEach((item) => {
						myMap.get(item.PO_NO) ? myMap.get(item.PO_NO).push(item.ID) : myMap.set(item.PO_NO, [item.ID]);
						myZABCMap.get(item.PO_NO) ? myZABCMap.get(item.PO_NO) : myZABCMap.set(item.PO_NO, item.ZABC);
					})
					var zip = new JSZipSync();
					that.printTaskPdf = {
						completedCount: 0,
						count: myMap.size + 1,
						progress: 0,
						pdfUrl: [],
						zip: zip,
						zipFolder: zip.folder("注文書・指定納品書"),
						zipFile: [],

					};
					that._onZWSprintTy(selectedIndices, uniqueIdList, myMap, myZABCMap, that, false);

					that._onNPSprintTy(selectedIndices, uniqueIdList, myMap, myZABCMap, that, false);

					that._isQuerenDb(selectedIndices, false);
				}
			})
		},
		/**
		 * 确认的调用后台
		 * @param {po和明细} pList 
		 */
		_querenDb(pList) {
			var that = this;
			that._callCdsAction(_objectCommData._entity2, { parms: JSON.stringify(pList) }, that).then(

				function (odata) {
					that.byId("smartTable").rebindTable();
				},
				function (error) {
					that.MessageTools._addMessage(error.responseText, null, null, that.getView());

				}
			)
		},

		/**
		 * 判断是否调用后台
		 */
		_isQuerenDb(tableList, boo) {
			var pList = Array();
			tableList.forEach((item) => {
				if ('2' == item.USER_TYPE || ('1' == item.USER_TYPE && item.ZABC != 'E' && item.ZABC != 'F' && item.ZABC != 'W')) {
					//为true时，为邮件调用，false为下载调用
					if (boo) {
						var p = {
							po: item.PO_NO,
							dNo: item.D_NO,
							t: "t"
						}
					} else {
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