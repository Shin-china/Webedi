sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageToast",
	"sap/m/MessageBox"
  ], function (Controller, MessageToast, MessageBox) {
	"use strict";
  
	return Controller.extend("umc.app.controller.pch.pch04_pay_d", {
  
	  onResend: function () {
		var oModel = this.getView().getModel(); // 获取 OData 模型
  
		// 调用自定义 action, 不需要传递邮箱地址，直接调用即可
		oModel.callFunction("/PCH04_SENDEMAIL", {
		  method: "POST",
		  urlParameters: {
			"parms": ""  // 如果 handler 中不需要任何参数，可以传递空字符串
		  },
		  success: function (oData, response) {
			MessageToast.show("邮件发送成功: " + oData);
		  },
		  error: function (oError) {
			MessageBox.error("发送邮件失败: " + JSON.parse(oError.responseText).error.message.value);
		  }
		});
	  }
  
	});
  });
  