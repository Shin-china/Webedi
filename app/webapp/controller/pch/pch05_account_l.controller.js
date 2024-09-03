sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch05_account_l", {
        onInit: function () {
            // 在这里初始化列表视图的逻辑
            // 例如：加载数据模型、设置默认值等
        },

        onListItemPress: function (oEvent) {
            // 当用户点击列表中的某项时的处理逻辑
            const oItem = oEvent.getSource();
            const oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            oRouter.navTo("RouteToDetail", {
                // 假设你在这里需要传递一个参数
                itemId: oItem.getBindingContext().getProperty("ID")
            });
        }
    });
});
