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
            this._PchResourceBundle = this.getOwnerComponent().getModel("pch").getResourceBundle();

			var oViewModel = new sap.ui.model.json.JSONModel({
				isButtonEnabled: true // 默认值为 true
			});
			this.getView().setModel(oViewModel, "viewModel");


			//  设置版本号
			this._setOnInitNo("PCH07", ".20240812.01");
		},

		onDownloadTemplate: function () {
			var aColumns = [];
			var aExport=[];
			var oData = {};
			var that = this;
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
					sap.m.MessageToast.show(that._PchResourceBundle.getText("EXPORT_FINISHED"));
				})
				.finally(function () {
					oSheet.destroy();
				});
		},

		_onRouteMatched: function (oEvent) {
			//this._viewCreateSet(); 	
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

				jsonModel.setData(jsonS);
			};
			oReader.readAsArrayBuffer(oFile);
		},


		onExport: function (oEvent) {
			var that = this;
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
					sap.m.MessageToast.show(that._PchResourceBundle.getText("EXPORT_FINISHED"));
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