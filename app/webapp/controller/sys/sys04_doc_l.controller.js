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
        
        },
        onDownload:function(){
		//test
		var mailObj = { attachmentJson:[{
			object: "3a8b4654",
			value: "3a8b4654-221c-4537-a324-4e64aee8d6ba.pdf"
		}]}



		// let newModel = this.getView().getModel('Common');
		// let oBind = newModel.bindList("/s3DownloadAttachment");
		// oBind.setIgnoreMessages(true);
	    // var response= oBind.create(mailObj);
		
		
		// response.created().then(function() {
		//  	debugger;
		// })

	//  	let newModel = this.getView().getModel('Common').bindContext("/s3DownloadAttachment(...)");
	//   	newModel.setParameter("attachmentJson", JSON.stringify(mailObj));
		
	//  	newModel.execute("$auto").then(()=>{
	//   	 	debugger;
	//   });
		// var newModel = this.getView().getModel('Common').bindContext("/s3DownloadAttachment.Common(...)",JSON.stringify(mailObj))
		// .invoke("$auto",false,null,/*bReplaceWithRVC*/false);
		//newModel.setParameter("attachmentJson", JSON.stringify(mailObj));
     	// newModel.execute("$auto",true,null,/*bReplaceWithRVC*/true).then(()=>{
	    // 	 	debugger;
	    // });
		$.ajax({
			url:"http://localhost:9000/odata/v4/Common/s3DownloadAttachment",
			type:"POST",
			contentType: "application/json; charset=utf-8",
			dataType:"json",
			crossDomain:true,
			data:JSON.stringify(mailObj),
			success:function(range){
				debugger;
			}
		})

        }
	});
});