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

            this.getRouter().getRoute("RouteCre_sys05").attachPatternMatched(this._onRouteMatchedCreate, this);
            this.getRouter().getRoute("RouteEdit_sys05").attachPatternMatched(this._onRouteMatchedEdit, this);

			//清除消息
			this.MessageTools._clearMessage();

        },
		_onRouteMatchedEdit:function(oEvent){
			var headID = oEvent.getParameter("arguments").headID;

			this._setEditable(false);
			this._setCreateAble(false);

			const that = this;
			this._id = headID;

			//Bind User Information
			 this.getModel().metadataLoaded().then(() => {
				// Create
				this._bindViewData(
				this.getModel().createKey("/SYS_T11_MAIL_TEMPLATE", {
				    TEMPLATE_ID: headID,
					isActiveEntity:true
				})
			);
			});


		},
		_onRouteMatchedCreate:function(oEvent){
		    const that = this;
			this._id = "";
			this._setEditable(true);
			this._setCreateAble(true);
			that.getModel().refresh(true);

			//Unbind element
			that.getView().unbindElement();

		},
        onSave:function(){
            const that = this;
            this.MessageTools._clearMessage();
			var noErrorFlag = this.MessageTools._checkInputErrorMSG();
			if(noErrorFlag){
				noErrorFlag = that._checkHead();
			}

            //数据结构
            var context = this.getView().getBindingContext();
            var itemobj = {
                templateID: that.byId("TEMPLATE_ID").getValue(),
                mailName: that.byId("TEMPLATE_NAME").getValue(),
                mailTitle: that.byId("TEMPLATE_TITLE").getValue(),
                mailContent: that.byId("TEMPLATE_CONTENT").getValue()
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
            var TemplateID = that.byId("TEMPLATE_ID").getValue();
            var checkResult = true;

			//检查输入
			if(TemplateID ==="" || TemplateID === null){
				const sPath = that.getView().getBindingContext().getPath() + "/TEMPLATE_ID";
				that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "MSG_TEMPLATE_ID", this.getView()), sPath, 1, that.getView());
				checkResult = false;
			}

            return checkResult;
        }
	});
});