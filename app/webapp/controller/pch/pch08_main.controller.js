sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch08_main", {
        onInit: function () {
            // 加载默认的详细视图
            const defaultView = sap.ui.xmlview("umc.app.view.pch.pch05_account_d");
            this.getView().byId("idIconTabBar").addContent(defaultView);
        },

        onTabSelect: function (oEvent) {
            // 获取选中的标签页
            const selectedKey = oEvent.getParameter("key");
            const oView = this.getView();

            // 根据选中的标签页加载相应的视图
            oView.byId("idIconTabBar").removeAllContent(); // 清除当前内容

            if (selectedKey === "listView") {
                const listView = sap.ui.xmlview("umc.app.view.pch.pch05_account_l");
                oView.byId("idIconTabBar").addContent(listView);
            } else if (selectedKey === "detailView") {
                const detailView = sap.ui.xmlview("umc.app.view.pch.pch08_answ_p");
                oView.byId("idIconTabBar").addContent(detailView);
            }
        }
    });
});
