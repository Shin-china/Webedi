sap.ui.define([
	"umc/app/controller/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
], function(
	Controller,
	JSONModel,
	MessageBox
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

			//Global ID
			this._id = "";
			
			//Setting List Drop
			var oList = { 
				"ListItems":[
					{
						"Type": "A",
						"Name": "A.Active"
					},
					{
						"Type": "I",
						"Name": "I.InActive"
					},
					{
						"Type": "L",
						"Name": "L.Lock"  
					}
				],
				"UserType": [
				    {
				        "Type": "1",
						"Name": "1.Internal User"
				    },
				    {
				        "Type": "2",
						"Name": "2.External Vendor"
				    }
				]};
			var oModel =  new JSONModel(oList) ;
			this.getView().setModel(oModel,"list");

			//Set Edit Route
			this.getRouter().getRoute("RouteEdit_sys01").attachPatternMatched(this._onRouteMatchedEdit, this);
			//Set Create Route
			this.getRouter().getRoute("RouteCre_sys01").attachPatternMatched(this._onRouteMatchedCreate, this);
			
			//清除消息
			this.MessageTools._clearMessage();


		},
		_onRouteMatchedEdit:function(oEvent){
			var headID = oEvent.getParameter("arguments").headID;

			this._setEditable(false);
			this._setCreateAble(false);

			const that = this;
			this._id = headID;

			//Bind User Information
			 this.getModel().metadataLoaded().then(() => {
				// Create
				this._bindViewData(
				this.getModel().createKey("/SYS_T01_USER", {
				    ID: headID,
					isActiveEntity:true
				})
			);
			});


			that.byId("smartTable1").rebindTable();
			that.byId("smartTable2").rebindTable();

		},
		_onRouteMatchedCreate:function(oEvent){
		    const that = this;
			this._id = "";
			this._setEditable(true);
			this._setCreateAble(true);
			that.getModel().refresh(true);

			//Unbind element
			that.getView().unbindElement();

			//bind new element
			const newHeaderContext = that.getModel().createEntry("/SYS_T01_USER", {
			    properties:{
					VALID_DATE_FROM: this.CommTools.getDate(new Date()),
				}
			})

			that.getView().setBindingContext(newHeaderContext);

		},
		//Edit Button
		onEdit:function(){
			this._setEditable(true);
			this._addSelection(this._id);
			//清除消息
			this.MessageTools._clearMessage();
		},
		//Save Button
		onSave:function(){
			const that = this;
			//清除消息
			this.MessageTools._clearMessage();
			var noErrorFlag = that.MessageTools._checkInputErrorMSG();
			if(noErrorFlag){
				noErrorFlag = that._checkHead();
			}


			//保存数据
			var context = this._getHeadBingdingContext();
			var dateT = this.CommTools._getDateYYYYMMDD(context.VALID_DATE_TO);
			var dateF = this.CommTools._getDateYYYYMMDD(context.VALID_DATE_FROM);
			var userStatus = this.byId("idSelectList").getSelectedKey();
			var userType = this.byId("idSelectList7").getSelectedKey();
			var plantIdList = this._getRootId("idTable1","PLANT_ID");
			var itemObj = {
				userId:context.USER_ID,
				userType:userType,
				userName:context.USER_NAME,
				userStatus:userStatus,
				validDateFrom:dateF,
				validDateTo:dateT,
				bpNumber:context.BP_NUMBER,
				plants:plantIdList,
			};

			var resStr = { userJson: JSON.stringify(itemObj) };
			if(noErrorFlag){
				if(this._id){
					//Create New User
					this.getModel().callFunction("/SYS01_USER_editUser", {
						method: "POST",
						urlParameters: resStr,
						success: function(oData) {
							that._id = context.ID;
							that._setEditable(false);
							that.getModel().refresh();
						},
						error: function(oError) {
							that._setEditable(true);
						}
					})

				}else{
					//Edit User
					that.getModel().callFunction("/SYS01_USER_addUser", {
						method: "POST",
						urlParameters: resStr,
						success: function(oData) {
							that._setEditable(false);
							that._id = context.ID;
							that.byId("USER_ID").rebindTable(smartTable1);
							that.getModel().refresh();
						},
						error: function(oError) {
							
						}
					})
				}
			}
		},
		//检查输入是否为空
		_checkHead:function(){
			var that = this;

			var checkResult = true;
			var UserCode = that.byId("USER_ID").getValue();
			var BpNumber = that.byId("BP_NUMBER").getValue();
			var UserName = that.byId("USER_NAME").getValue();
			var ValidFrom = that.byId("VALID_FROM").getValue();
			var ValidTo = that.byId("VALID_TO").getValue();
			
			//检查输入
			if(UserCode ==="" || UserCode === null){
				const sPath = that.getView().getBindingContext().getPath() + "/USER_ID";
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "MSG_USER_CODE", this.getView()), sPath, 1, that.getView());
				checkResult = false;
			}

			if(BpNumber === "" || BpNumber === null){
				const sPath = that.getView().getBindingContext().getPath() + "/BP_NUMBER";
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "MSG_BP_NUMBER", this.getView()), sPath, 1, that.getView());
				checkResult = false;
			}

			if(UserName === "" || UserName === null){
				const sPath = that.getView().getBindingContext().getPath() + "/USER_NAME";
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "MSG_USER_NAME", this.getView()), sPath, 1, that.getView());
				checkResult = false;
			}

			if(ValidFrom === "" || ValidFrom === null){
				const sPath = that.getView().getBindingContext().getPath() + "/VALID_FROM";
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "MSG_VALID_FROM", this.getView()), sPath, 1, that.getView());
				checkResult = false;
			}

			if(ValidTo === "" || ValidTo === null){
				const sPath = that.getView().getBindingContext().getPath() + "/VALID_TO";
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "MSG_VALID_TO", this.getView()), sPath, 1, that.getView());
				checkResult = false;
			}

			if(ValidFrom > ValidTo){
				const sPath = that.getView().getBindingContext().getPath() + "/VALID_TO";
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "MSG_DATE_ERROR", this.getView()), sPath, 1, that.getView());
				checkResult = false;
			}
			//End

			return checkResult
		},



		//过滤工厂数据
		onRebindPlant:function(oEvent){

			var oBindingParams = oEvent.getParameter("bindingParams");

			if(this._id != "" && this._id != null){
				oBindingParams.filters.push(new sap.ui.model.Filter("USER_ID", "EQ", this._id));
			}
			oEvent.getParameter("bindingParams").parameters.expand = "TO_PLANT";

		},
		//默认选上已保存的数据
		_addSelection:function(headID){

			//获取所有工厂数据
			var plantTable = this.byId("idTable1");
			//清空工厂选择项
			plantTable.clearSelection();

			var that = this;

			//获取已选择的工厂数据
			this._getUserPlantData(headID,true)

			.then((data) =>{
				that.plantData = data;
				return that._getPlantData(headID);
			}).then((oTable) =>{
			    var data = that.plantData.results;
				var tableContext = oTable.results;
				//对比工厂数据对选中的进行选中
				for(var index = 0; index < tableContext.length; index++){
					for(var i = 0; i < data.length; i++){
						if(tableContext[index].PLANT_ID === data[i].PLANT_ID){
							//plantTable.setSelectedIndex(index,index);
							plantTable.addSelectionInterval(index,index);
						}
					}
				}
			});
		},
		//获取用户选择的工作数据
		_getUserPlantData:function(headID,isE){
			var that = this;
			return new Promise(function(resolve,reject){
			    that.getModel().read("/SYS_T09_USER_2_PLANT",{
					filters:[new sap.ui.model.Filter("USER_ID", "EQ", headID)],
					success:function(data){
						resolve(data);
					},
					error:function(error){
						reject(error);
					}
				})
			})
		},
		//获取所有的工厂数据
		_getPlantData:function(headID){
			var that = this;
			return new Promise(function(resolve,reject){
			    that.getModel().read("/MST_T02_SAP_PLANT",{
			        success:function(data){
			            resolve(data);
			        },
					error:function(error){
					    reject(error);
					}
			    })
			})
		}
	});
});