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

	return Controller.extend("umc.app.controller.pch.pch01_pocf_u", {
		formatter : formatter,

		onInit: function () {
			//获取本地信息
			var oMessageManager = sap.ui.getCore().getMessageManager();
			this.getView().setModel(oMessageManager.getMessageModel(), "message");
			oMessageManager.registerObject(this.getView(), true);

			this.getRouter().getRoute("RouteCre_pch01").attachPatternMatched(this._onRouteMatched, this);

			var oViewModel = new sap.ui.model.json.JSONModel({
				isButtonEnabled: true // 默认值为 true
			});
			this.getView().setModel(oViewModel, "viewModel");
			

			//  设置版本号
			this._setOnInitNo("PCH01", ".20240812.01");
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
		
			//2. 必输字段检查   ・必須入力データは「発注番号」、「明細番号」、「納品日」、「納品数量」
			var hasError = false;

			data.forEach(function (item) {
				var missingFields = [];
				var deliveryDateError;

				if (!item.PO_NO || item.PO_NO === "") {
					//missingFields.push("発注番号"); // 発注番号
					missingFields.push("発注番号");
				}
				if (!item.D_NO || item.D_NO === "") {
					//missingFields.push("明細番号"); // 明細番号
					missingFields.push("明細番号");
				}
				if (!item.DELIVERY_DATE || item.DELIVERY_DATE === "" || isNaN(Date.parse(item.DELIVERY_DATE))) {
					//missingFields.push("納品日"); // 納品日
					missingFields.push("納品日");
				}

				var datePattern = /^\d{4}\/\d{2}\/\d{2}$/; // 正则表达式，确保日期格式为 YYYY/MM/DD
				if (item.DELIVERY_DATE && (!datePattern.test(item.DELIVERY_DATE) || isNaN(Date.parse(item.DELIVERY_DATE)))) {
					missingFields.push("納品日の形式が不正です"); // 推送日期格式错误
					deliveryDateError = true; // 标记为纳品日有错
				}

				if (!item.QUANTITY || item.QUANTITY === "") {
					//missingFields.push("納品数量"); // 納品数量
					missingFields.push("納品数量");
				}

				if (missingFields.length > 0) {
					item.MSG_TEXT = "必須項目は入力していません。データをチェックしてください。"; // 将错误消息设置到对应行的MSG_TEXT字段
					
					if (deliveryDateError) {
						item.MSG_TEXT = "日付形式はYYYY/MM/DDではないので、調整してください"
					}
						
					item.STATE = "Error"; // 将状态设置为错误状态
        			item.I_CON = "sap-icon://error"; // 设置状态图标为错误图
					jsonModel.refresh(); // 刷新模型以更新UI
					that._setBusy(false);
					hasError = true;//有错
					return;
				}
			});


		
			// 3. 如果所有检查都通过，调用服务
			if (checkResult && flgHeand && !hasError) {
				this.getModel().callFunction("/PCH01_CHECK_DATA", {
					method: "POST",
					urlParameters: this.getData(),
					success: function (result) {
						// 取数据
						var arr = result.PCH01_CHECK_DATA;
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
	
			this._callCdsAction("/PCH01_SAVE_DATA", this.getData(), this).then((oData) => {
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
				type: "binary",
				});
				//获得 sheet
				var oSheet = oWB.Sheets[oWB.SheetNames[0]];
				//设置头
				var header = ["PO_NO","D_NO","MAT_ID","PO_D_TXZ01","PO_PUR_QTY","PO_PUR_UNIT","SUPPLIER_MAT","DELIVERY_DATE","QUANTITY","REFERTO"];   //,"CUSTOMER_MAT" 存疑
				// 通过 XLSX 将sheet转为json  要转的oSheet，header标题，range起始行（1：第二行开始）
				var jsonS = XLSX.utils.sheet_to_json(oSheet,{header: header, range: 1});
				
				jsonS.forEach(function (row) {
				if (row.DELIVERY_DATE && !isNaN(row.DELIVERY_DATE)) {
						// 转换 Excel 日期序列号为实际日期
						row.DELIVERY_DATE = that._convertExcelDate(row.DELIVERY_DATE);
					}
				});
				
				
				jsonModel.setData(jsonS);
			};
			oReader.readAsBinaryString(oFile);
		},
		
		// 辅助函数：将 Excel 日期序列号转换为实际日期
		_convertExcelDate: function (serial) {
			var excelEpoch = new Date(1899, 11, 31); // Excel的基准日期
			return new Date(excelEpoch.getTime() + serial * 24 * 60 * 60 * 1000).toISOString().split('T')[0]; // 返回 YYYY-MM-DD 格式
		},

			
			
			getData: function () {
				var jsondata = this.getModel("workInfo").getData();
				var a = JSON.stringify({ list: jsondata });
				var oPrams = {
				  shelfJson: a,
				};
				return oPrams;
			  },


	});

});