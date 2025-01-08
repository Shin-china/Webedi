sap.ui.define(["umc/app/controller/BaseController", "sap/ui/model/json/JSONModel", "sap/m/MessageToast"],
  
function (Controller, JSONModel, MessageToast) {
  "use strict";
  return Controller.extend("umc.app.controller.sys.sys06_iflg_l", {
    onInit: function () {
      //  设置版本号
      this._setOnInitNo("SYS06");
      this.MessageTools._clearMessage();
      this.MessageTools._initoMessageManager(this);
    },


    onRebind: function (oEvent) {
      this.MessageTools._clearMessage();
      this._rowNoMap == null;
      let sorts = ["START_TIME"];
      let ascs = [true]; //true desc false asc
      //手动添加排序
      this._onListRebinSort(oEvent, sorts, ascs);
      // //获取并设置权限数据 指定菜单ID。
      // this._setAuthByMenuAndUser("SYS06");
    },

  });
});
