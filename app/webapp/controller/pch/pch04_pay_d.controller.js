sap.ui.define([
    "umc/app/controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator"
], function (Controller,A, MessageToast, MessageBox,Filter,FilterOperator) {
    "use strict";

    var _objectCommData = {
		_entity: "/PCH_T04_PAYMENT_SUM_HJ6",
	};


    return Controller.extend("umc.app.controller.pch.pch04_pay_d", {
        onInit: function () {
			

			this.getRouter().getRoute("RouteCre_pch04").attachPatternMatched(this._onRouteMatched, this);
			
			
			//  设置版本号
			this._setOnInitNo("PCH04", ".20250209.01");
		},


		_onRouteMatched: function (oEvent) {
			this._setAuthByMenuAndUser("PCH04");
		},
        onResend: function () {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            var that = this;
            var IdList = that._TableDataList("detailTable", 'SUPPLIER');
   
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
                var H_CODE = "MM0007";
                var SUPPLIER = supplierSet.values().next().value;
                var entity = "/SYS07_EMAIL";
             
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

                this._readHeadEmail(H_CODE, SUPPLIER, entity).then((oHeadData) => {
                    let mail = oHeadData.results && oHeadData.results.length > 0 ? 
                    oHeadData.results.map(result => result.EMAIL_ADDRESS).join(", ") : '';            
                let absama = oHeadData.results && oHeadData.results.length > 0 ? 
                    oHeadData.results.map(result => result.EMAIL_ADDRESS_NAME + " 様").join("  ") : '';

                    //Add by stanley 20241230
                    if (mail == "" || mail == null) {
                        MessageBox.error("仕入先のメールアドレスを取得できません");
                        return;
                    }

                    let mailobj = {
                        emailJson: {
                            TEMPLATE_ID: "UWEB_M007",
                            MAIL_TO: [mail].join(", "), 
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
                
            // let newModel = this.getView().getModel("Common");
            // let oBind = newModel.bindList("/sendEmail");
            // Add Confirm button by stanley 20241217
            var that = this;
            var confirmTxt = this.MessageTools._getI18nTextInModel("pch", "confirmTxt", this.getView());
            var confirmTitle = this.MessageTools._getI18nTextInModel("pch", "confirmTitle", this.getView());
            var confirmOk = this.MessageTools._getI18nTextInModel("pch", "confirmOk", this.getView());
            var confirmCancel = this.MessageTools._getI18nTextInModel("pch", "confirmCancel", this.getView());
            var sResponsivePaddingClasses = "sapUiResponsivePadding--header sapUiResponsivePadding--content sapUiResponsivePadding--footer";
            MessageBox.confirm(confirmTxt,{
                icon:MessageBox.Icon.INFORMATION,
                title:confirmTitle,
                actions: [confirmOk, confirmCancel],
                emphasized: confirmOk,
                initialFocus: confirmCancel,
                styleClass: sResponsivePaddingClasses,
                onClose: function(sAction) {
                  if(sAction === confirmOk){
                    that._sendEmail(mailobj);
                    that._setBusy(false);
                    
                    that._callCdsAction("/PCH04_SENDEMAIL", { parms: IdList[0] }, that).then((data) => {
                        
                    });
                }}
            });
            //END By Stanley


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
        },

        onExportToPDF: function () {
            var that = this;

            var aFilters = this.getView().byId("smartFilterBar").getFilters();
			_objectCommData._aFilters = aFilters;
			var view = this.getView();
			var jsonModel = view.getModel("workInfo");
			view.setModel(jsonModel, undefined);
			if (aFilters.length == 0) {
				that._setBusy(false);
				return;
			}
            //ADD BY STANLEY 20241230
             var aFilter = [];
             var times = 0;
             var singleCondition = false;
             if(aFilters[0].aFilters?.length !== undefined){
                 times = aFilters[0].aFilters.length;
             }else{
                 times = 1;
                 singleCondition = true;
             }
             for(var i = 0; i < times; i++){
               if(singleCondition){
                aFilter.push(new Filter(aFilters[0].sPath,
                    aFilters[0].sOperator,
                    aFilters[0].oValue1
                ))
               }else{
                if(aFilters?.[0]?.aFilters?.[i]?.sPath == undefined){
                    if(aFilters[0].aFilters[i].aFilters[0].sPath == "SUPPLIER"){
                    aFilter.push(new Filter(aFilters[0].aFilters[i].aFilters[0].sPath,
                        aFilters[0].aFilters[i].aFilters[0].sOperator,
                        aFilters[0].aFilters[i].aFilters[0].oValue1
                    ));
                    }}else{
                        aFilter.push(new Filter(aFilters[0].aFilters[i].sPath,
                        aFilters[0].aFilters[i].sOperator,
                        aFilters[0].aFilters[i].oValue1
                    ))
                    }
               }
             }
            //END ADD
			this._readEntryByServiceAndEntity(_objectCommData._entity, aFilter, null).then((oData) => {
            
            // 调用检查逻辑
            if (!this.checkSelectedSuppliers()) {
                return; // 如果检查不通过，则终止执行
            }
        
            let options = { compact: true, ignoreComment: true, spaces: 4 };
            var IdList = that._TableDataList("detailTable", 'DOWNLOADID');
            
            // 获取前台输入的 INV_MONTH 和 SUPPLIER
            // var invMonth = this.getView().byId("INV_MONTH").getValue(); 

            // 将日期字符串转换为指定格式
            function formatDateString(dateString) {
                const date = new Date(dateString);
                const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
                const formattedDate = date.toLocaleDateString('zh-CN', options);
                
                // 将日期格式调整为 YYYY/MM/DD
                return formattedDate.replace(/\//g, '/'); // 保证分隔符为 /
}
        
            if (IdList) {

                // 提取 IdList 的前几位，去掉后 6 位字符
                let processedIdList = IdList.map(id => id.slice(0, -6));

                // 从 IdList 的每个 ID 中提取最后 6 位，并拆分成年份和月份
                let yearMonthList = IdList.map(id => {
                    let lastSix = id.slice(-6); // 提取最后 6 位
                    let year = lastSix.slice(0, 4); // 前 4 位是年份
                    let month = lastSix.slice(4);  // 后 2 位是月份
                    return `${year}年${month}月度`; // 拼接为指定格式
                });

                 // 使用处理后的第一个值和年份-月份信息生成文件名
                var fileName = `${processedIdList[0]}_${yearMonthList[0]}UMC支払通知書.pdf`;

                that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T04_PAYMENT_SUM_HJ6", "DOWNLOADID").then((J) => {
                    // oData = this.jsonDateToString(oData);  
					J.results.forEach(function (row) {
    
                            // 格式化日期
                            if (row.INV_BASE_DATE) {
                                row.INV_BASE_DATE = formatDateString(row.INV_BASE_DATE);
                            }
                            if (row.GR_DATE) {
                                row.GR_DATE = formatDateString(row.GR_DATE);
                            }

                            //ADD BY STANLEY 20241230
                            row.SUPPLIER_DESCRIPTION = row.SUPPLIER_DESCRIPTION + " 御中";

					});

                    // 确保 oData[0] 存在
                    let sResponse = json2xml(J, options);
                    console.log(sResponse);
                    that.setSysConFig().then(res => {
                        // 调用打印方法
                        that.PrintTool._detailSelectPrintDow(that, sResponse, that.getGlobProperty("ADS_template_form") +"_rep04/T", J, null,fileName, null, null).then((oData) => {

                        var sapPo = {
                            po: IdList[0],
                            type: "PCH04",
                            fileName: fileName,
                        }
                        //打印pdf后写表共通
                        that.PrintTool.printBackActionPo(that,oData,sapPo)
                    })
                    });
                });
            }});
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
        },
        jsonDateToString: function (json) {
            for (let key in json) {
                if (json.hasOwnProperty(key)) {
                let value = json[key];
                // 如果值是日期，需要特殊处理
                if (value instanceof Date) {
                value = value.toISOString();
                json[key] = value;
                }
            }
            return json;
            }   
        },
        
        /*==============================
		下载excel
		==============================*/
		onExportToEXCEL: function (selectedIndices) {
			var that = this;
			that._setBusy(true);

            var aFilters = this.getView().byId("smartFilterBar").getFilters();
			_objectCommData._aFilters = aFilters;
			var view = this.getView();
			var jsonModel = view.getModel("workInfo");
			view.setModel(jsonModel, undefined);
			if (aFilters.length == 0) {
				that._setBusy(false);
				return;
			}
			this._readEntryByServiceAndEntity(_objectCommData._entity, aFilters, null).then((oData) => {

                // 调用检查逻辑
            if (!this.checkSelectedSuppliers()) {
                return; // 如果检查不通过，则终止执行
            }
        

                let options = { compact: true, ignoreComment: true, spaces: 4 };
                var IdList = that._TableDataList("detailTable", 'DOWNLOADID');

                

                if (IdList) {
                    that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T04_PAYMENT_SUM_HJ6", "DOWNLOADID").then((oData) => {
               
                    // 提取 IdList 的前几位，去掉后 6 位字符
                    let processedIdList = IdList.map(id => id.slice(0, -6));

                    // 从 IdList 的每个 ID 中提取最后 6 位，并拆分成年份和月份
                    let yearMonthList = IdList.map(id => {
                        let lastSix = id.slice(-6); // 提取最后 6 位
                        let year = lastSix.slice(0, 4); // 前 4 位是年份
                        let month = lastSix.slice(4);  // 后 2 位是月份
                        return `${year}年${month}月度`; // 拼接为指定格式
                    });

                     // 使用处理后的第一个值和年份-月份信息生成文件名
                    var fileName = `${processedIdList[0]}_${yearMonthList[0]}UMC支払通知書`;

					that._callCdsAction("/PCH04_EXCELDOWNLOAD", that._getDataDow(oData), that).then((J) => {
						const downloadLink = document.createElement("a");
						const blob = that._base64Blob(J.PCH04_EXCELDOWNLOAD,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
						const blobUrl = URL.createObjectURL(blob);
						downloadLink.href = blobUrl;
                        downloadLink.download = fileName; // 使用动态生成的文件名
						downloadLink.click();
						that._setBusy(false); 
                        //Convert Base64 to Blob
                        var mailObj = { attachmentJson:{
                            object: IdList[0],
                            object_type:"PCH04",
                            value:J.PCH04_EXCELDOWNLOAD,
                            file_type:btoa(blob.type),
                            file_name:fileName+".xlsx"
                          }}
                    
                        $.ajax({
                            url: "srv/odata/v4/Common/s3uploadAttachment",
                            type: "POST",
                            contentType: "application/json; charset=utf-8",
                            dataType: "json",
                            async: false,
                            crossDomain: true,
                            responseType: 'blob',
                            data: JSON.stringify(mailObj),
                            success: function (base64) {
                              console.log("上传成功");
                            }
                        })
					})

                    // var sapPo = {
                    //     po: IdList[0],
                    //     tpye: "PCH04",
                    //     fileName: fileName,
                    // }
                    // //打印pdf后写表共通
                    // that.PrintTool.printBackActionPo(that,oData,sapPo)
                    
            })};
        });

		},

		_getDataDow(oData){
			var jsondata = Array();
			oData.results.forEach((odata) => {
			    
				jsondata.push(odata);
			})

			var a = JSON.stringify({ list: jsondata });
			var oPrams = {
                parms: a,
			};
			return oPrams;
			
		},
        
    });
});
