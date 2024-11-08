sap.ui.define([
	"umc/app/controller/BaseController"
], function(
	Controller
) {
	"use strict";

	return Controller.extend("umc.app.controller.sys.sys03_dict_l", {
        onInit: function () {
    
            //  设置版本号
            this._setOnInitNo("SYS03", ".20240408.01");
            // 设置选中框 无
            this._setSelectionNone("detailTable");
            this.MessageTools._clearMessage();
            this.MessageTools._initoMessageManager(this);
            //显示页面
            this.getRouter().getRoute("RouteList_sys03").attachPatternMatched(this._onObjectMatched, this);
          },
          _onObjectMatched:function(){
            this._setAuthByMenuAndUser("SYS03");
          },
          onPress: function (oEvent) {
            var oItem = oEvent.getSource();
            var oContext = oItem.getBindingContext();
            this._onPress(oEvent, "RouteView_sys03", oContext.getObject().H_ID);
          },
          onRebind: function (oEvent) {
            let sorts = ["H_CODE", "D_NO"];
            let ascs = [false, false]; //true desc false asc
            //手动添加排序
            this._onListRebinSort(oEvent, sorts, ascs);
            this._setAuthByMenuAndUser("SYS03");
          },
          onRowSelectionChange:function(oEvent){
           // rowSelectionChange="onRowSelectionChange" selectionBehavior="Row" selectionMode="Single"
            
            var oBindingContext = oEvent.getParameter("rowContext");
                  var oSelectedItem = oBindingContext.getObject();
       
                  // 你现在可以使用oSelectedItem来获取整行的数据
                  // 例如：
                  var sSelectedItemId = oSele
          }
        });
});