sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "sap/ui/export/Spreadsheet"
], function (Controller, MessageToast, MessageBox, Spreadsheet) {
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

            // 获取选中行的数据
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });

            // 按 SUPPLIER 分组
            var oGroupedData = this.groupBySupplier(aSelectedData);

            // 遍历分组数据并发送邮件和生成文件
            Object.keys(oGroupedData).forEach(function (sSupplier) {
                var aGroupData = oGroupedData[sSupplier];
                var oFirstData = aGroupData[0];
                var supplierCode = oFirstData.SUPPLIER; // 获取 SUPPLIER 字段
                var invPostDate = oFirstData.INV_POST_DATE; // 获取 INV_POST_DATE

                // 将 INV_POST_DATE 转换为格式化字符串
                var formattedDate = this.formatDate(invPostDate);

                // 使用 OData Model 从 SYS_T11_MAIL_TEMPLATE 实体中获取邮件模板
                var sPath = "/SYS_T11_MAIL_TEMPLATE(TEMPLATE_ID='UWEB_M007')"; // 实体路径

                oModel.read(sPath, {
                    success: function (oData) {
                        if (!oData) {
                            MessageBox.error("邮件模板未找到。");
                            return;
                        }

                        var sTemplateContent = oData.MAIL_CONTENT;
                        var aEmailParams = [];

                        aGroupData.forEach(function (oData) {
                            var invNo = oData.INV_NO; // 获取 INV_NO
                            var supplierDescription = oData.SUPPLIER_DESCRIPTION; // 获取 SUPPLIER_DESCRIPTION

                            // 替换模板中的占位符
                            var sEmailContent = sTemplateContent.replace("{仕入先名称}", supplierDescription)
                                .replace(/{YEAR}/g, formattedDate.year)
                                .replace(/{MONTH}/g, formattedDate.month);

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

                                // 创建 Excel 文件并下载
                                new Spreadsheet({
                                    dataSource: aGroupData.map(this.convertDateFields), // 转换日期字段
                                    columns: oTable.getColumns().map(function (oColumn) {
                                        return {
                                            label: oColumn.getLabel().getText(),
                                            type: "string",
                                            property: oColumn.getTemplate().getBindingPath("text"),
                                            width: parseFloat(oColumn.getWidth())
                                        };
                                    }),
                                    fileName: supplierCode + "_" + formattedDate.year + "年" + formattedDate.month + "月度UMC支払通知書.xlsx", // 保持文件名为分组数据的特定名称
                                    worker: false // Disable web worker for simplicity
                                }).build().finally(function () {
                                    MessageToast.show("ファイルのダウンロードが完了しました");
                                });
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
                                MessageBox.error("メール送信に失敗しました: " + errorMessage);
                            }
                        });
                    },
                    error: function (oError) {
                        MessageBox.error("邮件模板的获取失败: " + oError.message);
                    }
                });
            }.bind(this)); // Ensure `this` context is preserved
        },

        onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                MessageToast.show("選択されたデータがありません、データを選択してください。");
                oEvent.preventDefault(); // 取消导出操作
                return;
            }

            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });

            // 按 SUPPLIER 分组
            var oGroupedData = this.groupBySupplier(aSelectedData);

            // 遍历分组数据
            Object.keys(oGroupedData).forEach(function (sSupplier) {
                var aGroupData = oGroupedData[sSupplier];
                var oFirstData = aGroupData[0];
                var supplierCode = oFirstData.SUPPLIER; // 获取 SUPPLIER 字段
                var invPostDate = oFirstData.INV_POST_DATE; // 获取 INV_POST_DATE

                // 确保 INV_POST_DATE 是 Date 对象并进行格式转换
                var formattedDate = this.formatDate(invPostDate);

                // 设置文件名
                var sFileName = supplierCode + "_" + formattedDate.year + "年" + formattedDate.month + "月度UMC支払通知書.xlsx";

                // 更新 exportSettings
                var oSettings = oEvent.getParameter("exportSettings");
                if (oSettings) {
                    oSettings.fileName = sFileName; // 设置文件名
                }
            }.bind(this)); // Ensure `this` context is preserved
        },

        // 按 SUPPLIER 字段分组数据
        groupBySupplier: function (aData) {
            return aData.reduce(function (oGrouped, oItem) {
                var key = oItem.SUPPLIER;
                if (!oGrouped[key]) {
                    oGrouped[key] = [];
                }
                oGrouped[key].push(oItem);
                return oGrouped;
            }, {});
        },

        // 格式化日期为用户友好的字符串
        formatDate: function (invPostDate) {
            if (typeof invPostDate === 'string' && invPostDate.startsWith('/Date(')) {
                invPostDate = this.parseODataTimestamp(invPostDate);
            }

            if (invPostDate instanceof Date && !isNaN(invPostDate.getTime())) {
                var year = invPostDate.getFullYear(); // 提取年份
                var month = ("0" + (invPostDate.getMonth() + 1)).slice(-2); // 提取月份并确保是两位数
                return { year: year, month: month };
            } else {
                MessageBox.error("INV_POST_DATE 的格式不正确: " + invPostDate);
                return { year: "", month: "" };
            }
        },

        // 解析 OData 时间戳格式
        parseODataTimestamp: function (odataTimestamp) {
            if (typeof odataTimestamp === 'string' && odataTimestamp.startsWith('/Date(') && odataTimestamp.endsWith(')/')) {
                var timestampStr = odataTimestamp.substring(6, odataTimestamp.length - 2);
                var timestamp = parseInt(timestampStr, 10);
                return new Date(timestamp);
            } else {
                throw new Error("Invalid OData timestamp format");
            }
        },

        // 转换日期字段以确保正确显示
        convertDateFields: function (oData) {
            if (typeof oData.INV_POST_DATE === 'string' && oData.INV_POST_DATE.startsWith('/Date(')) {
                // 处理 OData 时间戳格式
                var timestamp = this.parseODataTimestamp(oData.INV_POST_DATE);
                oData.INV_POST_DATE = this.formatDate(timestamp).year + "-" + this.formatDate(timestamp).month;
            } else if (oData.INV_POST_DATE instanceof Date) {
                oData.INV_POST_DATE = this.formatDate(oData.INV_POST_DATE).year + "-" + this.formatDate(oData.INV_POST_DATE).month;
            } else {
                // 处理其他日期格式
                var date = new Date(oData.INV_POST_DATE);
                if (!isNaN(date.getTime())) {
                    oData.INV_POST_DATE = this.formatDate(date).year + "-" + this.formatDate(date).month;
                }
            }
            return oData;
        }
    });
});
