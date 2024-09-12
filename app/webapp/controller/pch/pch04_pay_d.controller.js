sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Controller, MessageToast, MessageBox) {
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

            var oModel = this.getView().getModel(); // 默认模型指向 TableService
            var aEmailParams = [];

            // 获取选中行的 ZABC 值
            var zabcValue = this.getZABCFromSelection(oTable, aSelectedIndices);
            console.log("ZABC Value from selection: ", zabcValue); // 调试日志
            
            // 验证 ZABC 值
            if (!this.isZABCValid(zabcValue)) {
                MessageBox.error("WEB EDI対象ではないので、支払通知送信できません。ZABC値: " + zabcValue);
                return;
            }

            // 使用 OData Model 从 SYS_T11_MAIL_TEMPLATE 实体中获取邮件模板
            var sPath = "/SYS_T11_MAIL_TEMPLATE(TEMPLATE_ID='UWEB_M007')"; // 实体路径

            oModel.read(sPath, {
                success: function (oData) {
                    if (!oData) {
                        MessageBox.error("邮件模板未找到。");
                        return;
                    }

                    var sTemplateTitle = oData.MAIL_TITLE;
                    var sTemplateContent = oData.MAIL_CONTENT;

                    // 遍历选中的行，提取所需数据
                    var aSelectedData = aSelectedIndices.map(function (iIndex) {
                        return oTable.getContextByIndex(iIndex).getObject();
                    });

                    aSelectedData.forEach(function (oData) {
                        var invNo = oData.INV_NO; // 获取 INV_NO
                        var supplierDescription = oData.SUPPLIER_DESCRIPTION; // 获取 SUPPLIER_DESCRIPTION
                        var invPostDate = oData.INV_POST_DATE; // 获取 INV_POST_DATE

                        // 调试日志，检查 invPostDate 是否为 undefined
                        if (typeof invPostDate === "undefined" || !invPostDate) {
                            console.error("INV_POST_DATE is undefined or empty for data: ", oData);
                            return; // 如果 INV_POST_DATE 为 undefined 或空，则跳过当前数据
                        }

                        // 将 INV_POST_DATE 解析为 Date 对象
                        var date = new Date(invPostDate);
                        var year = date.getFullYear(); // 提取年份
                        var month = ("0" + (date.getMonth() + 1)).slice(-2); // 提取月份并确保是两位数

                        // 替换模板中的占位符
                        var sEmailContent = sTemplateContent.replace("{仕入先名称}", supplierDescription)
                            .replace(/{YEAR}/g, year)
                            .replace(/{MONTH}/g, month);

                        // 将提取的数据添加到参数数组中
                        aEmailParams.push({
                            TEMPLATE_ID: "UWEB_M007", // 邮件模板 ID
                            MAIL_TO: "xiaoyue.wang@sh.shin-china.com", // 收件人邮箱
                            MAIL_BODY: [{
                                object: "content",
                                value: sEmailContent // 邮件内容
                            }]
                        });
                    });

                    // 将参数转换为字符串或其他格式，以便传递给后端
                    var sParams = JSON.stringify(aEmailParams);

                    // 调用 CDS 中定义的 PCH04_SENDEMAIL 动作
                    oModel.callFunction("/PCH04_SENDEMAIL", {
                        method: "POST",
                        urlParameters: {
                            "parms": sParams
                        },
                        success: function (oData) {
                            MessageToast.show("メール送信に成功しました: " + oData);
                        },
                        error: function (oError) {
                            var errorMessage = oError.message || "発生したエラー: 未知のエラー";
                            if (oError.responseText) {
                                try {
                                    var responseJSON = JSON.parse(oError.responseText);
                                    errorMessage = responseJSON.error.message.value || errorMessage;
                                } catch (e) {
                                    console.error("Error parsing response text: ", oError.responseText);
                                }
                            }
                            MessageBox.error("メール送信に失敗しました: " + errorMessage + ". ZABC値: " + zabcValue);
                        }
                    });
                },
                error: function (oError) {
                    MessageBox.error("メールテンプレートの取得に失敗しました: " + oError.message);
                }
            });
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
        }

    });
});
