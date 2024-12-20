sap.ui.define([
	"umc/app/controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter, Spreadsheet) {
	"use strict";
	/**
	 * 共通用对象 全局
	 */
	var _objectCommData = {
		_entity: "/PCH_T06_PO_ITEM", //此本页面操作的对象//绑定的数据源视图
		_aFilters: undefined

	};
	var myMap = new Map();
	return Controller.extend("umc.app.controller.pch.pch06_answ_d", {
		formatter: formatter,

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
			_objectCommData._aFilters = aFilters;
			var view = this.getView();
			var jsonModel = view.getModel("workInfo");
			view.setModel(jsonModel, undefined);
			if (aFilters.length == 0) {
				that._setBusy(false);
				return;
			}
			this._readEntryByServiceAndEntity(_objectCommData._entity, aFilters, null).then((oData) => {


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
		/**
		 * 过账方法
		 * @param {*} oEvent 
		 */
		onQue: function (oEvent) {
			var that = this;
			that._setBusy(true);
			var selectedIndices = this._TableList("tableUploadData"); // 获取选中行
			
			if (selectedIndices) {

				this._invoPo(selectedIndices);


			}else{
				that._setBusy(false);
			}
		},
		/**
		 * 同期方法
		 * @param {*} oEvent 
		 */
		onTongQi: function (oEvent) {
			var that = this; 
			that._setBusy(true);
			this._callCdsAction("/PCH06_TQ", null, this).then(
				function (oData) {
				  var str = oData.PCH06_TQ;
				  that._setBusy(false);
				  sap.m.MessageToast.show(str);
				},
				function (error) {
				  that._setBusy(false);
				  sap.m.MessageToast.show(error);
				}
			  )
			// var datas = jsonModel.getData();
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
			if (selectedIndices) {
				selectedIndices.forEach((selectedIndex) => {
					//如果删除只能删除新追加的
					if (!selectedIndex.CD_BY) {
						var id = selectedIndex.ID
						//根据id删除list
						datas = datas.filter(odata =>
							odata.ID !== id
						)
					} else {
						that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_06_ERROR_MSG2", this.getView()), null, 1, this.getView());
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
			if (selectedIndices) {
				selectedIndices.forEach((selectedIndex) => {
					let copiedIndex = JSON.parse(JSON.stringify(selectedIndex));
					copiedIndex.DELIVERY_DATE = selectedIndex.DELIVERY_DATE;
					copiedIndex.PO_DATE = selectedIndex.PO_DATE;
					copiedIndex.PO_D_DATE = selectedIndex.PO_D_DATE;
					copiedIndex.RQ = null;

					copiedIndex.CD_BY = null;
					copiedIndex.CD_TIME = null;
					copiedIndex.SEQ = myMap.get(selectedIndex.PO_NO + selectedIndex.D_NO) + 1;
					copiedIndex.ID = selectedIndex.PO_NO + selectedIndex.D_NO + copiedIndex.SEQ
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
			var view = this.getView();
			//清除msg
			this.MessageTools._clearMessage();

			if (that._isCheckData()) {
				var jsonModel = view.getModel("workInfo");
				this._callCdsAction("/PCH06_SAVE_DATA", this._getData(), this).then((oData) => {


					var myArray = JSON.parse(oData.PCH06_SAVE_DATA);
					this._setEditable(false);
					if (myArray.err) {
						this._setEditable(true);
						var rt = myArray.reTxt.split("||");
						for (var i = 1; i < rt.length; i++) {

							that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", rt[i], this.getView()), null, 1, this.getView());
						}

					} else {

						that._readEntryByServiceAndEntity(_objectCommData._entity, _objectCommData._aFilters, null).then((oData) => {


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
					}
					that._setBusy(false);

				});
			} else {
				that._setBusy(false);
			}
		},

		/**
		 * 判断保存前check,数据必输check
		 * @returns 
		 */
		_isCheckData: function () {
			var jsondata = this.getModel("workInfo").getData();
			var boo = true;
			if(jsondata.length){
				jsondata.forEach((odata) => {
					if (this.formatter._isNull(odata.QUANTITY)) {
						this.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_06_ERROR_MSG4", this.getView()), null, 1, this.getView());
						boo = false;
					}
					if (this.formatter._isNull(odata.DELIVERY_DATE)) {
						this.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_06_ERROR_MSG5", this.getView()), null, 1, this.getView());
						boo = false;
					}
	
				})
			}else{boo =false;}
		
			return boo;
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


		/*==============================
		init
		==============================*/
		_onRouteMatched: function (oEvent) {
			this._poNO = "";
			this._setEditableAuth(true);
			this._setEditable(false);

			;


		},

		onBeforeExport: function (oEvt) {
			var mExcelSettings = oEvt.getParameter("exportSettings");
			for (var i = 0; i < mExcelSettings.workbook.columns.length; i++) {

			}

		},
	


		
		/**
		 * 获得并设置sql
		 * @param {*} data 
		 */
		_getSeq(data) {
			var view = this.getView();
			var jsonModel = view.getModel("workInfo");
			if (!jsonModel) {
				jsonModel = new sap.ui.model.json.JSONModel();
				view.setModel(jsonModel, "workInfo");
			}
			var datas = jsonModel.getData();

			datas.forEach(data => {
				let PO_NO = data.PO_NO;
				let D_NO = data.D_NO;
				let SQL = data.SEQ;
				let podno = PO_NO + D_NO;
				let hasName = myMap.has(podno);
				if (hasName) {
					let locSql = myMap.get(podno);

					//如果存下locSql没有行里面Sql的大，则于以替换
					if (locSql < SQL) {
						myMap.set(podno, SQL)
					}
				} else {
					myMap.set(podno, SQL)
				}
			})


		}
	});
});