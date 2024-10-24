sap.ui.define([
    "umc/app/Controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Controller,A, MessageToast, MessageBox) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch09_forcast_d", {

        onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
                oEvent.preventDefault(); // 取消导出操作
                return;
            }

            var oSettings = oEvent.getParameter("exportSettings");
            if (oSettings) {
                console.log("onBeforeExport called");
                console.log("Export Settings:", oSettings);
                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
                // 设置文件名为当前日期和时间
                oSettings.fileName = `FCレポート_${sDate}${sTime}.xlsx`;
            }
        },

        onResend: function () {
			var that = this;
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

            // 遍历选中的行，提取所需数据
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });  
  

            // 检查 SUPPLIER 是否相同
            var supplierSet = new Set(aSelectedData.map(data => data.SUPPLIER));
            if (supplierSet.size > 1) {
                MessageBox.error("複数の仕入先がまとめて配信することができませんので、1社の仕入先を選択してください。");
                return;
            }

            // 假设您在这里定义邮件内容模板
            var supplierName = "";
            var year = "";
            var month = "";
            var nextMonthDate = ""; // 用于存储下个月的第二个工作日
            var finalYear = "";     // 用于存储最终年份
            var finalMonth = "";    // 用于存储最终月份
            var finalDay = "";      // 用于存储最终日期

            aSelectedData.map(function (data) {
                supplierName = data.NAME1 || "未指定"; // 默认值
                
                // 使用当前年月
                var currentDate = new Date();
                year = currentDate.getFullYear().toString(); // 当前年份
                month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // 当前月份，确保是两位数格式
            });

    
                // 构建邮件内容对象
                var mailobj = {
                    emailJson: {
                        TEMPLATE_ID: "UWEB_M001",
                        MAIL_TO: [
                            "zhao.wang@sh.shin-china.com",
                            "xiaochen.huang@sh.shin-china.com"
                            
                        ].join(", "), // 使用逗号和空格连接
                        MAIL_BODY: [
                            {
                                object: "vendor",
                                value: supplierName
                            },
                            {
                                object: "yyyy",
                                value: year
                            },
                            {
                                object: "mm",
                                value: month
                            },
                            {
                                object: "absama",
                                value: "测试没问题　様"
                            }

                        ]
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
            
        }                                                                                                                                                                                                                                                                                          
        
    });
});
