sap.ui.define(
	["sap/base/i18n/ResourceBundle", "sap/ui/model/resource/ResourceModel", "sap/ui/core/Messaging", "sap/ui/core/message/Message"],
	function (ResourceBundle, ResourceModel, Messaging, Message) {
	  "use strict";
  
	  return {
		/**初始化消息框 */
		/**
		 *
		 * @param {page 中的this.getView} _view
		 */
		_initoMessageManager: function (_that) {
		  var that= this;
		  // set message model
		  _that.getView().setModel(Messaging.getMessageModel(), "message");
		  // activate automatic message generation for complete view
		  Messaging.registerObject(_that.getView(), true);
		  //自动弹出
		  var oButtonPopover = _that.byId("messageButton");
		   oButtonPopover.addEventDelegate({
		  "onAfterRendering": function () {
			var pop = that._getMessagePopover("",_that.getView());
			pop.openBy(oButtonPopover);
		  }
		  }, _that);
		},
	   
  
		/**
		 * 清除异常msg
		 */
		_clearMessage: function () {
		  Messaging.removeAllMessages();
		},
  
		//检查画面是否有检查错误消息。同时清除弹出框的消息
		_checkInputErrorMSG: function () {
		  var isNPost = true;
  
		  var aExistedMessages = Messaging.getMessageModel().getData();
		  aExistedMessages.forEach((oExistedMessage) => {
			if (oExistedMessage.getId().startsWith("usap_")) {
			  Messaging.removeMessages(oExistedMessage);
			} else if (oExistedMessage.getCode() === undefined) {
			  isNPost = false;
			} else {
			  Messaging.removeMessages(oExistedMessage);
			}
		  });
		  return isNPost;
		},
		//取得多语言内容
		_getI18nTextInModel: function (_model, _i18nID, _view) {
		  if (this._i18nBundle == null) {
			this._i18nBundle = _view.getModel(_model).getResourceBundle();
		  }
		  return this._i18nBundle.getText(_i18nID);
		},
		//取得多语言内容
		_getI18nText: function (_i18nID, _view) {
		  return this._getI18nTextInModel("com", _i18nID, _view);
		},
		//取得多语言消息
		_getI18nMessage: function (messageID, paramsArray, _view) {
		  var message = this._getI18nText(messageID, _view);
		  var matchesArray = message.match(/\{(\d+)\}/g);
		  for (var i = 0; i < matchesArray.length && i < paramsArray.length; i++) {
			message = message.replace(matchesArray[i], this._getI18nText(paramsArray[i], _view));
		  }
		  return message;
		},
		/*==============================
		  前台追加Message
		  sType: 1-Error, 2-Warning, 3-Success
		  ==============================*/
		_addMessage: function (sMessage, sPath, iType, _view) {
		  if (this._messageIndex === null) {
			this._messageIndex = 0;
		  }
		  this._messageIndex++;
		  // 新規MessageをMessageManagerに登録
		  let oMessage = new sap.ui.core.message.Message({
			id: "mmss_" + this._messageIndex,
			message: sMessage,
			type: iType === 3 ? sap.ui.core.MessageType.Success : iType === 2 ? sap.ui.core.MessageType.Warning : sap.ui.core.MessageType.Error,
			target: sPath,
			processor: _view.getModel(),
		  });
		  sap.ui.getCore().getMessageManager().addMessages(oMessage);
		},
		_addMessages: function (sMessage, sPath, iType, _view) {
		  if (this._messageIndex === null) {
			this._messageIndex = 0;
		  }
		  this._messageIndex++;
		  const oMessage = new Message({
			message: sMessage,
			type: iType === 3 ? sap.ui.core.MessageType.Success : iType === 2 ? sap.ui.core.MessageType.Warning : sap.ui.core.MessageType.Error,
			target: sPath,
			processor: _view.getModel(),
		  });
		  Messaging.addMessages(oMessage);
		},
  
		/*++++++++++++++++++++++++++++++
		  点击消息按钮
		  ++++++++++++++++++++++++++++++*/
		_onMessagePopoverPress: function (oEvent, _view) {
		  this._getMessagePopoveroEvent, _view().openBy(oEvent.getSource());
		},
  
		_onClickMessagePopover: function (oEvent, _view) {
		  this._getMessagePopover(oEvent, _view).openBy(oEvent.getSource());
		},
		/*++++++++++++++++++++++++++++++
			  消息栏title点击后定位至错误控件事件
			  ++++++++++++++++++++++++++++++*/
		onClickMessage: function (oEvent) {
		  var oItem = oEvent.getParameter("item"),
			oMessage = oItem.getBindingContext("message").getObject(),
			oControl = sap.ui.getCore().byId(oMessage.getControlId());
		  if (oControl && oControl.getDomRef()) {
			oControl.focus();
		  }
		},
		/*==============================
		  取得消息弹出框
		  ==============================*/
		_getMessagePopover: function (oEvent, _veiw) {
		  //create popover lazily (singleton)
		  if (!this._oMessagePopover) {
			//create popover lazily (singleton)
			this._oMessagePopover = sap.ui.xmlfragment(_veiw.getId(), "umc.app.view.MessagePopover", this);
			_veiw.addDependent(this._oMessagePopover);
		  }
		  return this._oMessagePopover;
		},
		//动态绑定资源文件
		_bindResourceModel: function (_veiw, _modelName) {
		  var oResourceModel2 = new ResourceModel({
			// specify url of the base .properties file
			bundle: ResourceBundle.create({
			  bundleName: "root/i18n/" + _modelName,
			}),
			async: true,
		  });
  
		  _veiw.setModel(oResourceModel2, _modelName);
		},
	  };
	}
  );
  