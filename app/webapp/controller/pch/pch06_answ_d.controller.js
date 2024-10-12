sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch06_answ_d", {
		formatter : formatter,

		onInit: function () {
			// 设置自己的 OData模型为默认模型
			this._setDefaultDataModel("TableService");
			//  设置版本号
			this._setOnInitNo("PCH01", ".20240812.01");
			this.MessageTools._clearMessage();
			this.MessageTools._initoMessageManager(this);

			this.getRouter().getRoute("RouteCre_pch06").attachPatternMatched(this._onRouteMatched, this);
		},
		/*==============================
		检索
		==============================*/
		onJS: function (oEvent) {
			var that = this;
			
			var oHeaderContext = this.getView().getBindingContext();
			var oHeaderObject = oHeaderContext.getObject();
			this._poNO = oHeaderObject.PO_NO;
			this._setEditable(false);
			that.byId("smartTable").rebindTable();
			
		},	
		/*==============================
		删除
		==============================*/
		onDelete: function (oEvent) {
			
			this._setEditable(true);
		},	
		/*==============================
		复制
		==============================*/
		onCop: function (oEvent) {
			var that = this;
			var selectedIndices = this._TableList("detailTable"); // 获取选中行
			

			
			this._setEditable(true);
		},
		/*==============================
		编辑
		==============================*/
		onEdi: function (oEvent) {
			if(this._poNO){

			
			      //消除更改记录
				  var that = this;
				  this.getModel().resetChanges();
				  this._setBusy(true);
				  var sHeaderObject = this.getView().getBindingContext().getObject();
				  that._editDraft2("/PCH_T03_PO_C_draftEdit",this._poNO,true).then((oData) => {
					var sPath = that.getModel().createKey("/PCH_T03_PO_C", {
					  PO_NO: sHeaderObject.PO_NO,
					  IsActiveEntity: oData.IsActiveEntity,
					});
					that._bindViewData(sPath);
					that._setEditable(true);
					that.getModel().refresh(true);
					that.byId("smdetailTable").rebindTable();
				  }).catch((oError) => {
					that._addMessage(that._getI18nText("MSG_EDIT"), null);
				  }).finally(() => {
					that._setBusy(false);
				  });
				}
		},
		/*==============================
		保存
		==============================*/
		onSav: function (oEvent) {
			
			this._setEditable(false);
		},
		/*==============================
		==============================*/
		_onRouteMatched: function (oEvent) {
			this._poNO = "";
			this._setEditableAuth(true);
			this._setEditable(false);

			            //新建
						var newHeaderContext = this.getModel().createEntry("/PCH_T03_PO_C", {

							properties: {
							  
							},
							groupId: "createEntry",
						  });
						  this.getView().setBindingContext(newHeaderContext);

			
		},
		onRebind: function (oEvent) {
			//直过滤
			var oBindingParams = oEvent.getParameter("bindingParams");

			if (this._poNO != "" && this._poNO != null) {

				oBindingParams.filters.push(new sap.ui.model.Filter("PO_NO", "EQ", this._poNO));
			} else {
				oBindingParams.filters.push(new sap.ui.model.Filter("PO_NO", "EQ", "1111111111111"));
			}
			oBindingParams.parameters.expand = "TO_PCH_T01,TO_PCH_T02";
	   
		},
	
		onBeforeExport: function (oEvt) {
		var mExcelSettings = oEvt.getParameter("exportSettings");
		for (var i = 0; i < mExcelSettings.workbook.columns.length; i++) {

			}
	
		},
	        /*==============================
          编辑生效版本并生成新版本草稿
          ==============================*/
        /**
         *
         * @param {*草稿表对应的草稿Table_draftEdit} _editDraftPath
         * @param {*头表ID} _headId
         * @param {*是否为草稿} _isActiveEntity
         * @returns
         */
        _editDraft2: function (_editDraftPath, _headId, _isActiveEntity) {
			let that = this;
	
			return new Promise(function (resolve, reject) {
			  that.getModel().callFunction(_editDraftPath, {
				method: "POST",
				urlParameters: {
				  ID: _headId,
				  IsActiveEntity: _isActiveEntity,
				},
				success: function (oData) {
				  resolve(oData);
				},
				error: function (oError) {
				  console.log("%c version =======" + oError.statusCode + oError.statusText);
				  reject(oError);
				},
			  });
			});
		  },


		/*==============================
		设置弹框内容
		==============================*/
		_setPopData: function (material) {
			var that = this;
			var myArray=new Array();
			var view = this.getView();
			return new Promise((resolve, reject) => {
				//判断后根据品目设置弹框内容
				var jsonModel = view.getModel("pch03Data");
				if(!jsonModel) {
				jsonModel = new sap.ui.model.json.JSONModel();
				view.setModel(jsonModel, "pch03Data");
				}
				that.getModel().read("/PCH_T03_PO_C",{
					filters:[
						new sap.ui.model.Filter({
							path:"PO_NO",
							value1:material,
							operator:sap.ui.model.FilterOperator.EQ,
						}),
					],
					success:function(oData){
						oData.results.forEach(data=>{
							myArray.push(data);
						})

						//设置
						jsonModel.setData(myArray);
						resolve();
					},
					error:function(oError){
						reject();
					}
				})
			});
	
		},
	});
});