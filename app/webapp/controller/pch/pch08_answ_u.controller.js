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
				// var header = ["QUO_NUMBER", "QUO_ITEM", "NO",
				// 	'REFRENCE_NO', 'CUSTOMER', 'MACHINE_TYPE', 'QUANTITY', 'VALIDATE_START', 'VALIDATE_END', 'MATERIAL_NUMBER', 'CUST_MATERIAL',
				// 	'MANUFACT_MATERIAL', 'Attachment', 'Material', 'MAKER', 'UWEB_USER', 'BP_NUMBER', 'PERSON_NO1', 'PERSON_NO2', 'PERSON_NO3',
				// 	'PERSON_NO4', 'PERSON_NO5', 'YLP', 'MANUL', 'MANUFACT_CODE', 'CUSTOMER_MMODEL', 'MID_QF', 'SMALL_QF', 'OTHER_QF',
				// 	'CURRENCY', 'PRICE', 'PRICE_CONTROL', 'LEAD_TIME', 'MOQ', 'UNIT', 'SPQ', 'KBXT', 'PRODUCT_WEIGHT', 'ORIGINAL_COU',
				// 	'EOL', 'ISBOI', 'Incoterms', 'Incoterms_Text', 'MEMO1', 'MEMO2', 'MEMO3', 'SL', 'TZ', 'RMATERIAL', 'RMATERIAL_CURRENCY',
				// 	'RMATERIAL_PRICE', 'RMATERIAL_LT', 'RMATERIAL_MOQ', 'RMATERIAL_KBXT', 'UMC_COMMENT_1', 'UMC_COMMENT_2', 'SUPPLIER_MAT'];
				// // 通过 XLSX 将sheet转为json  要转的oSheet，header标题，range起始行（1：第二行开始）
				// var jsonS = XLSX.utils.sheet_to_json(oSheet, { header: header, range: 1, raw: true });

				var sExcelJson = XLSX.utils.sheet_to_json(oSheet, {
					header: 1,  // 使用数组形式
					defval: null  // 空值处理
				});

				if (!sExcelJson || sExcelJson === '') {
					return;
				}

				delete sExcelJson[0]; // 删除第一行

				var aExcelData = [];
				var aDynamicData = [];
				//rebuild json
				sExcelJson.forEach(row => {
					var length = row.length;
					var oItem = {};
					oItem["QUO_NUMBER"] = row[0];
					oItem["QUO_ITEM"] = row[1];
					oItem["NO"] = row[2];
					oItem['REFRENCE_NO'] = row[3];
					oItem['CUSTOMER'] = row[4];
					oItem['MACHINE_TYPE'] = row[5];
					oItem['QUANTITY'] = row[6];
					oItem['Item'] = row[7];
					oItem['TIME'] = row[8];
					oItem['LOCATION'] = row[9];
					oItem['VALIDATE_START'] = row[10];
					oItem['VALIDATE_END'] = row[11];
					oItem['MATERIAL_NUMBER'] = row[12];
					oItem['CUST_MATERIAL'] = row[13];
					oItem['MANUFACT_MATERIAL'] = row[14];
					oItem['Attachment'] = row[15];
					oItem['Material'] = row[16];
					oItem['MAKER'] = row[17];
					oItem['UWEB_USER'] = row[18];
					oItem['BP_NUMBER'] = row[19];
					oItem['PERSON_NO1'] = row[20];
					oItem['PERSON_NO2'] = row[21];
					oItem['PERSON_NO3'] = row[22];
					oItem['PERSON_NO4'] = row[23];
					oItem['PERSON_NO5'] = row[24];
					oItem['YLP'] = row[25];
					oItem['MANUL'] = row[26];
					oItem['MANUFACT_CODE'] = row[27];
					oItem['CUSTOMER_MMODEL'] = row[28];
					oItem['MID_QF'] = row[29];
					oItem['SMALL_QF'] = row[30];
					oItem['OTHER_QF'] = row[31];
					oItem['CURRENCY'] = row[32];
					oItem['PRICE_CONTROL'] = row[33];
					oItem['LEAD_TIME'] = row[34];
					oItem['MOQ'] = row[35];
					oItem['UNIT'] = row[36];
					oItem['SPQ'] = row[37];
					oItem['KBXT'] = row[38];
					oItem['PRODUCT_WEIGHT'] = row[39];
					oItem['ORIGINAL_COU'] = row[40];
					oItem['EOL'] = row[41];
					oItem['ISBOI'] = row[42];
					oItem['Incoterms'] = row[43];
					oItem['Incoterms_Text'] = row[44];
					oItem['MEMO1'] = row[45];
					oItem['MEMO2'] = row[46];
					oItem['MEMO3'] = row[47];
					oItem['SL'] = row[48];
					oItem['TZ'] = row[49];
					oItem['RMATERIAL'] = row[50];
					oItem['RMATERIAL_CURRENCY'] = row[51];
					oItem['RMATERIAL_PRICE'] = row[52];
					oItem['RMATERIAL_LT'] = row[53];
					oItem['RMATERIAL_MOQ'] = row[54];
					oItem['RMATERIAL_KBXT'] = row[55];
					oItem['UMC_COMMENT_1'] = row[56];
					oItem['UMC_COMMENT_2'] = row[57];
					oItem['SUPPLIER_MAT'] = row[58];

					var oDynamicData = {};
					oDynamicData["QUO_NUMBER"] = row[0];
					oDynamicData["QUO_ITEM"] = row[1];
					oDynamicData["NO"] = row[2];

					var iIndex = 0;
					for (var i = 58; i < length; i += 2) {
						iIndex++;
						let sValue = row[i];

						if (!sValue) {
							continue;
						}

						let nValue = Number(sValue);
						if (isNaN(nValue) || nValue <= 0) {
							continue;
						}

						oItem["QTY_" + iIndex] = nValue;
						oDynamicData["QTY_" + iIndex] = oItem["QTY_" + iIndex];
					}

					iIndex = 0;
					for (var i = 59; i < length; i += 2) {
						iIndex++;
						let sValue = row[i];

						if (!sValue) {
							continue;
						}

						let nValue = Number(sValue);
						if (isNaN(nValue) || nValue <= 0) {
							continue;
						}

						oItem["PRICE_" + iIndex] = nValue;
						oDynamicData["PRICE_" + iIndex] = oItem["PRICE_" + iIndex];
					}

					aExcelData.push(oItem);
					aDynamicData.push(oDynamicData);
				});

				var jsonS = aExcelData;//JSON.stringify(aExcelData);

				debugger;

				const sConvertedData = jsonS.map(row => {
					const newRow = { ...row };

					if (newRow.VALIDATE_START) {
						newRow.VALIDATE_START = that.__formatExcelDate(newRow.VALIDATE_START);
					}

					if (newRow.VALIDATE_END) {
						newRow.VALIDATE_END = that.__formatExcelDate(newRow.VALIDATE_END);
					}

					if (newRow.TIME) {
						newRow.TIME = that.__formatExcelDate(newRow.TIME);
					}

					return newRow;
				});

				jsonModel.setData(sConvertedData);
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

				if (row.TIME && row.TIME !== '') {
					row.TIME = new Date(row.TIME);
				}
			})

			var aColumns = oTable.getColumns().map(function (oColumn) {
				var sDateFormat = 'yyyy-MM-dd';
				var sFormat = '';
				var sType = '';

				var property = oColumn.getTemplate().getBindingPath("text");

				if (property === 'VALIDATE_START' || property === 'VALIDATE_END' || property === 'TIME') {
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


	});

});