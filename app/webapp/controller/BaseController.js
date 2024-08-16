sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/core/routing/History",
    "sap/ui/core/UIComponent",
    "umc/app/model/formatter",
    "sap/ui/core/Fragment",
    "sap/ui/model/odata/v2/ODataModel",
    "umc/app/controller/com/MessageTools",
    "umc/app/controller/com/CommTools",
	"sap/ui/model/odata/type/Boolean",
    "sap/m/MessageToast"
], function(
	Controller,
	History,
	UIComponent,
	formatter,
	Fragment,
	ODataModel,
	MessageTools,
	CommTools,
	Boolean,
	MessageToast
) {
	"use strict";

	return Controller.extend("umc.app.controller.BaseController", {
        formatter:formatter,
        MessageTools: MessageTools,

        CommTools:CommTools,

        navTo: function (psTarget, pmParameters, pbReplace) {
            this.getRouter().navTo(psTarget, pmParameters, pbReplace);
        },
        getRouter: function () {
            return UIComponent.getRouterFor(this);
        },
        _setDefaultDataModel:function(_mName){
            this.setModel(this.getModel(_mName), "localModel");
        },  
        
        //Set Global parameter
        setGlobalProperty:function(_sName,_sValue){
            this.getModel("localModel").setProperty("/"+_sName,_sValue);
        },
        //get Global parameter
        getGlobalProperty:function(_sName){
            return this.getModel("localModel").getProperty("/"+_sName);
        },
        setModel:function(oModel,sName){
            return this.getView().setModel(oModel,sName);
        },
        getModel:function(sName){
            var jsonModel = this.getView().getModel(sName);
            if(!jsonModel){
                jsonModel = new sap.ui.model.json.JSONModel();
                this.getView().setModel(jsonModel,sName);
            }
            return jsonModel;
        },
        //Output program version
        _setOnInitNo: function (oEvent, _initno) {
            console.log("%c version =======" + oEvent + _initno);
            console.log("color: #ffffff; font-style: italic; background-color: #20B2AA;padding: 2px 4px");
          },
        _setEditable:function(isEditable){
               var isTrue = true;
               var isFalse = false;
               this.setGlobalProperty("editable",isEditable )
               //可编辑的情况下，显示所有的可选项，不可编辑的情况下，显示已选中的可选项
               if(isEditable){
                this.setGlobalProperty("showall", isTrue);
                this.setGlobalProperty("showresult", isFalse);
               }else{
                this.setGlobalProperty("showall", isFalse);
                this.setGlobalProperty("showresult", true);
               }
        },

        _setEditableAuth:function(isEditable){
            this.setGlobalProperty("jurisdiction",isEditable )
        },
        _setCreateAble:function(isCreateAble){
            this.setGlobalProperty("createable",isCreateAble )
        },
      /**
       *  一栏画面跳转到明细页面
       * @param {*} oEvent
       * @param {*} _navTo  要跳转路径
       * @param {*} _infoId  参数
       */
        _onPressNav:function(oEvent,_navTo,_infoId){
            this.getRouter().navTo(_navTo, { headID: _infoId });
        },
        //Get Model
        getModel: function (sName) {
            var jsonModel = this.getView().getModel(sName);
            if (!jsonModel) {
                jsonModel = new sap.ui.model.json.JSONModel();
                this.getView().setModel(jsonModel, sName);
            }
            return jsonModel;
        },

        _getHeadBingdingContext() {
            var oHeaderContext = this.getView().getBindingContext();
            return oHeaderContext.getObject();
          },
        //绑定视图数据 
        _bindViewData(sPath,items){

            var that = this;
            this.getView().bindElement({
                path: sPath,
                parameters: {
                    expand: items
                },
                events: {
                }
            })
        },
        _getByIdObject(id){
            //获取角色明细数据
            var roleTable = this.byId(id);
            if(!roleTable){
                return undefined;
            }
            var roleTableIndices = roleTable.getSelectedIndices();
            var dataList = new Array();
        //设值
            for (var j = 0; j < roleTableIndices.length; j++) {
                var deleteContext = roleTable.getContextByIndex(roleTableIndices[j]);
                var receiveDetail = deleteContext.getObject();
                dataList.push(receiveDetail);
            }
            return dataList;
        },
		_getRootId(viewId,fieldId){
			//获取权限数据
			var dataList = this._getByIdObject(viewId);
			var dataID = new Array();
			dataList.forEach(function(item){
			   var data = item;
			   dataID.push(eval("data."+ fieldId));
			})

			return dataID;
		},
    
        _showMessageToast:function(_msg){
            MessageToast.show(_msg);
        },
        // Message 弹出框
        onClickMessagePopover:function(oEvent){
            this.MessageTools._onClickMessagePopover(oEvent,this.getView());
        },
        _setBusy: function (isBusy) {
            this.getView().setBusy(isBusy);
        }
	});
});