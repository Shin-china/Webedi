sap.ui.define([
	"umc/app/controller/BaseController"
], function(
	Controller
) {
	"use strict";

	return Controller.extend("umc.app.controller.sys.sys02_role_l", {
        onInit: function () {
            // 设置版本号
            this._setOnInitNo("SYS02",".20240418");
            // 设置自己的 OData模型为默认模型
            this._setDefaultDataModel("TableService");
            // 设置选中框 无
            this._setSelectionNone("detailTable");
        },

        onRebind: function (oEvent) {
            let sorts  =["ROLE_CODE"];
            let ascs = [false];  //true desc false asc
            //手动添加排序
            this._onListRebinSort(oEvent,sorts,ascs);
        },

        onPress: function (oEvent) {
            var oItem = oEvent.getSource();
            var oContext = oItem.getBindingContext();
            this._onPress(oEvent, "RouteEdit_sys02", oContext.getObject().ID);
        },

        onCreateRole: function (oEvent) {
            var oItem = oEvent.getSource();
            //var oContext = oItem.getBindingContext();
            this._onPress(oEvent, "RouteCre_sys02");
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
							//删除权限
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
											MessageBox.alert(successDel);
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