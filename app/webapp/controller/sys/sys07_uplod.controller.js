jQuery.sap.require("umc/app/lib/xlsx");
sap.ui.define(
  ["umc/app/controller/BaseController", "sap/ui/model/odata/v2/ODataModel", "sap/ui/export/library", "sap/ui/model/json/JSONModel","sap/ui/export/Spreadsheet"],
  function (Controller, ODataModel, exportLibrary, JSONModel) {
    "use strict";
    return Controller.extend("umc.app.controller.sys.sys07_uplod", {
      /*++++++++++++++++++++++++++++++
		  初始化
		  ++++++++++++++++++++++++++++++*/
      onInit: function (oEvent) { 
        // Message Init
        //初始化 msg
        this.MessageTools._clearMessage();
        // this.getRouter().getRoute("RouteCre_SYS07").attachPatternMatched(this._onRouteMatched, this);
        //  设置版本号
        this._setOnInitNo("SYS07", ".20241225.01");
      },

   
    });
}
);
