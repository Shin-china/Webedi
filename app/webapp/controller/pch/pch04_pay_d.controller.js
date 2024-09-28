sap.ui.define([
    "umc/app/Controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Controller,A, MessageToast, MessageBox) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch04_pay_d", {

        onResend: function () {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();
   
            // 检查是否有选中的数据
            if (aSelectedIndices.length === 0) {
                MessageToast.show("選択されたデータがありません、データを選択してください。");
                return;
            }

            var oModel = this.getView().getModel();
            var oCommonModel = this.getView().getModel("Common"); // 获取公共模型
            var aEmailParams = [];

            // 获取选中行的 ZABC 值
            var zabcValue = this.getZABCFromSelection(oTable, aSelectedIndices);
            console.log("ZABC Value from selection: ", zabcValue); // 调试日志
            
            // 验证 ZABC 值
            if (!this.isZABCValid(zabcValue)) {
                MessageBox.error("WEB EDI対象ではないので、支払通知送信できません。ZABC値: " + zabcValue);
                return;
            }

                // 遍历选中的行，提取所需数据
                var aSelectedData = aSelectedIndices.map(function (iIndex) {
                    return oTable.getContextByIndex(iIndex).getObject();
                });    

                 // 检查 SUPPLIER 是否相同
                var supplierSet = new Set(aSelectedData.map(data => data.SUPPLIER_DESCRIPTION));
                if (supplierSet.size > 1) {
                    MessageBox.error("複数の仕入先がまとめて配信することができませんので、1社の仕入先を選択してください。");
                    return;
                }

                // 假设您在这里定义邮件内容模板
             
                var supplierName =""
                var year =""
                var month ="" 
                aSelectedData.map(function (data) {
                            supplierName = data.SUPPLIER_DESCRIPTION || "未指定"; // 默认值
                            year = data.INV_MONTH ? data.INV_MONTH.substring(0, 4) : "年"; // 默认值
                            month = data.INV_MONTH ? data.INV_MONTH.substring(4, 6) : "月"; // 默认值
            

                        })

                var mailobj = {
                    emailJson: {
                        TEMPLATE_ID: "UWEB_M007",
                        MAIL_TO: "xiaoyue.wang@sh.shin-china.com",
                        MAIL_BODY: [{
                            object: "仕入先名称",
                            value: supplierName // 使用替换后的邮件内容
                        },
                        {
                            object: "YEAR",
                            value: year
                        },
                        {
                            object: "MONTH",
                            value: month
                        }]
                    }
                };

      
            let newModel = this.getView().getModel("Common");
            let oBind = newModel.bindList("/sendEmail");
            // oBind.create(mailobj);
            let a =oBind.create(mailobj, {
                success: function (oData) {
                    console.log(oData)
                    // 确保oData不为null并且有返回的结果
                    if (oData && oData.result && oData.result === "sucess") {
                        MessageToast.show("メールが正常に送信されました。");
                    } else {
                        MessageBox.error("メール送信に失敗しました。エラー: " + (oData.result || "不明なエラー"));
                    }
                },
                error: function (oError) {
                    console.log(oError)
                    MessageBox.error("メール送信に失敗しました。エラー: " + oError.message);
                }
            });
            console.log(a)
            
        },

        // 从选中的行中获取 ZABC 的值
        getZABCFromSelection: function (oTable, aSelectedIndices) {
            if (aSelectedIndices.length > 0) {
                var oData = oTable.getContextByIndex(aSelectedIndices[0]).getObject();
                console.log("Retrieved ZABC value from selection: ", oData.ZABC); // 调试日志
                return oData.ZABC; // 根据实际字段名替换 ZABC
            }
            return null;
        },

        // 检查 ZABC 值是否合法的方法
        isZABCValid: function (zabcValue) {
            console.log("Validating ZABC value: ", zabcValue); // 调试日志
            return zabcValue === "W";
        },

        onExportToPDF: function () {
			var that = this;
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			var IdList = that._TableDataList("detailTable", 'SUPPLIER')
			if (IdList) {
				that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T04_PAYMENT_FINAL", "SUPPLIER").then((oData) => {
					let sResponse = json2xml(oData, options);
					console.log(sResponse)
					that.setSysConFig().then(res => {
						// that.PrintTool._detailSelectPrint(that, sResponse, "test/test", oData, null, null, null, null)
						that.PrintTool._detailSelectPrintDow(that, sResponse, "test2/test2", oData, null, null, null, null)
					})
				})
			}


		}

    });
});
