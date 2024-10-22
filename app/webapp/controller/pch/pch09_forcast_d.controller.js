sap.ui.define([
    "umc/app/Controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Controller,A, MessageToast, MessageBox) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch09_forcast_d", {

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
                    supplierName = data.SUPPLIER_DESCRIPTION || "未指定"; // 默认值
                    year = data.INV_MONTH ? data.INV_MONTH.substring(0, 4) : "年"; // 默认值
                    month = data.INV_MONTH ? data.INV_MONTH.substring(4, 6) : "月"; // 默认值
                
                    // 将提取的 year 和 month 转换为数值类型
                    var invYear = parseInt(year, 10);
                    var invMonth = parseInt(month, 10);
                
                    // 计算下一个月的年和月
                    var nextMonth = invMonth === 12 ? 1 : invMonth + 1;
                    var nextYear = invMonth === 12 ? invYear + 1 : invYear;
                
                    // 获取下个月的第一天
                    var nextMonthFirstDay = new Date(nextYear, nextMonth - 1, 1);
                
                    // 查找第二个工作日（非周末）
                    var workDayCount = 0;
                    for (var i = 0; i < 7; i++) {
                        var tempDate = new Date(nextMonthFirstDay);
                        tempDate.setDate(nextMonthFirstDay.getDate() + i);
                        var dayOfWeek = tempDate.getDay();
                        if (dayOfWeek !== 0 && dayOfWeek !== 6) { // 0: Sunday, 6: Saturday
                            workDayCount++;
                            if (workDayCount === 2) {
                                nextMonthDate = tempDate; // 第二个工作日
                                break;
                            }
                        }
                    }
                
                    // 在第二个工作日的基础上加 7 天
                    var finalDateObj = new Date(nextMonthDate);
                    finalDateObj.setDate(nextMonthDate.getDate() + 7);
                
                    // 处理跨年和跨月，格式化最终日期
                    finalYear = finalDateObj.getFullYear();
                    finalMonth = ("0" + (finalDateObj.getMonth() + 1)).slice(-2); // 月份补零
                    finalDay = ("0" + finalDateObj.getDate()).slice(-2);          // 日期补零
                });
                
                // 构建邮件内容对象
                var mailobj = {
                    emailJson: {
                        TEMPLATE_ID: "UWEB_M007",
                        MAIL_TO: [
                            "xiaoyue.wang@sh.shin-china.com",
                            "huifang.ji@sh.shin-china.com"
                        ].join(", "), // 使用逗号和空格连接
                        MAIL_BODY: [
                            {
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
                            },
                            {
                                object: "YEAR1",
                                value: finalYear+'' // 替换后的年
                            },
                            {
                                object: "MONTH1",
                                value: finalMonth // 替换后的月
                            },
                            {
                                object: "DAY1",
                                value: finalDay // 替换后的日
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

        // onExportToPDF: function () {
		// 	var that = this;
		// 	let options = { compact: true, ignoreComment: true, spaces: 4 };
		// 	var IdList = that._TableDataList("detailTable", 'SUPPLIER')
		// 	if (IdList) {
		// 		that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T04_PAYMENT_SUM_HJ6", "SUPPLIER").then((oData) => {
		// 			let sResponse = json2xml(oData, options);
		// 			console.log(sResponse)
		// 			that.setSysConFig().then(res => {
		// 				// that.PrintTool._detailSelectPrint(that, sResponse, "test/test", oData, null, null, null, null)
		// 				that.PrintTool._detailSelectPrintDow(that, sResponse, "test02/test02", oData, null, null, null, null)
		// 			})
		// 		})
		// 	}


		// }

        onExportToPDF: function () {
            var that = this;
            
            // 调用检查逻辑
            if (!this.checkSelectedSuppliers()) {
                return; // 如果检查不通过，则终止执行
            }
        
            let options = { compact: true, ignoreComment: true, spaces: 4 };
            var IdList = that._TableDataList("detailTable", 'SUPPLIER');
        
            if (IdList) {
                that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T04_PAYMENT_SUM_HJ6", "SUPPLIER").then((oData) => {
                    let sResponse = json2xml(oData, options);
                    console.log(sResponse);
                    that.setSysConFig().then(res => {
                        // 调用打印方法
                        that.PrintTool._detailSelectPrintDow(that, sResponse, "test02/test05", oData, null, null, null, null);
                    });
                });
            }
        },
        
        checkSelectedSuppliers: function () {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();
        
            // 检查是否有选中的数据
            if (aSelectedIndices.length === 0) {
                MessageToast.show("選択されたデータがありません、データを選択してください。");
                return false;
            }
        
            // 遍历选中的行，提取所需数据
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });
        
            // 检查 SUPPLIER 是否相同
            var supplierSet = new Set(aSelectedData.map(data => data.SUPPLIER));
            if (supplierSet.size > 1) {
                MessageBox.error("複数の取引先がまとめて配信することができませんので、1社の取引先を選択してください。");
                return false;
            }
        
            return true; // 检查通过
        }

    });
});
