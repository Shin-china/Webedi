sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel",
    "sap/base/i18n/ResourceBundle",
    "sap/ui/model/resource/ResourceModel",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator"
],
    /**
     * @param {typeof sap.ui.core.mvc.Controller} Controller
     */
    function (Controller,
	JSONModel,
	ResourceModel,
	Filter,
	ResourceBundle,FilterOperator) {
        "use strict";

        return Controller.extend("umc.app.controller.HomePage", {
            onInit: function () {
                //var oModel = new JSONModel(sap.ui.require.toUrl("root/json/homepage.json"));
                //this.getView().setModel(oModel);
                // 设置错误处理器
          
                window.onerror = function (errorMsg, url, lineNumber) {
                  alert("xxx");
                  console.log("Error: " + errorMsg + " Script: " + url + " Line: " + lineNumber);
                  // report error maybe
                };
          
                jQuery(window).on("error", function (event) {
                  alert("xxx");
          
                  var oError = event.originalEvent;
                  // 检查错误是否来自UI5框架的请求
                  if (oError && oError.filename && oError.filename.indexOf("sap-ui-core.js") !== -1) {
                    // 处理请求异常
                    console.error("UI5请求异常捕获：", oError);
                    // 可以在这里执行更多的异常处理逻辑
                  }
                });
              },
              onRebind: function (oEvent) {
                //手动添加排序
                this._onListRebinSort(oEvent, "MENU_MODULE", "MST");
              },
              homeOnPress: function (oEvent) {
                //功能跳转
                var oItem = oEvent.getSource();
                var oContext = oItem.getBindingContext();
                var obj = oContext.getObject();
                this.getRouter().navTo(obj.MENU_ROUTE_ID);
              },
              onSearch: function (oEvent) {
                var name = this._getOwnerModel("localModel").getProperty("/MENU_NAME");
                if (name == null) {
                  name = "";
                }
                //alert(name);
                var aFilters = [new sap.ui.model.Filter("MENU_NAME", sap.ui.model.FilterOperator.Contains, name)];
                /** 
                this.getView().byId("TileContainerExpandedMST").getBinding("content").filter(aFilters);
                this.getView().byId("TileContainerExpandedSYS").getBinding("content").filter(aFilters);
                this.getView().byId("TileContainerExpandedPCH").getBinding("content").filter(aFilters);
                this.getView().byId("TileContainerExpandedIQC").getBinding("content").filter(aFilters);
                */
              },
        });
    });
