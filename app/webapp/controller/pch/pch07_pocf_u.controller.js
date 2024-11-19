sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/util/xlsx",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet",
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

             // 模板下载
         onSampleExport: function () {
            var downfilename = "購買見積登録テンプレート";
            var aCols = this.createSampleConfig();
            var json = [{
               D_NMATERIAL_NUMBERO: "",
			   CUST_MATERIAL:"",
			   MANUFACT_MATERIAL:"",
               PLANT_ID:"",
			   SALES_NUMBER:"",
               BP_NUMBER:"",
               QTY:"",
               VALIDATE_START:"",
               VALIDATE_END:"",
			   UMC_COMMENT_1:"",
			   UMC_COMMENT_2:"",
			   INITIAL_OBJ:"",
            }];
   
            var oModel = new sap.ui.model.json.JSONModel(json);
            var aProducts = oModel.getProperty('/');
            console.log("a " + aProducts);
            var oSettings = {
               workbook: { columns: aCols },
               dataSource: aProducts,
               fileName: downfilename
            };
            var oSheet = new sap.ui.export.Spreadsheet(oSettings);
            console.log("b " + oSheet);
            oSheet.build()
               .then(function () {
               }).finally(function () {
                  oSheet.destroy();
               });
         },

		 createSampleConfig: function (oEvt) {
			      var that = this;
			      var testJson={attachmentJson:[{
			        object:"template",
			        value:"購買見積登録テンプレート.xlsx"
			        }]}
			      $.ajax({
			        url: "srv/odata/v4/Common/s3DownloadAttachment",
			        type: "POST",
			        contentType: "application/json; charset=utf-8",
			        dataType: "json",
			        async: false,
			        crossDomain: true,
			        responseType: 'blob',
			        data: JSON.stringify(testJson),
			        success: function (base64) {
			          const downloadLink = document.createElement("a");
			          const blob = that._base64Blob(base64.value, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			          const blobUrl = URL.createObjectURL(blob);
			          downloadLink.href = blobUrl;
			          downloadLink.download = "購買見積登録テンプレート.xlsx";//data.FILE_NAME + "." + data.FILE_TYPE;
			          downloadLink.click();
			        }
			      })
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

				// if (!item.MATERIAL_NUMBER || item.MATERIAL_NUMBER === "") {
				// 	//missingFields.push("SAP品目コード"); // SAP品目コード
				// 	missingFields.push("SAP品目コード");
				// }

                // 先检查 MATERIAL_NUMBER 和 CUST_MATERIAL 字段是否至少有一个有值
				// if ((!item.MATERIAL_NUMBER || item.MATERIAL_NUMBER === "") && 
				// (!item.CUST_MATERIAL || item.CUST_MATERIAL === "")) {
				// missingFields.push("SAP 品目コード、図面品番は少なくとも一つを入力してください。");
		    	// }

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

		onSave: function () {
			var that = this;
			that._setBusy(true);
		
			// 检查按钮是否可用
			if (that.getView().getModel("viewModel").getProperty("/isButtonEnabled")) {
				this._callCdsAction("/PCH07_SAVE_DATA", this.getData(), this).then((oData) => {
					var myArray = JSON.parse(oData.PCH07_SAVE_DATA);
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
				var header = ["","MATERIAL_NUMBER","CUST_MATERIAL","MANUFACT_MATERIAL","PLANT_ID","SALES_NUMBER","BP_NUMBER","QTY","VALIDATE_START","VALIDATE_END","UMC_COMMENT_1","UMC_COMMENT_2","INITIAL_OBJ"];
				// 通过 XLSX 将sheet转为json  要转的oSheet，header标题，range起始行（1：第二行开始）
				var jsonS = XLSX.utils.sheet_to_json(oSheet,{header: header, range: 6});

				jsonS.forEach(function (row) {
					if (row.VALIDATE_START && !isNaN(row.VALIDATE_START)) {
						// 转换 Excel 日期序列号为实际日期
						row.VALIDATE_START = that._convertExcelDate(row.VALIDATE_START,'YYYY/MM/DD');
					}
					if (row.VALIDATE_END && !isNaN(row.VALIDATE_END)) {
						// 转换 Excel 日期序列号为实际日期
						row.VALIDATE_END = that._convertExcelDate(row.VALIDATE_END,'YYYY/MM/DD');
					}
				});
				

				jsonModel.setData(jsonS);
			};
			oReader.readAsBinaryString(oFile);
			},



			onExport: function () {
				    var aCols, oRowBinding, oSettings, oSheet, oTable;
				 
				    if (!this._oTable) {
				     this._oTable = this.byId('tableUploadData');
				    }
				 
				    oTable = this._oTable;
				    oRowBinding = oTable.getBinding('rows');
				    if (oRowBinding != undefined) {
				     console.log(oRowBinding);
				 
				     aCols = this.createColumnConfig();
				     var oModel = oRowBinding.getModel('resultSet').getProperty('/');
				     console.log(oModel);
				     for (var i = 0; i < oModel.length; i++) {
				      // oModel[i].MODIFIEDDAT = this.formatDate(oModel[i].MODIFIEDDAT)
				      // oModel[i].SD07_RESULT = this.formateType(oModel[i].SD07_RESULT)
				     }
				     oSettings = {
				      workbook: {
				       columns: aCols,
				       hierarchyLevel: 'Level'
				      },
				      dataSource: oModel,
				      fileName: '購買見積登録_' + formatter.formatDateForFileName(new Date()) + '.xlsx', // 调用 formatter
				      worker: false // We need to disable worker because we are using a MockServer as OData Service
				     };
				  
				     oSheet = new sap.ui.export.Spreadsheet(oSettings);
				     oSheet.build().finally(function () {
				      oSheet.destroy();
				     });
				    }
				    
				   },
				
				   createColumnConfig: function () {
				    var aCols = [];
				 
				    aCols.push({
				     label: 'ステータス',
				     property: 'STATUS'
				    });
				 
				    aCols.push({
				     label: '実行結果',
				     property: 'RESULT'
				    });
				 
				    aCols.push({
				     label: '結果内容',
				     property: 'MESSAGE'
				    });
				 
				    aCols.push({
				     label: '購買見積番号',
				     property: 'QUO_NUMBER'
				    });
				 
				    aCols.push({
				     label: '管理No',
				     property: 'QUO_ITEM',
				 
				    });
				 
				    aCols.push({
				     label: 'SAP品目コード',
				     property: 'MATERIAL_NUMBER'
				    });

					aCols.push({
						label: '顧客品番',
						property: 'CUST_MATERIAL'
					   });

					   aCols.push({
						label: 'メーカー品番',
						property: 'MANUFACT_MATERIAL'
					   });

				    aCols.push({
				     label: '仕入先',
				     property: 'BP_NUMBER'
				    });

					aCols.push({
						label: 'プラント',
						property: 'PLANT_ID'
					   });

					   aCols.push({
						label: '販売見積案件No',
						property: 'SALES_NUMBER'
					   });

					aCols.push({
				     label: '数量',
				     property: 'QTY'
				    });
				 
			        aCols.push({
						label: '有効開始日付',
						property: 'VALIDATE_START'
					   });

					aCols.push({
				     label: '有効終了日付',
				     property: 'VALIDATE_END'
				    });

					aCols.push({
						label: 'UMC購買コメント1',
						property: 'UMC_COMMENT_1'
					   });

                    aCols.push({
						label: 'UMC購買コメント2',
						property: 'UMC_COMMENT_2'
					   });

					aCols.push({
						label: 'イニシャル費用対象',
						property: 'INITIAL_OBJ'
					   });

					    return aCols;
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