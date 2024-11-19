sap.ui.define([
    "umc/app/Controller/BaseController",
], function (Controller) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch08_main", {
        onInit: function () {
            // 加载默认的详细视图
            const defaultView = sap.ui.xmlview("umc.app.view.pch.pch08_list");
            this.getView().byId("idIconTabBar").addContent(defaultView);
        }
    });
});
