sap.ui.define([
	"umc/app/controller/BaseController",
	"sap/ui/model/json/JSONModel"
], function(
	Controller,
	JSONModel
) {
	"use strict";

	return Controller.extend("umc.app.controller.sys.sys01_user_d", {
		onInit: function(){
			const that = this;
			const oMessageManager = sap.ui.getCore().getMessageManager();
			this.getView().setModel(oMessageManager.getMessageModel(),"message");
			oMessageManager.registerObject(this.getView(), true);

			//Program Version
			this._setOnInitNo("SYS01","20240710-01");
			


			this.getRouter().getRoute("RouteEdit_sys01").attachPatternMatched(this._onRouteMatchedEdit, this);
		},
		_onRouteMatchedEdit:function(oEvent){
			var headID = oEvent.getParameter("arguments").headID;

			this._setEditable(true);
			this._setEditableAuth(true)
			const that = this;
			this._id = headID;

			//设置下拉框
			var oList = { 
				"ListItems":[
					{
						"Type": "1",
						"Name": "Active"
					},
					{
						"Type": "2",
						"Name": "2.未激活"
					},
					{
						"Type": "3",
						"Name": "3.锁定"  
					}
				]};
			var oModel =  new JSONModel(oList) ;
			this.getView().setModel(oModel,"list");

				/*** 
			this.getModel().metadataLoaded().then(() => {
				// Create
				that._bindViewData(
				that.getModel().createKey("/SYS_T01_USER", {
				    ID: headID,
					isActiveEntity:true
				})
			);
			});*/


		},
		onEdit:function(){
			this._setEditable(true);
		}


	});
});