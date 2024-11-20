sap.ui.define([
	"umc/app/controller/BaseController"
], function(
	Controller
) {
	"use strict";

	return Controller.extend("umc.app.controller.sys.sys04_doc_l", {

        /**
         * @override
         */
        onInit: function() {
			const that = this;
			const oMessageManager = sap.ui.getCore().getMessageManager();
			this.getView().setModel(oMessageManager.getMessageModel(),"message");
			oMessageManager.registerObject(this.getView(), true);
            // 设置版本号
            this._setOnInitNo("SYS02",".20240418"); 
        },
        onDownload:function(){
		//test
		let that = this;
		// var mailObj = { attachmentJson:[{
		// 	object: "3a8b4654",
		// 	value: "3a8b4654-221c-4537-a324-4e64aee8d6ba.pdf"
		// }]}
		// var newModel = this.getView().getModel('Common').bindContext("/s3DownloadAttachment.Common(...)",JSON.stringify(mailObj))
		// .invoke("$auto",false,null,/*bReplaceWithRVC*/false);
		//newModel.setParameter("attachmentJson", JSON.stringify(mailObj));
     	// newModel.execute("$auto",true,null,/*bReplaceWithRVC*/true).then(()=>{
	    // 	 	debugger;
	    // 
		//Test for excel
		// var excelVal = {
		// 	id:1,
		// 	mat:"mat1",
		// 	qty:1,
		// 	unp:1.0
		// }
		// var jsonExcel = { content:JSON.stringify(excelVal) }
		// this.getModel().callFunction("/EXCEL_TEST", {
		// 	method: "POST",
		// 	urlParameters: jsonExcel,
		// 	success: function(oData) {
		// 		debugger;
		// 		const downloadLink = document.createElement("a");
		// 		const blob = that._base64Blob(oData.EXCEL_TEST,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		// 		const blobUrl = URL.createObjectURL(blob);
		// 		downloadLink.href = blobUrl;
		// 		downloadLink.download = "test.xlsx";
		// 		downloadLink.click();
		// 	},
		// 	error: function(oError) {
		// 	}
		// })
		//End
		var dataList = this._getByIdObject("detailTable");
		for(var i=0;i<dataList.length;i++){
			var data = dataList[i];
			var att_type = "";
			var downloadJson = { attachmentJson:[{
				object:"download",
				value: data.OBJECT_LINK
			}]}
			// switch(data.FILE_TYPE){
			// 	case "pdf":
			// 		att_type = "application/pdf";
			// 		break;
			// 	case "csv" || "txt":
			// 		att_type = "text/plain";
			// 		break;	
			// }

			$.ajax({
				url:"srv/odata/v4/Common/s3DownloadAttachment",
				type:"POST",
				contentType: "application/json; charset=utf-8",
				dataType:"json",
				async:false,
				crossDomain:true,
				responseType:'blob',
				data:JSON.stringify(downloadJson),
				success:function(base64){
					const downloadLink = document.createElement("a");
					const blob = that._base64Blob(base64.value,data.FILE_TYPE);
					const blobUrl = URL.createObjectURL(blob);
					downloadLink.href = blobUrl;
					downloadLink.download = data.FILE_NAME;
					downloadLink.click();
				}
			})
		}


        },
		_base64Blob:function(base64,mimeType){
            const byteCharacters = atob(base64);
            const byteArrays = [];
            
            for (let offset = 0; offset < byteCharacters.length; offset += 512) {
              const slice = byteCharacters.slice(offset, offset + 512);
              const byteNumbers = new Array(slice.length);
              
              for (let i = 0; i < slice.length; i++) {
                byteNumbers[i] = slice.charCodeAt(i);
              }
              
              const byteArray = new Uint8Array(byteNumbers);
              byteArrays.push(byteArray);
            }
            
            return new Blob(byteArrays, {type: mimeType}); 
        },
		onBeforeRebindList:function(oEvent){
            let oBindingParams = oEvent.getParameter("bindingParams");
			oBindingParams.filters.push(new sap.ui.model.Filter("OBJECT_TYPE", "NE", "PCH08"));
		}

	});
});