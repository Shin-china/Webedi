sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet",
	"umc/app/util/xlsx",
	"sap/m/MessageToast",
	'sap/ui/comp/library',
	'sap/ui/model/type/String',

], function (Controller, Filter, xlsx, formatter,
	Spreadsheet) {
	"use strict";

	return Controller.extend("umc.app.controller.pch.pch08_answ_u", {
		formatter: formatter,

		onInit: function () {
			//获取本地信息
			var oMessageManager = sap.ui.getCore().getMessageManager();
			this.getView().setModel(oMessageManager.getMessageModel(), "message");
			oMessageManager.registerObject(this.getView(), true);

			this.getRouter().getRoute("RouteCre_pch07").attachPatternMatched(this._onRouteMatched, this);

			var oViewModel = new sap.ui.model.json.JSONModel({
				isButtonEnabled: true // 默认值为 true
			});
			this.getView().setModel(oViewModel, "viewModel");


			//  设置版本号
			this._setOnInitNo("PCH07", ".20240812.01");
		},

		onDownloadTemplate: function () {
			// var sUrl = "/path/to/your/template/file.xlsx"; // 模板文件的路径
			// sap.m.URLHelper.redirect(sUrl, true); // 通过 URLHelper 重定向到文件 URL，进行下载
			var oExportModel = new sap.ui.model.json.JSONModel([]);
			var aColumns = [];
			var aExport=[];
			var oData = {};
			oData.QUO_NUMBER = "";
			aExport.push(oData);
			aColumns.push({
				label: "QUO_NUMBER",
				type: "string",
				property:"QUO_NUMBER",
				width: 10
			});

			var oSettings = {
				dataSource: aExport,
				workbook: {
					columns: aColumns,

					context: {
						sheetName: "購買見積回答"
					}
				}
			};

			var oSheet = new sap.ui.export.Spreadsheet(oSettings); 

			oSheet.build()
				.then(function () {
					sap.m.MessageToast.show(this._ResourceBundle.getText("exportFinished"));
				})
				.finally(function () {
					oSheet.destroy();
				});
		},


		uploadButtonPress(oEvent) {
			this._viewCreateSet();
			const model = this.getView().getModel("workInfo");
			model.setData(oEvent.getParameter("rawData"));
		},

		_onRouteMatched: function (oEvent) {
			//this._viewCreateSet(); 	
		},

		onCheck: function () {
			var flgHeand = true;
			var checkResult = true;

			var that = this;
			that._setBusy(true);
			that._setEditable(true);

			var jsonModel = that.getModel("workInfo");
			var data = jsonModel.getData();

			// 1. 检查模板是否有数据
			if (jsonModel.oData.length === undefined) {
				sap.m.MessageBox.alert(that.MessageTools._getI18nTextInModel("pch", "PCH01_MSG_FILE_BLANK", this.getView()));
				that._setBusy(false);
				return;
			}

			//2. 必输字段检查   ・必須入力データは「SAP品目コード」、「仕入先」、「プラント」、「数量」、「有効開始日付」、「有効終了日付」
			var hasError = false;

			data.forEach(function (item) {
				var missingFields = [];

				if (!item.MATERIAL_NUMBER || item.MATERIAL_NUMBER === "") {
					//missingFields.push("SAP品目コード"); // SAP品目コード
					missingFields.push("SAP品目コード");
				}
				if (!item.BP_NUMBER || item.BP_NUMBER === "") {
					//missingFields.push("仕入先"); // 仕入先
					missingFields.push("仕入先");
				}
				if (!item.PLANT_ID || item.PLANT_ID === "") {
					//missingFields.push("プラント"); // プラント
					missingFields.push("プラント");
				}
				if (!item.QTY || item.QTY === "") {
					//missingFields.push("数量"); // 数量
					missingFields.push("数量");
				}
				if (!item.VALIDATE_START || item.VALIDATE_START === "") {
					//missingFields.push("有効開始日付"); // 有効開始日付
					missingFields.push("有効開始日付");
				}
				if (!item.VALIDATE_END || item.VALIDATE_END === "") {
					//missingFields.push("有効終了日付"); // 有効終了日付
					missingFields.push("有効終了日付");
				}

				if (missingFields.length > 0) {
					item.MSG_TEXT = missingFields.join("\n"); // 将错误消息设置到对应行的MSG_TEXT字段

					item.STATE = "Error"; // 将状态设置为错误状态
					item.I_CON = "sap-icon://error"; // 设置状态图标为错误图
					jsonModel.refresh(); // 刷新模型以更新UI
					that._setBusy(false);
					hasError = true;//有错
					return;
				}
			});
			//如果 任意一行message里有消息，则弹出消息。
			if (hasError) {
				sap.m.MessageBox.alert(that.MessageTools._getI18nTextInModel("pch", "PCH01_MSG_FILED_BLANK", this.getView()));
				that._setBusy(false);
				return;
			}

			// 3. 如果所有检查都通过，调用服务
			if (checkResult && flgHeand) {
				this.getModel().callFunction("/PCH07_CHECK_DATA", {
					method: "POST",
					urlParameters: this.getData(),
					success: function (result) {
						// 取数据
						var arr = result.PCH07_CHECK_DATA;
						var myArray = JSON.parse(arr);

						// 设置画面上总结
						//that._setCnt(myArray.reTxt);

						// 更新画面上的model
						jsonModel.setData(myArray.list);

						myArray.list.forEach(function (item) {
							if (item.SUCCESS == false) { // 根据实际数据结构判断是否存在错误字段
								hasError = true; // 后台检查有错误
							}
						});

						if (hasError) {
							// that._setEditable(false);
							that.getView().getModel("viewModel").setProperty("/isButtonEnabled", false);

						}

						that._setBusy(false);

					},

					error: function (oError) {

						that._setBusy(false);

					}
				});
			}
		},

		onSave: function () {
			var that = this;
			that._setBusy(true);

			this._callCdsAction("/PCH07_SAVE_DATA", this.getData(), this).then((oData) => {
				var myArray = JSON.parse(oData.PCH01_SAVE_DATA);
				//that._setCnt(myArray.reTxt);
				var jsonModel = that.getModel("workInfo");

				jsonModel.setData(myArray.list);
				//设置为非创建
				that._setIsCreate(false);

				that._setBusy(false);
			});
		},

		// excel上传
		onFileChange: function (oEvent) {
			this._setEditable(true);
			var oFileUploader = this.byId("fileUploader");

			var that = this;
			var view = this.getView();
			var jsonModel = view.getModel("workInfo");
			if (!jsonModel) {
				jsonModel = new sap.ui.model.json.JSONModel();
				view.setModel(jsonModel, "workInfo");
			}
			var oFile = oFileUploader.FUEl.files[0];
			var oReader = new FileReader();
			oReader.onload = function (oFileData) {
				var sResult = oFileData.target.result;
				var oWB = XLSX.read(sResult, {
					type: "binary"
				});
				//获得 sheet
				var oSheet = oWB.Sheets[oWB.SheetNames[0]];
				//设置头
				var header = ['QUO_NUMBER', 'QUO_ITEM', 'NO', 'SALES_NUMBER', 'SALES_D_NO',
					'REFRENCE_NO', 'CUSTOMER', 'MACHINE_TYPE', 'QUANTITY', "ITEM","TIME","LOCATION",
					'VALIDATE_START', 'VALIDATE_END', 'MATERIAL_NUMBER', 'CUST_MATERIAL',
					'MANUFACT_MATERIAL', 'Attachment', 'Material', 'MAKER', 'UWEB_USER', 'BP_NUMBER', 
					'YLP', 'MANUL', 'MANUFACT_CODE', 'CUSTOMER_MMODEL', 'MID_QF', 'SMALL_QF', 'OTHER_QF',
					'CURRENCY', 'QTY', 'PRICE', 'PERSON_NO1', 'PRICE_CONTROL', 'LEAD_TIME', 'MOQ', 'UNIT', 'SPQ', 
					'KBXT', 'PRODUCT_WEIGHT', 'ORIGINAL_COU','EOL', 'ISBOI', 'Incoterms', 'Incoterms_Text', 'MEMO1', 
					'MEMO2', 'MEMO3', 'SL', 'TZ', 'RMATERIAL', 'RMATERIAL_CURRENCY', 'RMATERIAL_PRICE', 'RMATERIAL_LT', 
					'RMATERIAL_MOQ', 'RMATERIAL_KBXT', 'UMC_COMMENT_1', 'UMC_COMMENT_2', 'SUPPLIER_MAT'];
				// 通过 XLSX 将sheet转为json  要转的oSheet，header标题，range起始行（1：第二行开始）
				var jsonS = XLSX.utils.sheet_to_json(oSheet, { header: header, range: 1, raw: true });

				jsonS.forEach(row=>{
					row.STATUS = 'Information';
					row.ICON = 'sap-icon://pending';
				})

				// const sConvertedData = jsonS.map(row => {
				// 	const newRow = { ...row };

				// 	if (newRow.VALIDATE_START) {
				// 		newRow.VALIDATE_START = that.__formatExcelDate(newRow.VALIDATE_START);
				// 	}

				// 	if (newRow.VALIDATE_END) {
				// 		newRow.VALIDATE_END = that.__formatExcelDate(newRow.VALIDATE_END);
				// 	}

				// 	return newRow;
				// });

				//jsonModel.setData(sConvertedData);
				jsonModel.setData(jsonS);
			};
			oReader.readAsBinaryString(oFile);
		},

		__formatExcelDate: function (date) {
			if (!date || date === '') {
				return;
			}

			const excelEpoch = new Date(1899, 11, 30);
			const msPerDay = 24 * 60 * 60 * 1000;
			const dNewDate = new Date(excelEpoch.getTime() + (date * msPerDay));
			const sNewdate = dNewDate.toISOString().split("T")[0];

			return sNewdate;
		},

		onExport: function (oEvent) {
			var oTable = this.getView().byId("tableUploadData");
			var aSelectedIndices = oTable.getSelectedIndices();

			if (aSelectedIndices.length === 0) {
				sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
				oEvent.preventDefault(); // 取消导出操作
				return;
			}

			var aSelectedData = aSelectedIndices.map(function (iIndex) {
				return oTable.getContextByIndex(iIndex).getObject();
			});

			if (aSelectedData.length === 0) {
				sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
				return;
			}

			aSelectedData.forEach(row => {
				if (row.VALIDATE_START && row.VALIDATE_START !== '') {
					row.VALIDATE_START = new Date(row.VALIDATE_START);
				}

				if (row.VALIDATE_END && row.VALIDATE_END !== '') {
					row.VALIDATE_END = new Date(row.VALIDATE_END);
				}
			})

			var aColumns = oTable.getColumns().map(function (oColumn) {
				var sDateFormat = 'yyyy-MM-dd';
				var sFormat = '';
				var sType = '';

				var property = oColumn.getTemplate().getBindingPath("text");

				if (property === 'VALIDATE_START' || property === 'VALIDATE_END') {
					sFormat = sDateFormat;
					sType = 'date';
				} else {
					sFormat = '';
					sType = 'string';
				}


				return {
					label: oColumn.getLabel().getText(),
					type: sType,
					property: oColumn.getTemplate().getBindingPath("text"),
					width: parseFloat(oColumn.getWidth()),
					format: sFormat
				};
			});

			var oSettings = {
				dataSource: aSelectedData,
				workbook: {
					columns: aColumns,

					context: {
						sheetName: "購買見積登録"
					}
				}
			};

			var oSheet = new sap.ui.export.Spreadsheet(oSettings);
			oSheet.attachBeforeExport(this.onBeforeExport.bind(this));
			oSheet.build()
				.then(function () {
					sap.m.MessageToast.show(this._ResourceBundle.getText("exportFinished"));
				})
				.finally(function () {
					oSheet.destroy();
				});
		},

		onBeforeExport: function (oEvent) {
			var oTable = this.getView().byId("tableUploadData");
			var aSelectedIndices = oTable.getSelectedIndices();

			if (aSelectedIndices.length === 0) {
				sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
				oEvent.preventDefault(); // 取消导出操作
				return;
			}

			var oSettings = oEvent.getParameter("exportSettings");
			if (oSettings) {
				console.log("onBeforeExport called");
				console.log("Export Settings:", oSettings);
				var oDate = new Date();
				var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
				var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
				// 设置文件名为当前日期和时间
				oSettings.fileName = `購買見積登録_${sDate}${sTime}.xlsx`;
			}
		},


		getData: function () {
			var jsondata = this.getModel("workInfo").getData();
			var a = JSON.stringify({ list: jsondata });
			var oPrams = {
				ShelfJson: a,
			};
			return oPrams;
		},

		onUploadCheck: function (oEvent) {
			this._uploadData(true);
		},

		onUploadExecute: function(oEvent){
			this._uploadData(false);
		},

		_uploadData :function(bTestRun){
			var that = this;
			that._setBusy(true);
			that._setEditable(true);

			var jsonModel = that.getModel("workInfo");
			var aData = jsonModel.getData();

			// 1. 检查模板是否有数据
			if (aData.length === undefined) {
				sap.m.MessageBox.alert(that.MessageTools._getI18nTextInModel("pch", "PCH01_MSG_FILE_BLANK", this.getView()));
				that._setBusy(false);
				return;
			}

			const oTable = this.getView().byId("tableUploadData");
			const aSelectedIndices = oTable.getSelectedIndices();

			if (aSelectedIndices.length === 0) {
				sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); 
				return;
			}

			const sAction = bTestRun? "PCH08_UPLOAD_CHECK" : "PCH08_UPLOAD_EXECUTE";
			const sActionPath = `/${sAction}`;
			 
			//2. Param长度有限制，必须循环一条条调用
			for(var i = 0; i < aSelectedIndices.length; i++){ 
				var sPost = { param : JSON.stringify(aData[aSelectedIndices[i]])}
				let sBindPath = `/${i}`;
				this._callCdsAction(sActionPath, sPost, this).then((oData) => {
					var sResult = JSON.parse(oData[sAction]);
					
					if(sResult.STATUS === 'E'){
						jsonModel.getContext(sBindPath).setProperty('STATUS','Error');
						jsonModel.getContext(sBindPath).setProperty('ICON', 'sap-icon://error'); 
					}else{
						jsonModel.getContext(sBindPath).setProperty('STATUS','Success');
						jsonModel.getContext(sBindPath).setProperty('ICON', 'sap-icon://message-success');
					}

					jsonModel.getContext(sBindPath).setProperty('MESSAGE', sResult.MESSAGE);

					jsonModel.setData(aData);
					that._setIsCreate(false);

					that._setBusy(false);
				});
			}
		}


	});

});