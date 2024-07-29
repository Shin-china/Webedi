sap.ui.define([
	"umc/app/controller/BaseController",
	"sap/ui/model/odata/v2/ODataModel",
	"sap/m/MessageBox"
], function(
	Controller,
	ODataModel,
	MessageBox
) {
	"use strict";

	return Controller.extend("umc.app.controller.sys.sys01_user_l", {
		onInit:function(){
      		// 设置自己的 OData模型为默认模型
		    this._setCreateAble(false);
			this._setDefaultDataModel("TableService");

			//Clear Message
			this.MessageTools._clearMessage();


		},
		onPress:function(oEvent){
			var oItem = oEvent.getSource();
			var oContext = oItem.getBindingContext();
			this._onPressNav(oEvent,"RouteEdit_sys01",oContext.getObject().ID);
		},

		onCreate:function(oEvent){
			var oItem = oEvent.getSource();
			this._onPressNav(oEvent,"RouteCre_sys01");
		},

		onDel:function(oEvent){
			var that = this;
			var sResponsivePaddingClasses = "sapUiResponsivePadding--header sapUiResponsivePadding--content sapUiResponsivePadding--footer";
			//弹框
			var confirmTxt = this.MessageTools._getI18nTextInModel("sys", "deleteUserMsg", this.getView());
			var confirmTitle = this.MessageTools._getI18nTextInModel("sys", "deleteUserTitle", this.getView());
			var successDel = this.MessageTools._getI18nTextInModel("sys", "deleteUserSuccess", this.getView());
			MessageBox.warning(
				confirmTxt,
				{
					icon: MessageBox.Icon.WARNING,
					title: confirmTitle,
					actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
					emphasizedAction: MessageBox.Action.OK,
					initialFocus: MessageBox.Action.CANCEL,
					styleClass: sResponsivePaddingClasses,
					onClose:function(sAction){
						if(sAction === "OK"){
							//删除用户
							var userList = that._getRootId("detailTable","USER_ID");
							for(var ind = 0;ind < userList.length;ind++){
								var itemObj = {
									"USER_ID": userList[ind]
								};

								var restStr = {userJson: JSON.stringify(itemObj)};
								that.getModel().callFunction("/SYS01_USER_deleteUser",{
									method: "POST",
									urlParameters: restStr,
										success: function(data){
											that.getModel().refresh();
											that._showMessageToast(successDel);
										},
										error: function(error){
											console.log(error);
										}
								});
	
							}
						}
					},
					dependentOn: this.getView()
				}
			);

		}
	});
});