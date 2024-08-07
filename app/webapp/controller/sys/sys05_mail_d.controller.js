sap.ui.define([
	"umc/app/controller/BaseController"
], function(
	Controller
) {
	"use strict";

	return Controller.extend("umc.app.controller.sys.sys05_mail_d", {
        /**
         * @override
         */
        onInit: function() {
            const oMessageManager = sap.ui.getCore().getMessageManager();
            this.getView().setModel(oMessageManager.getMessageModel(),"message");
            oMessageManager.registerObject(this.getView(), true);

			//Program Version
			this._setOnInitNo("SYS05","20240805-01");

        }
	});
});