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

            var aSelectedIndices =this._TableDataList("detailTable","INV_NO")         
           
            if (aSelectedIndices) {
                // 启用 onResend 按钮，通过更新 viewModel
                this.getView().getModel("viewModel").setProperty("/isButtonEnabled", true);
                var par = {parms:JSON.stringify(aSelectedIndices)};

                this._callCdsAction("/PCH05_CONFIRM",  par, this)
                    .then((oData) => {
                        sap.m.MessageToast.show("インボイス確定成功！");
                        this.getModel().refresh(true); //刷新数据
                    })
                                // 设置标志为已确认
                this.isConfirmed = true;
                
            }
        
            // 调用 CDS Action

        },


        onCancel: function () {
            var that = this;
            var oTable = this.getView().byId("detailTable");


            var aSelectedIndices =this._TableDataList("detailTable","INV_NO");
           
            if (aSelectedIndices) {
                // 启用 onResend 按钮，通过更新 viewModel
                
                this.getView().getModel("viewModel").setProperty("/isButtonEnabled", false);
                var par = {parms:JSON.stringify(aSelectedIndices)};
                var that = this;
    
                var jsonModel = that.getModel("viewModel");
                var data = jsonModel.getData();
                this._callCdsAction("/PCH05_CANCEL",  par, this)
                    .then((oData) => {
                        sap.m.MessageToast.show("インボイス解除成功！");
                        this.isConfirmed = false; // 取消确认状态
                        
                        // // 更新 viewModel 或其他属性
                        // this.getView().getModel("viewModel").setProperty("/isButtonEnabled", true); // 重新启用按钮

                        // // 假设 myArray.list 是最新的列表数据
                        // var jsonModel = this.getView().getModel("jsonModel"); // 获取 JSONModel
                        // jsonModel.setData(myArray.list); // 更新模型数据

                        // // 强制刷新表格绑定
                        // var oTableBinding = oTable.getBinding("items");
                        // if (oTableBinding) {
                        //     oTableBinding.refresh(); // 强制刷新表格绑定
                        // }
                        this.getModel().refresh(true); //刷新数据
                    })

                   

            }
        
            // // 调用 CDS Action

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
							that.PrintTool._detailSelectPrintEmil(that, sResponse, "test02/test03", oData, null,"買掛金明細", null, null).then(()=>{
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
