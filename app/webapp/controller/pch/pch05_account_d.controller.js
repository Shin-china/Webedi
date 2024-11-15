sap.ui.define([
	"umc/app/Controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "umc/app/model/formatter" ,
], function (Controller,A, MessageToast, MessageBox,formatter) {
    "use strict";

    var _objectCommData = {
		_entity: "/PCH_T05_FOREXCEL",
	};

    return Controller.extend("umc.app.controller.pch.pch05_pay_d", {
        formatter: formatter, // 将格式化器分配给控制器

        onInit: function () {

            var oViewModel = new sap.ui.model.json.JSONModel({
				isButtonEnabled: false // 默认值为 true
			});
			this.getView().setModel(oViewModel, "viewModel");
            // 初始化代码
            // 这里可以添加其他初始化逻辑，比如绑定数据等
            console.log("Controller initialized.");
        },

        onConfirm: function () {
			var that = this;
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            // 获取选中行的 SUPPLIER 字段值
           var oSelectedRow = oTable.getContextByIndex(aSelectedIndices[0]).getObject(); // 获取选中行的数据对象
           var supplierValue = oSelectedRow.SUPPLIER; // 获取选中行的 SUPPLIER 字段值

            // 设置标志为已确认
            this.isConfirmed = true;

            // 启用 onResend 按钮，通过更新 viewModel
            this.getView().getModel("viewModel").setProperty("/isButtonEnabled", true);

            // 遍历所有行，选中与选中行的 SUPPLIER 字段相等的行，并更新 INV_DATE 字段
            var aAllItems = oTable.getItems(); // 获取所有的行数据
            aAllItems.forEach(function (oItem) {
            var oRowContext = oItem.getBindingContext();
            var oRowData = oRowContext.getObject(); // 获取当前行的数据对象

                if (oRowData.SUPPLIER === supplierValue) {
                    // 如果行的 SUPPLIER 字段值与选中行相同，选中该行并更新 INV_DATE 字段为 "確定"
                    oItem.setSelected(true);
                    oRowData.INV_DATE = "確定"; // 更新 INV_DATE 字段
                    oRowContext.setProperty("INV_DATE", "確定"); // 使用 setProperty 更新 INV_DATE 字段值
                } else {
                    // 否则，取消选中该行
                    oItem.setSelected(false);
                }
            });
        },

        // 取消按钮点击事件
        onCancel: function () {
            // 将 isButtonEnabled 设置为 false，使 onResend 按钮不可用
            this.isConfirmed = false; // 取消确认状态
            this.getView().getModel("viewModel").setProperty("/isButtonEnabled", false);

            // 你可以添加其他取消的逻辑，例如清除选择的表格行
            var oTable = this.getView().byId("detailTable");
            oTable.removeSelections(); // 清除表格中的选中行

            // 遍历所有行，清空 INV_DATE 字段
            var aAllItems = oTable.getItems(); // 获取所有的行数据
            aAllItems.forEach(function (oItem) {
                var oRowContext = oItem.getBindingContext();
                var oRowData = oRowContext.getObject(); // 获取当前行的数据对象

                // 清空 INV_DATE 字段
                oRowData.INV_DATE = ""; // 或者 oRowData.INV_DATE = null; 根据你的需求
                oRowContext.setProperty("INV_DATE", ""); // 使用 setProperty 清空 INV_DATE 字段值
            });
        },
        
        onResend: function () {
			var that = this;
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            
             // 将日期字符串转换为指定格式
             function formatDateString(dateString) {
                const date = new Date(dateString);
                const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
                const formattedDate = date.toLocaleDateString('zh-CN', options);
                
                // 将日期格式调整为 YYYY/MM/DD
                return formattedDate.replace(/\//g, '/'); // 保证分隔符为 /
              }
   
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
                    MessageBox.error("複数の取引先がまとめて配信することができませんので、1社の取引先を選択してください。");
                    return;
                }
       
                // 检查是否有选中的数据
                if (aSelectedIndices.length === 0) {
                    MessageToast.show("選択されたデータがありません、データを選択してください。");
                    return;
                }
    
                        // 检查 SUPPLIER 是否相同
                        var supplierSet = new Set(aSelectedData.map(data => data.SUPPLIER));
                        if (supplierSet.size > 1) {
                            MessageBox.error("複数の取引先がまとめて配信することができませんので、1社の取引先を選択してください。");
                            return;
                        }



				let options = { compact: true, ignoreComment: true, spaces: 4 };
				var IdList = that._TableDataList("detailTable", 'SUPPLIER')
				if (IdList) {
					that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T05_ACCOUNT_DETAIL_EXCEL", "SUPPLIER").then((oData) => {
						
                        oData.results.forEach(function (row) {
    
                            // 格式化日期
                            if (row.INV_POST_DATE) {
                                row.INV_POST_DATE = formatDateString(row.INV_POST_DATE);
                            }
    
                    });

                    let sResponse = json2xml(oData, options);
						console.log(sResponse)
						that.setSysConFig().then(res => {
							that.PrintTool._detailSelectPrintEmil(that, sResponse, "test02/test03", oData, null, null, null, null).then(()=>{
								that.PrintTool.getImageBase64(that._blob).then((odata)=>{

								var mailobj = {
									emailJson: {
										TEMPLATE_ID: "UWEB_M008",
										// MAIL_TO: "xiaoyue.wang@sh.shin-china.com",
                                        MAIL_TO: [
                                            "xiaoyue.wang@sh.shin-china.com",
                                            "huifang.ji@sh.shin-china.com"
                                        ].join(", "), // 使用逗号和空格连接
										MAIL_BODY: [
										{
											object: "filename_1",
											value: "PO_買掛金計上高明細表.pdf"
										},
										{
											object: "filecontent_1",
											value: odata.replace("data:application/pdf;base64,","")
										},
				
									]
									}
								};

								let newModel = this.getView().getModel("Common");
								let oBind = newModel.bindList("/sendEmail");
								oBind.create(mailobj);

                                that._callCdsAction(_objectCommData._entity, { parms: IdList[0] }, that).then((data) => {


                                });

								})								
							})						
						})
					})
				}				
            },


            

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
                    oSettings.fileName = `買掛金明細_${sDate}${sTime}.xlsx`;

				oSettings.workbook.columns.forEach(function (oColumn) {
					if (oColumn.property === "INV_POST_DATE" || oColumn.property === "INV_BASE_DATE" || oColumn.property === "GR_DATE") {
						oColumn.type = sap.ui.export.EdmType.Date; // 设置为日期类型
						oColumn.format = "yyyy/M/d"; // 设置日期格式，去掉时间
					}
				});

                }
            },
             
        onExportToPDF: function () {
            var that = this;
            
            // 调用检查逻辑
            if (!this.checkSelectedSuppliers()) {
                return; // 如果检查不通过，则终止执行
            }
        
            let options = { compact: true, ignoreComment: true, spaces: 4 };
            var IdList = that._TableDataList("detailTable", 'SUPPLIER');

             // 将日期字符串转换为指定格式
             function formatDateString(dateString) {
                const date = new Date(dateString);
                const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
                const formattedDate = date.toLocaleDateString('zh-CN', options);
                
                // 将日期格式调整为 YYYY/MM/DD
                return formattedDate.replace(/\//g, '/'); // 保证分隔符为 /
              }
        
            if (IdList) {
                that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T05_ACCOUNT_DETAIL_EXCEL", "SUPPLIER").then((oData) => {
                    oData.results.forEach(function (row) {
    
                        // 格式化日期
                        if (row.INV_POST_DATE) {
                            row.INV_POST_DATE = formatDateString(row.INV_POST_DATE);
                        }

                });

                    let sResponse = json2xml(oData, options);
                    console.log(sResponse);
                    that.setSysConFig().then(res => {
                        // 调用打印方法
                        that.PrintTool._detailSelectPrintDow(that, sResponse, "test02/test03", oData, null, null, null, null);
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
