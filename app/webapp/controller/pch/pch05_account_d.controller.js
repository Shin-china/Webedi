sap.ui.define([
	"umc/app/Controller/BaseController",
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Controller,A, MessageToast, MessageBox) {
    "use strict";

    return Controller.extend("umc.app.controller.pch.pch05_pay_d", {

        // onResend: function () {
        //     var oTable = this.getView().byId("detailTable");
        //     var aSelectedIndices = oTable.getSelectedIndices();
   
        //     // 检查是否有选中的数据
        //     if (aSelectedIndices.length === 0) {
        //         MessageToast.show("選択されたデータがありません、データを選択してください。");
        //         return;
        //     }

        //     var oModel = this.getView().getModel();
        //     var oCommonModel = this.getView().getModel("Common"); // 获取公共模型
        //     var aEmailParams = [];

        //         // 遍历选中的行，提取所需数据
        //         var aSelectedData = aSelectedIndices.map(function (iIndex) {
        //             return oTable.getContextByIndex(iIndex).getObject();
        //         });    

        //          // 检查 SUPPLIER 是否相同
        //         var supplierSet = new Set(aSelectedData.map(data => data.SUPPLIER_DESCRIPTION));
        //         if (supplierSet.size > 1) {
        //             MessageBox.error("複数の取引先がまとめて配信することができませんので、1社の取引先を選択してください。");
        //             return;
        //         }
       
        //         // 检查是否有选中的数据
        //         if (aSelectedIndices.length === 0) {
        //             MessageToast.show("選択されたデータがありません、データを選択してください。");
        //             return;
        //         }
    
        //                 // 检查 SUPPLIER 是否相同
        //                 var supplierSet = new Set(aSelectedData.map(data => data.SUPPLIER_DESCRIPTION));
        //                 if (supplierSet.size > 1) {
        //                     MessageBox.error("複数の取引先がまとめて配信することができませんので、1社の取引先を選択してください。");
        //                     return;
        //                 }

		// 		if (selectedIndices) {
		// 			// 构建CSV内容  
		// 			// var csvContent = "data:text/csv;charset=utf-8,";
		// 			var csvContent = "";
		// 			var headers = Object.keys(selectedIndices[0]); // 假设所有条目的结构都相同，取第一条的键作为表头  
		// 			headers.shift();
		// 			// csvContent += headers.join(",") + "\n";  

		// 			selectedIndices.forEach(function (row) {
		// 				var values = headers.map(function (header) {

		// 					return (row[header] === null || row[header] === undefined) ? "" : '"' + row[header] + '"';
		// 				});

		// 				csvContent += values.join(",") + "\n";
		// 			});
		// 		}

		// 		let options = { compact: true, ignoreComment: true, spaces: 4 };
		// 		var IdList = that._TableDataList("detailTable", 'SUPPLIER')
		// 		if (IdList) {
		// 			that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T05_ACCOUNT_DETAIL_SUM_FINAL", "SUPPLIER").then((oData) => {
		// 				let sResponse = json2xml(oData, options);
		// 				console.log(sResponse)
		// 				that.setSysConFig().then(res => {
		// 					that.PrintTool._detailSelectPrint(that, sResponse, "test2/test3", oData, null, null, null, null).then(()=>{
		// 						that.PrintTool.getImageBase64(that._blob).then((odata)=>{

		// 						var mailobj = {
		// 							emailJson: {
		// 								TEMPLATE_ID: "UWEB_M008",
		// 								MAIL_TO: "xiaoyue.wang@sh.shin-china.com",
		// 								MAIL_BODY: [{
		// 									object: "content",
		// 									value: "hello"
		// 								},
		// 								{
		// 									object: "recipient",
		// 									value: "aa"
		// 								},
		// 								{
		// 									object: "filename_1",
		// 									value: "aa.pdf"
		// 								},
		// 								{
		// 									object: "filecontent_1",
		// 									value: odata.replace("data:application/pdf;base64,","")
		// 								},
										
		// 								{
		// 									object: "filename_2",
		// 									value: "test.csv"
		// 								},
		// 								{
		// 									object: "filecontent_2",
		// 									value: csvContent
		// 								}
		// 							]
		// 							}
		// 						};

		// 						let newModel = this.getView().getModel("Common");
		// 						let oBind = newModel.bindList("/sendEmail");
		// 						oBind.create(mailobj);
		// 						})								
		// 					})						
		// 				})
		// 			})
		// 		}				
        //     },

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
                }
            },
             
        onExportToPDF: function () {
			var that = this;
			let options = { compact: true, ignoreComment: true, spaces: 4 };
			var IdList = that._TableDataList("detailTable", 'SUPPLIER')
			if (IdList) {
				that.PrintTool._getPrintDataInfo(that, IdList, "/PCH_T05_ACCOUNT_DETAIL_SUM_FINAL", "SUPPLIER").then((oData) => {
					let sResponse = json2xml(oData, options);
					console.log(sResponse)
					that.setSysConFig().then(res => {
						// that.PrintTool._detailSelectPrint(that, sResponse, "test/test", oData, null, null, null, null)
						that.PrintTool._detailSelectPrintDow(that, sResponse, "test2/test3", oData, null, null, null, null)
					})
				})
			}


		}

    });
});
