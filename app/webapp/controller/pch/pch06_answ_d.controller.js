sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	
	/**
	 * 共通用对象 全局
	 */
	var _objectCommData = {
		_entity: "/PCH_T06_PO_ITEM", //此本页面操作的对象//绑定的数据源视图
		
	};
	var  myMap = new Map(); 
	return Controller.extend("umc.app.controller.pch.pch06_answ_d", {
		formatter : formatter,

		onInit: function () {
			// 设置自己的 OData模型为默认模型
			this._setDefaultDataModel("TableService");
			//  设置版本号
			this._setOnInitNo("PCH01", ".20240812.01");
			this.MessageTools._clearMessage();
			this.MessageTools._initoMessageManager(this);
			//	进入画面则刷新
			myMap = new Map(); 
			this.getRouter().getRoute("RouteCre_pch06").attachPatternMatched(this._onRouteMatched, this);
		},
		/*==============================
		检索
		==============================*/
		onJS: function (oEvent) {
			var that = this;
			that._setBusy(true);
			var aFilters = this.getView().byId("smartFilterBar").getFilters();
			var view = this.getView();
			var jsonModel = view.getModel("workInfo");
			view.setModel(jsonModel, undefined);
			this._readEntryByServiceAndEntity(_objectCommData._entity,aFilters, null).then((oData) => {
				
				
				if (!jsonModel) {
					jsonModel = new sap.ui.model.json.JSONModel();
					view.setModel(jsonModel, "workInfo");
				  }
				jsonModel.setData(oData.results);
				//更新seq数据
				that._getSeq();
				that._setBusy(false);
				console.log(oData.results)
			  });
			
		},	
		/*==============================
		删除
		==============================*/
		onDelete: function (oEvent) {
			var that = this;
			var view = this.getView();
			var selectedIndices = this._TableList("tableUploadData"); // 获取选中行
			var jsonModel = view.getModel("workInfo");

			var datas = jsonModel.getData();
			if(selectedIndices){
				selectedIndices.forEach((selectedIndex) => {
					//如果删除只能删除新追加的
					if(!selectedIndex.CD_BY){
						var id = selectedIndex.ID
						//根据id删除list
						datas = datas.filter(odata=>
							odata.ID !== id
						)
					}else{
						that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("pch", "PCH_06_ERROR_MSG2", this.getView()), null, 1, this.getView());
					}
					

				  });
				  jsonModel.setData(datas);
			}
		},	
		/*==============================
		复制
		==============================*/
		onCop: function (oEvent) {
			var that = this;
			var view = this.getView();
			var selectedIndices = this._TableList("tableUploadData"); // 获取选中行
			var jsonModel = view.getModel("workInfo");

			var datas = jsonModel.getData();
			if(selectedIndices){
				selectedIndices.forEach((selectedIndex) => {
					let copiedIndex = JSON.parse(JSON.stringify(selectedIndex)); 
					copiedIndex.DELIVERY_DATE= selectedIndex.DELIVERY_DATE;
					copiedIndex.PO_DATE= selectedIndex.PO_DATE;
					copiedIndex.PO_D_DATE= selectedIndex.PO_D_DATE;
					copiedIndex.RQ= null;
					
					copiedIndex.CD_BY = null;
					copiedIndex.CD_TIME = null;
					copiedIndex.SEQ=myMap.get(selectedIndex.PO_NO+selectedIndex.D_NO) + 1;
					copiedIndex.ID=selectedIndex.PO_NO+selectedIndex.D_NO+copiedIndex.SEQ
					datas.push(copiedIndex);
				  });
				  jsonModel.setData(datas);
				//更新seq数据
				that._getSeq();
			}
		},
		/*==============================
		编辑
		==============================*/
		onEdi: function (oEvent) {
			var that = this;
			that._setEditable(true);
		},
		/*==============================
		保存
		==============================*/
		onSav: function (oEvent) {
			var that = this;
			that._setBusy(true);
	
			this._callCdsAction("/PCH06_SAVE_DATA", this._getData(), this).then((oData) => {

			
			  var myArray = JSON.parse(oData.PCH06_SAVE_DATA);
			  this._setEditable(false);
			  if(myArray.err){
				this._setEditable(true);
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("pch", myArray.reTxt, this.getView()), null, 1, this.getView());
			  }
			  that._setBusy(false);

			});
			
		},
		
		/**
		 * 
		 * @returns 
		 */
		_getData: function () {
			var jsondata = this.getModel("workInfo").getData();
			var a = JSON.stringify({ list: jsondata });
			var oPrams = {
			  str: a,
			};
			return oPrams;
		  },

		/**
		 * 
		 * @returns 
		 */
		_deleteDatas: function (datas,id) {
			let list = new Array();

			datas.forEach(odata=>{
				if(odata.ID != id){
					list
				}
			})

		},

		/*==============================
		init
		==============================*/
		_onRouteMatched: function (oEvent) {
			this._poNO = "";
			this._setEditableAuth(true);
			this._setEditable(false);

			;

			
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
				that.getModel().read("/T03_PO_C",{
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
		
		/**
		 * 获得并设置sql
		 * @param {*} data 
		 */
		_getSeq(data){
			var view = this.getView();
			var jsonModel = view.getModel("workInfo");
			if (!jsonModel) {
				jsonModel = new sap.ui.model.json.JSONModel();
				view.setModel(jsonModel, "workInfo");
			  }
			var datas = jsonModel.getData();

			datas.forEach(data=>{
				let PO_NO =data.PO_NO;
				let D_NO =data.D_NO;
				let SQL = data.SEQ;
				let podno =PO_NO+D_NO;
				let hasName = myMap.has(podno);
				if(hasName){
					let locSql = myMap.get(podno);
					
					//如果存下locSql没有行里面Sql的大，则于以替换
					if(locSql < SQL){
						myMap.set(podno,SQL)
					}
				}else{
					myMap.set(podno,SQL)
				}
			})


		}
	});
});