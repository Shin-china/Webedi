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
         
	});
});