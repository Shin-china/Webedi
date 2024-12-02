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
	var _objecPrintData = {
		_mailobj: "",//邮件内容 
		_supplier: "",//供应商
		_Len: 0, //这个供应商应该发送多少个po文件
		_Cnt: 0,//这个供应商已经发送了多少个po文件
		_poNo: "",//当前的po号
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
					
					console.log((this.MessageTools._getI18nTextInModel("pch", "PCH03_MESSAGE1", this.getView())))
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
			this.MessageTools._clearMessage();
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
		_setHeaderModel: function () {
			let keys = ["obj1","DOWN_FLAG","TYPE","obj1","ID","INT_NUMBER","PO_DATE","PO_D_DATE","BP_NAME1","SUPPLIER_MAT","PO_D_TXZ01","MAT_ID","CUST_MATERIAL","MANU_MATERIAL","PO_PUR_UNIT","CURRENCY","PO_PUR_QTY2","DEL_PRICE2","ISSUEDAMOUNT","MEMO","備考２","BP_ID","資産元","STORAGE_LOC","STORAGE_TXT","checkOk","BYNAME","PR_BY","棚番号","調整依頼コメント","納品先郵便番号","納品先住所１","納品先住所２","納品先住所３","納品先住所４","納品先電話番号","納品先ＦＡＸ","納品日","納品数"]
			// headers.shift();
			return keys;
			// headers.splice(4, 0, 55);
		},
		_setHeaderData: function (csvContent) {
			let headers = ["項目","確認区分","ステータス","システム管理番号","発注番号","海外PO番号","発注日","納期","メーカー","品目番号","品目名称","UMC共通品番(SAP品番)","顧客品番","メーカー品番","単位","通貨","発注数","発注単価","発注金額","備考１","備考２","得意先コード","資産元","納入先コード","納品先名","検査区分","発注担当者","依頼者","棚番号","調整依頼コメント","納品先郵便番号","納品先住所１","納品先住所２","納品先住所３","納品先住所４","納品先電話番号","納品先ＦＡＸ","納品日","納品数"]

			
		    csvContent += headers.join(",") + "\n";
			return csvContent;
		},
		/**
		 * 将数据通过suppli分组
		 * @param {*} csvContent 
		 * @returns 
		 */
		_getObjiList: function (csvContent,s) {
			const groupedBySupplier = csvContent.reduce((acc, item) => {
				// 如果累加器（acc）中还没有这个SUPPLIER，则初始化为一个空数组
				if (!acc[item[s]]) {
					acc[item[s]] = [];
				}
				// 将当前项添加到对应的SUPPLIER数组中
				acc[item[s]].push(item);
				return acc;
			}, {});
			


			return groupedBySupplier;
		},

		onCsvdow: function (oEvt) {
			// 假设你的数据模型是JSONModel，并且已经绑定到了SmartTable  
			var that = this;
			//设置通用dialog
			this._AfterDigLogCheck().then((selectedIndices) => {
				const groupedBySupplier =that._getObjiList(selectedIndices, "SUPPLIER");

				Object.keys(groupedBySupplier).forEach(key => {
					const row = groupedBySupplier[key];

					// 构建CSV内容  
					var csvContent = "data:text/csv;charset=utf-8,";
					csvContent = that._getCsvData(row,csvContent);

					// 触发下载  
					var encodedUri = encodeURI(csvContent);
					var link = document.createElement("a");
					link.setAttribute("href", encodedUri);
					link.setAttribute("download", that.CommTools.getCurrentTimeFormatted()+"_"+key+".csv");
					document.body.appendChild(link);
					link.click();
				})

				//完成后是否更新确认,false不更新Y
				that._isQuerenDb(selectedIndices, "csv");
				that._isPrintHx(selectedIndices);
				that._setBusy(false);
			})

		},

		_isPrintHx: function (selectedIndices) {
			this._callCdsAction("/PCH03_PRINTHX", this._getDialogParm(selectedIndices), this).then((oData) => {
			
			})
		
		},
		onBeforeExport: function (oEvt) {
			var mExcelSettings = oEvt.getParameter("exportSettings");

			// Disable Worker as Mockserver is used in Demokit sample
			mExcelSettings.worker = false;
		},
		onFax: function (oEvt) {
			var that = this;
			this.MessageTools._clearMessage();
			//有输入true则需要判读是否已经发送过邮件，且提示框内容不一样
			this._AfterDigLogCheck(true).then((selectedIndices) => {
				//发注区分的报错fax
				//true则报错，false则无报错
				if (this._checkZABC(selectedIndices, "F")) {
					this._setBusy(false);
					console.log((this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG4", this.getView())))
					that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG4", this.getView()), null, 1, this.getView());
					return;

				} else {
					//
					//获取supplierMap
					var supplierObjMap = new Map();
					var supplierLenMap = new Map();
					var supplierCntMap = new Map();
					//将工厂分过的数据通过供应商分组
					const groupedBySupplier = that._getObjiList(selectedIndices, "SUPPLIER");
					Object.keys(groupedBySupplier).forEach(supplier => {
						//所有供应商下的数据	
						const obj1 = groupedBySupplier[supplier];
						if (obj1[0].EMAIL_ADDRESS) {
							this._sendEmailFaxTy(obj1, supplierObjMap, supplierLenMap, supplierCntMap, supplier, "UWEB_M006")
						} else {
							this._setBusy(false);
							console.log((this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG1", this.getView())))
							that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG1", this.getView()), null, 1, this.getView());
						}
					})
					this._isQuerenDb(selectedIndices, "eml");
				}



			})
		},
		_checkZABC: function (selectedIndices,_ZABC){
			var boo =false;
			selectedIndices.some((index) => {
				if(index.ZABC != _ZABC){
					 boo = true;
				}
			})
			return boo;
		},
		onEmail: function (oEvt) {
			var that = this;
			this.MessageTools._clearMessage();
			//有输入true则需要判读是否已经发送过邮件，且提示框内容不一样
			this._AfterDigLogCheck(true).then((selectedIndices) => {
				//发注区分的报错fax
				//true则报错，false则无报错
				if(this._checkZABC(selectedIndices,"W") && this._checkZABC(selectedIndices,"E") ){
					this._setBusy(false);
					console.log((this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG3", this.getView())))
					that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG3", this.getView()), null, 1, this.getView());
					return;
					
				}else{

				//
				//获取supplierMap
				var supplierObjMap = new Map();
				var supplierLenMap = new Map();
				var supplierCntMap = new Map();
				//将数据通过工厂分组
				const plantList = that._getObjiList(selectedIndices, "PLANT_ID");

				const plant1100 = plantList["1100"];
				const plant1400 = plantList["1400"];
				if (plant1100 && plant1100.length > 0) {
					//循环1100的数据
					plant1100.forEach((item) => {
						//通过供应商分组

						//将工厂分过的数据通过供应商分组
						const groupedBySupplier = that._getObjiList(plant1100, "SUPPLIER");
						//将mailobj对象设置到supplierLenMap中


						Object.keys(groupedBySupplier).forEach(supplier => {
							//所有供应商下的数据	
							const obj1 = groupedBySupplier[supplier];

							//通过新规变更删除分组
							var typeList = that._getObjiList(obj1, "TYPE");
							Object.keys(typeList).forEach(type => {
								//所有供应商下的数据	
								const typeObj = typeList[type];
								if(typeObj[0].EMAIL_ADDRESS){
								if ("新规" == type) {
									this._sendEmailTy(typeObj, supplierObjMap, supplierLenMap, supplierCntMap, supplier, "UWEB_PCH03_C")
								}
								if ("削除" == type) {
									this._sendEmailTy(typeObj, supplierObjMap, supplierLenMap, supplierCntMap, supplier, "UWEB_PCH03_D")
								}
								if ("変更" == type) {
									this._sendEmailTy(typeObj, supplierObjMap, supplierLenMap, supplierCntMap, supplier, "UWEB_PCH03_U")
								}
							}else{
								this._setBusy(false);
								console.log((this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG1", this.getView())))
								that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG1", this.getView()), null, 1, this.getView());
							}
							})

						})


					})
				}
				if (plant1400 && plant1400.length > 0) {
					//循环1400的数据
					//通过供应商分组
					//将工厂分过的数据通过供应商分组
					const groupedBySupplier = that._getObjiList(plant1400, "SUPPLIER");
					//将mailobj对象设置到supplierLenMap中
					Object.keys(groupedBySupplier).forEach(supplier => {
						//所有供应商下的数据	
						const obj1 = groupedBySupplier[supplier];
						if(obj1[0].EMAIL_ADDRESS){
							this._sendEmailTy(obj1, supplierObjMap, supplierLenMap, supplierCntMap, supplier, "UWEB_PCH03_P")
						}else{
							this._setBusy(false);
							console.log((this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG1", this.getView())))
							that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_03_ERROR_MSG1", this.getView()), null, 1, this.getView());
						}


						

					})
				}
				that._isQuerenDb(selectedIndices, "eml");
				}


			})
		},

		_sendEmailTy: function (obj1,supplierObjMap,supplierLenMap,supplierCntMap,supplier,TEMPLATE_ID){
			var that = this;
			// 创建一个新的Map对象  用作判断key值对应po对应的明细数据
			let myMap = new Map();
			var poList = [];
			obj1.forEach((item) => {
				poList.push(item.PO_NO);
				myMap.get(item.PO_NO) ? myMap.get(item.PO_NO).push(item.ID) : myMap.set(item.PO_NO, [item.ID]);
			})




			//经过去重以后的po号
			var uniqueIdList = [...new Set(poList)];
			//获取csv
			var csvContent = that._getCsvData(obj1,"");
			var mailobj = {
				emailJson: {
					TEMPLATE_ID: TEMPLATE_ID,
					MAIL_TO: obj1[0].EMAIL_ADDRESS,
					MAIL_BODY: [
						{
							object: "仕入先名称",
							value: supplier // 使用替换后的邮件内容
						},
						{
							object: "po",
							value: uniqueIdList // 使用替换后的邮件内容
						},
						{
							object: "filename_2",
							value: that.CommTools.getCurrentTimeFormatted()+"_"+supplier+".csv"
						},
						{
							object: "filecontent_2",
							value: csvContent
						}
					]
				}
			};
			
			//将mailobj对象设置到supplierObjMap中
			supplierObjMap.set(supplier, mailobj);
			supplierLenMap.set(supplier, uniqueIdList.length);
			supplierCntMap.set(supplier, 0);

			//循环po通过po打印
			for(var i=0;i<uniqueIdList.length;i++){
				
				var item = uniqueIdList[i];
				let obj = obj1[0].ZABC;
				//EFwei全po打印
				if (obj == "E" || obj == "F") {

					that._printZWSPrintEmail(that, [item], "/PCH_T03_PO_ITEM_PRINT", "PO_NO",supplierObjMap,item,supplier).then((oData) => {

						that._newNPSprinEmil(myMap, that, oData, supplierObjMap).then((oData2) => {


							if(this._checkEmailaskPdf(supplierLenMap,supplierCntMap,oData2[1])){
								this._sendEmail(supplierObjMap.get(oData2[1]));
								this._setBusy(false);
							}
						})

					})

				}
				//sonota和C部分明细打印
				if (obj == "C" || obj == "W") {
					that._printZWSPrintEmail(that, myMap.get(item), "/PCH_T03_PO_ITEM_PRINT", "ID",supplierObjMap,item,supplier).then((oData) => {

						that._newNPSprinEmil(myMap, that, oData, supplierObjMap).then((oData2) => {
							if(this._checkEmailaskPdf(supplierLenMap,supplierCntMap,oData2[1])){
								this._sendEmail(supplierObjMap.get(oData2[1]));
								this._setBusy(false);
							}
						})
					})
				}

			}
		 },
		 _sendEmailFaxTy: function (obj1,supplierObjMap,supplierLenMap,supplierCntMap,supplier,TEMPLATE_ID){
			var that = this;
			// 创建一个新的Map对象  用作判断key值对应po对应的明细数据
			let myMap = new Map();
			var poList = [];
			obj1.forEach((item) => {
				poList.push(item.PO_NO);
				myMap.get(item.PO_NO) ? myMap.get(item.PO_NO).push(item.ID) : myMap.set(item.PO_NO, [item.ID]);
			})




			//经过去重以后的po号
			var uniqueIdList = [...new Set(poList)];
			var mailobj = {
				emailJson: {
					TEMPLATE_ID: TEMPLATE_ID,
					MAIL_TO: obj1[0].EMAIL_ADDRESS,
					MAIL_BODY: [

						{
							object: "発注番号",
							value: uniqueIdList.join("_") // 使用替换后的邮件内容
						},
					
					]
				}
			};
			
			//将mailobj对象设置到supplierObjMap中
			supplierObjMap.set(supplier, mailobj);
			supplierLenMap.set(supplier, uniqueIdList.length);
			supplierCntMap.set(supplier, 0);

			//循环po通过po打印
			for(var i=0;i<uniqueIdList.length;i++){
				
				var item = uniqueIdList[i];
				let obj = obj1[0].ZABC;
				//EFwei全po打印
				if (obj == "E" || obj == "F") {

					that._printZWSPrintEmail(that, [item], "/PCH_T03_PO_ITEM_PRINT", "PO_NO",supplierObjMap,item,supplier,"FAX").then((oData) => {

						that._newNPSprinEmil(myMap, that, oData, supplierObjMap).then((oData2) => {


							if(this._checkEmailaskPdf(supplierLenMap,supplierCntMap,oData2[1])){
								this._sendEmail(supplierObjMap.get(oData2[1]));
								this._setBusy(false);
							}
						})

					})

				}
				//sonota和C部分明细打印
				if (obj == "C" || obj == "W") {
					that._printZWSPrintEmail(that, myMap.get(item), "/PCH_T03_PO_ITEM_PRINT", "ID",supplierObjMap,item,supplier,"FAX").then((oData) => {

						that._newNPSprinEmil(myMap, that, oData, supplierObjMap).then((oData2) => {
							if(this._checkEmailaskPdf(supplierLenMap,supplierCntMap,oData2[1])){
								this._sendEmail(supplierObjMap.get(oData2[1]));
								this._setBusy(false);
							}
						})
					})
				}

			}
		 },
		_getCsvData: function (selectedIndices,csvContent) {
			var that = this;
			if (selectedIndices) {
							//设置头的模板
							var headers = that._setHeaderModel();
							//头部数据写入一行
							csvContent = that._setHeaderData(csvContent);
			
							selectedIndices.forEach(function (row) {
								var values = headers.map(function (header) {
									let re = ""
									//如果取不出来就是
									if(row[header] === null || row[header] === undefined){
										return re = ""
									}else{
										if("PO_DATE" == header || "PO_D_DATE" == header){
											re = that.CommTools._formatToYYYYMMDD(row[header]);
										}else{
											re = '"' + row[header] + '"'
										}
									}
									return re;
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
						zipFolder: zip.folder(),
						zipFile: [],

					};
					that._onNPSprintTy(selectedIndices, uniqueIdList, myMap, myZABCMap, that, false);

				}
				that._isQuerenDb(selectedIndices, "nps");

			})


		},
		onPrintZws: function () {
			var that = this;

			//设置通用dialog
			this._AfterDigLogCheck(true).then((ObList) => {
				// 创建一个新的Map对象  用作判断key值对应po对应的明细数据
				let myMap = new Map();
				// 创建一个新的Map对象  用作判断po是否状态
				let myZABCMap = new Map();
				// 创建一个新的Map对象  用作判断key值对应po对应的明细数据 po+明细
				let myMapPoDno = new Map();
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
						zipFolder: zip.folder(),
						zipFile: [],

					};
					that._onZWSprintTy(ObList, uniqueIdList, myMap, myZABCMap, that);

				}
				that._isQuerenDb(ObList,"zws");
			})

		},
		_printZWSPrintEmail(that, item, cds, key,list,name,supplier,fax) {
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			return new Promise(function (resolve, reject) {
			//打印前先获取打印数据npm
			that.PrintTool._getPrintDataInfo(that, item, cds, key).then((oData) => {
				let sResponse = json2xml(oData, options);
				console.log(sResponse)

				that.PrintTool._detailSelectPrintEmil(that, sResponse, "test03/test2", oData, null, "注文書", null, null, null).then((oData) => {

					//完成后是否更新确认,false不更新Y
					that.PrintTool.getImageBase64(oData).then((odata2) => {
						list.get(supplier).emailJson.MAIL_BODY.push({
							object: "filename_2",
							value: fax? "TransFAX_"+name+".pdf" :that.CommTools.getCurrentTimeFormatted()+"_"+name+"注文書.pdf"
						});
						list.get(supplier).emailJson.MAIL_BODY.push({
							object: "filecontent_2",
							value: odata2
						});
						resolve([name,supplier]);
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

				that.PrintTool._detailSelectPrintDowS(that, sResponse, "test03/test2", oData, null, null, null, null, null).then((oData) => {
					//po=po+podno
					var sapPo = {
						po: item[0],
						type: "PCH03",
						fileName: "注文書",
					}
					//打印pdf后写表共通
					that.PrintTool.printBackActionPo(that,oData,sapPo)

					
				})

				resolve();
			})
			})
		

		},


		/**
		 * 打印注文书中转站
		 * @param {*} ObList 
		 * @param {*} uniqueIdList 
		 * @param {*} myMap 
		 * @param {*} myZABCMap 
		 * @param {*} that 
		 * @param {*} boo 
		 * @returns 
		 */
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
				
					that._setBusy(false);
				
			})

		},
		/* 全部取得后发送邮件  返回true发送 */
		_checkEmailaskPdf: function (supplierLenMap, supplierCntMap, supplier) {

			
			//将supplierCntMap中值+1
			supplierCntMap.set(supplier, supplierCntMap.get(supplier) + 1);
			if(supplierLenMap.get(supplier)==supplierCntMap.get(supplier)){
				return true;
			}

			return false;

		},

		_newNPSprinEmil(myMap, that, item, list) {
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			var that = this;
			return new Promise(function (resolve, reject) {
			
			that.PrintTool._getPrintDataInfo(that, myMap.get(item[0]), "/PCH_T03_PO_ITEM_PRINT", "ID").then((oData) => {
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
						list.get(item[1]).emailJson.MAIL_BODY.push({
							object: "filename_3",
							value: that.CommTools.getCurrentTimeFormatted()+"_"+item[0]+"納品書.pdf"
						});
						list.get(item[1]).emailJson.MAIL_BODY.push({
							object: "filecontent_3",
							value: odata2
						});
						resolve(item);
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
				that.PrintTool._detailSelectPrintDowS(that, sResponse, "test03/test1", oData, null, null, null, null, null).then((oData) => {
					var sapPo = {
						po:  myMap.get(item),
						type: "PCH03",
						fileName: "納品書",
					}

					//完成后是否更新确认

					that.PrintTool.printBackActionPo(that,oData,sapPo)


				})

			})
		},
		/**
		 * 纳品书中转站
		 * @param {*} ObList 
		 * @param {*} uniqueIdList 
		 * @param {*} myMap 
		 * @param {*} myZABCMap 
		 * @param {*} that 
		 * @param {*} boo 
		 * @param {*} list 
		 * @returns 
		 */
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
				that._setBusy(false);

			})


		},

		onPrintZwsNps: function () {
			var that = this;

			//设置通用dialog
			this._AfterDigLogCheck(true).then((selectedIndices) => {
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
						zipFolder: zip.folder(),
						zipFile: [],

					};
					that._onZWSprintTy(selectedIndices, uniqueIdList, myMap, myZABCMap, that, false);

					that._onNPSprintTy(selectedIndices, uniqueIdList, myMap, myZABCMap, that, false);

					that._isQuerenDb(selectedIndices, "zws");
				}
			})
		},
		/**
		 * 确认的调用后台
		 * @param {po和明细} pList 
		 */
		_querenDb(pList) {
			var that = this;
			if(pList.length>0){
				that._callCdsAction(_objectCommData._entity2, { parms: JSON.stringify(pList) }, that).then(

					function (odata) {
						that.byId("smartTable").rebindTable();
						that._setBusy(false);
					},
					function (error) {
						that.MessageTools._addMessage(error.responseText, null, null, that.getView());
	
					}
				)
			}

		},

		/**
		 * 判断是否调用后台
		 */
		_isQuerenDb(tableList, str) {
			var pList = Array();
			tableList.forEach((item) => {
				if ("zws" == str) {
					//如果是zws,条件：1且！=w，条件 2且=w
					if ('2' == item.USER_TYPE && item.ZABC == 'W' || ('1' == item.USER_TYPE && item.ZABC != 'W')) {
						var p = {
							po: item.PO_NO,
							dNo: item.D_NO,
							t: "t"
						}
						pList.push(p)
					}else//如果是zws,条件：1且！=w，条件 只更新状态
					if ('2' == item.USER_TYPE  || ('1' == item.USER_TYPE && item.ZABC != 'W')) {
						var p = {
							po: item.PO_NO,
							dNo: item.D_NO,
						}
						pList.push(p)
					}
					
				}
				//为eml时，为邮件调用，false为下载调用
				if ("eml" == str) {
					var p = {
						po: item.PO_NO,
						dNo: item.D_NO,
						t: "t"
					}
					pList.push(p)
				}
				if ("zws" != str && "eml" != str) {
					if ('2' == item.USER_TYPE || ('1' == item.USER_TYPE && item.ZABC != 'W')) {
						var p = {
							po: item.PO_NO,
							dNo: item.D_NO,

						}
						pList.push(p)
					}
				}
			})
			this._querenDb(pList)
		},
	});
});