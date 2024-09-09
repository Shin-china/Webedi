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
            if (aSelectedIndices.length === 0) {
                MessageToast.show("未选择任何数据"); // 提示未选择数据
                return;
            }

            var oModel = this.getView().getModel(); // 获取 OData 模型
            var aEmailParams = [];

            // 检查 ZABC 的值
            var zabcValue = this.checkZABC(); // 获取 ZABC 的值
            if (!this.isZABCValid(zabcValue)) {
                MessageBox.error("WEB EDI対象ではないので、支払通知送信できません。");
                return; // 如果不满足条件，终止后续逻辑
            }

            // 邮件模板
            var sTemplate = "{仕入先名称} 御中\n\n" +
                "いつも大変お世話になっております｡\n\n" +
                "{YEAR}年{MONTH}月度の支払通知書がアップロードされましたので、\n" +
                "以下のURLから、所定のID・Passwordでログインの上、\n" +
                "ご確認の程、宜しくお願いいたします。\n\n" +
                "御社の金額と差異がある場合、{YEAR}年{MONTH}月△日までにご連絡ください。\n\n" +
                "尚､ダウンロード期限がございますので、\n" +
                "お早めにご確認及びダウンロード頂き、ファイルの保管をお願いいたします。\n\n" +
                "UWEB（WEB-EDI） URL：*****\n\n" +
                "※本メールは配信専用となっておりますので、\n" +
                "各種お問合せやご連絡につきましては、それぞれ以下へご連絡ください。\n\n" +
                "■配信先の変更（追加・削除）依頼\n" +
                "umc-japan-change-notification@umc.co.jp\n" +
                "[調達G 変更連絡窓口]\n\n" +
                "■検収単価差異、支払日に関するお問合せ\n" +
                "umc-japan-purch-change-inf@umc.co.jp\n" +
                "[購買管理G]\n\n" +
                "■検収数量差異、未検収/過検収に関するお問合せ\n" +
                "umc-japan-purchasing@umc.co.jp\n" +
                "[調達G 納期窓口]\n\n" +
                "以上\n\n" +
                "ユー・エム・シー・エレクトロニクス　株式会社　購買統括部\n" +
                "〒362-0022\n" +
                "埼玉県上尾市瓦葺721\n" +
                "TEL：048-615-1717";

            // 遍历选中的行，提取所需数据
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });

            aSelectedData.forEach(function (oData) {
                var invNo = oData.INV_NO; // 获取 INV_NO
                var supplierDescription = oData.SUPPLIER_DESCRIPTION; // 获取 SUPPLIER_DESCRIPTION
                var glYear = oData.GL_YEAR; // 获取 GL_YEAR

                // 调试日志，检查 glYear 是否为 undefined
                if (typeof glYear === "undefined") {
                    console.error("glYear is undefined for data: ", oData);
                    return; // 如果 glYear 为 undefined，则跳过当前数据
                }

                // 提取年份和月份
                var year = glYear.substring(0, 4); // 前四个字符作为年份
                var month = glYear.substring(4, 6); // 后两个字符作为月份

                // 替换模板中的占位符
                var sEmailContent = sTemplate.replace("{仕入先名称}", supplierDescription)
                    .replace(/{YEAR}/g, year)
                    .replace(/{MONTH}/g, month);

                // 将提取的数据添加到参数数组中
                aEmailParams.push({
                    INV_NO: invNo,
                    SUPPLIER_DESCRIPTION: supplierDescription,
                    EMAIL_CONTENT: sEmailContent
                });
            });

            // 将参数转换为字符串或其他格式，以便传递给后端
            var sParams = JSON.stringify(aEmailParams); // 根据后端需求调整格式

            // 调用自定义 action
            oModel.callFunction("/PCH04_SENDEMAIL", {
                method: "POST",
                urlParameters: {
                    "parms": sParams // 传递提取的参数
                },
                success: function (oData, response) {
                    MessageToast.show("邮件发送成功: " + oData);
                },
                error: function (oError) {
                    // 提取更多的错误信息进行调试
                    var errorMessage = oError.message || "发生未知错误";
                    if (oError.responseText) {
                        try {
                            var responseJSON = JSON.parse(oError.responseText);
                            errorMessage = responseJSON.error.message.value || errorMessage;
                        } catch (e) {
                            console.error("Error parsing response text: ", oError.responseText);
                        }
                    }
                    MessageBox.error("发送邮件失败: " + errorMessage);
                }
            });
        },

        // 检查 ZABC 的值的方法
        checkZABC: function () {
            // 根据你的逻辑获取 ZABC 的值
            // 例如，可以通过 OData 请求获取，或从视图中获取等
            // 返回 ZABC 的值
            return "W"; // 示例，返回 W，实际中你应该根据逻辑返回
        },

        // 检查 ZABC 值是否合法的方法
        isZABCValid: function (zabcValue) {
            // 根据业务逻辑判断 ZABC 值是否合法
            // 只有 ZABC = 'W' 时返回 true
            return zabcValue === "W"; // 只有当 ZABC 为 'W' 时有效
        }

    });
});
