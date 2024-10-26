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

	return Controller.extend("umc.app.controller.pch.pch07_pocf_u", {
		formatter : formatter,

		onInit: function () {
			//获取本地信息
			var oMessageManager = sap.ui.getCore().getMessageManager();
			this.getView().setModel(oMessageManager.getMessageModel(), "message");
			oMessageManager.registerObject(this.getView(), true);

			this.getRouter().getRoute("RouteCre_pch07").attachPatternMatched(this._onRouteMatched, this);

			var oViewModel = new sap.ui.model.json.JSONModel({
				isButtonEnabled: false // 默认值为 true
			});
			this.getView().setModel(oViewModel, "viewModel");
			

			//  设置版本号
			this._setOnInitNo("PCH07", ".20240812.01");
		},

		// onDownloadTemplate: function() {
		// 	var sUrl = "/path/to/your/template/file.xlsx"; // 模板文件的路径
		// 	sap.m.URLHelper.redirect(sUrl, true); // 通过 URLHelper 重定向到文件 URL，进行下载
		// },
		

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
				sap.m.MessageBox.alert(that.MessageTools._getI18nTextInModel("pch", "PCH01_MSG_FILED_BLANK", this.getView()) );
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

						 // 更新按钮启用状态
						 that.getView().getModel("viewModel").setProperty("/isButtonEnabled", !hasError);
						 that.getModel("workInfo").setData(myArray.list); // 更新模型数据

						that._setBusy(false);

					},

					error: function (oError) {

						that._setBusy(false);
						
					}
				});
			}
		},

		// onSave: function () {
		// 	var that = this;
		// 	that._setBusy(true);
	
		// 	this._callCdsAction("/PCH07_SAVE_DATA", this.getData(), this).then((oData) => {
		// 	  var myArray = JSON.parse(oData.PCH01_SAVE_DATA);
		// 	  //that._setCnt(myArray.reTxt);
		// 	  var jsonModel = that.getModel("workInfo");
	
		// 	  jsonModel.setData(myArray.list);
		// 	  //设置为非创建
		// 	  that._setIsCreate(false);
	
		// 	  that._setBusy(false);
		// 	});
		//   },

		// onSave: function () {
		// 	var that = this;
		// 	that._setBusy(true);
		
		// 	// 检查按钮是否可用
		// 	if (that.getView().getModel("viewModel").getProperty("/isButtonEnabled")) {
		// 		this._callCdsAction("/PCH07_SAVE_DATA", this.getData(), this).then((oData) => {
		// 			var myArray = JSON.parse(oData.PCH01_SAVE_DATA);
		// 			var jsonModel = that.getModel("workInfo");
		
		// 			jsonModel.setData(myArray.list);
		// 			// 设置为非创建
		// 			that._setIsCreate(false);	
		// 			that._setBusy(false);
		// 		});
		// 	} else {
		// 		// 如果按钮不可用，显示提示
		// 		that._setBusy(false);
		// 		sap.m.MessageToast.show("请先完成检查并确保所有项成功。");
		// 	}
		// },

		onSave: function () {
			var that = this;
			that._setBusy(true);
		
			// 检查按钮是否可用
			if (that.getView().getModel("viewModel").getProperty("/isButtonEnabled")) {
				this._callCdsAction("/PCH07_SAVE_DATA", this.getData(), this).then((oData) => {
					var myArray = JSON.parse(oData.PCH01_SAVE_DATA);
					var jsonModel = that.getModel("workInfo");
		
					jsonModel.setData(myArray.list);
					that._setIsCreate(false);
					that._setBusy(false);
				});
			} else {
				// 如果按钮不可用，显示提示
				that._setBusy(false);
				sap.m.MessageToast.show("请先完成检查并确保所有项成功。");
			}
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
				var header = ["MATERIAL_NUMBER","PLANT_ID","BP_NUMBER","QTY","VALIDATE_START","VALIDATE_END","UMC_COMMENT_1","UMC_COMMENT_2","INITIAL_OBJ"];
				// 通过 XLSX 将sheet转为json  要转的oSheet，header标题，range起始行（1：第二行开始）
				var jsonS = XLSX.utils.sheet_to_json(oSheet,{header: header, range: 1});
				jsonModel.setData(jsonS);
			};
			oReader.readAsBinaryString(oFile);
			},

			onExport: function (oEvent) {
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
					oSettings.fileName = `購買見積登録.xlsx`;
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