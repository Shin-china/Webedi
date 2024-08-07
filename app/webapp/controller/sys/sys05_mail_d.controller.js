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

        },
        onSave:function(){
            this.MessageTools._clearMessage();
			var noErrorFlag = this.MessageTools._checkInputErrorMSG();
			if(noErrorFlag){
				noErrorFlag = that._checkHead();
			}

            //数据结构
            var context = this.getView().getBindingContext();
            var itemobj = {
                templateID: context.getProperty("TEMPLATE_ID"),
                templateName: context.getProperty("TEMPLATE_NAME"),
                templateContent: context.getProperty("TEMPLATE_CONTENT")
            }

            var resStr = { userJson: JSON.stringify(itemobj) };
            if (noErrorFlag) {
                //Crate or Update
                this.getModel().callFunction("/SYS05_MAILTEMP_add", {
                    method: "POST",
                    urlParameters: resStr,
                    success: function(oData) {
                        that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("sys", "MSG_SUCCESS", that.getView()), "", 0, that.getView());
                        that.onNavBack
                    },
                    error: function(oError) {
                        that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("sys", "MSG_ERROR", that.getView()), "", 1, that.getView());
                    }
                })
            }
        },

        _checkHead:function(){
            var that = this;
            var TemplateID = that.byId("idInput1").getValue();

			//检查输入
			if(TemplateID ==="" || TemplateID === null){
				const sPath = that.getView().getBindingContext().getPath() + "/TEMPLATE_ID";
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "MSG_TEMPLATE_ID", this.getView()), sPath, 1, that.getView());
				checkResult = false;
			}
        }
	});
});