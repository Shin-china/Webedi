jQuery.sap.require("umc/app/lib/xlsx");
sap.ui.define(
  ["umc/app/controller/BaseController", "sap/ui/model/odata/v2/ODataModel", "sap/ui/export/library", "sap/ui/model/json/JSONModel","sap/ui/export/Spreadsheet"],
  function (Controller, ODataModel, exportLibrary, JSONModel) {
    "use strict";
    return Controller.extend("umc.app.controller.sys.sys07_uplod_l", {
      /*++++++++++++++++++++++++++++++
		  初始化
		  ++++++++++++++++++++++++++++++*/
      onInit: function (oEvent) { 
        // Message Init
        //初始化 msg
        this.MessageTools._clearMessage();
        // this.getRouter().getRoute("RouteCre_SYS07_l").attachPatternMatched(this._onRouteMatched, this);
        //  设置版本号
        this._setOnInitNo("SYS07", ".20241225.01");
      },
	  	/*==============================
		编辑
		==============================*/
		onEdi: function (oEvent) {
			var that = this;
			that._setEditable(true);
		},
		onSerach: function(oEvent) {
			var that = this;
			that._setEditable(false);
		  },
		/*==============================
		保存
		==============================*/
		onSav: function (oEvent) {
			var that = this;
			that._setBusy(true);
			var view = this.getView();
			//清除msg
			var boo = this.MessageTools._checkInputErrorMSG();
			var selectedIndices = this._TableListAll("detailTable");
			if (selectedIndices && boo) {
				this._callCdsAction("/SYS07_SAVE_DATA_L", this._getData(selectedIndices), this).then((oData) => {
					that._setBusy(false); 
					that._setEditable(false);
				})
	
			}else {
				that._setBusy(false); 
			}
			
			
		},
		onRebind: function (oEvent) {
			let sorts = ["PROJECT_NO", "REQUISITION_VERSION", "INI_ITEM_NO"];
			let ascs = [false, false, false]; //true desc false asc
			//手动添加排序
			this._onListRebinSort(oEvent, sorts, ascs);
			// this._setAuthByMenuAndUser("SYS03");
		  },
		_getData(selectedIndices){
			var pList = Array();
			selectedIndices.forEach((odata) => {
			    var p = {
					po: odata.PROJECT_NO,
					ver: odata.REQUISITION_VERSION,
					dno: odata.INI_ITEM_NO,
					ITEM: odata.ITEM,
					QUANTITY: odata.QUANTITY,
					UNIT_PRICE: odata.UNIT_PRICE,
					LIFE: odata.LIFE,
					ASSETS: odata.ASSETS,
					PAYMENT: odata.PAYMENT,
					REMARKS: odata.REMARKS,
					CURRENCY: odata.CURRENCY
					
				}
				pList.push(p);
			})
			return {SYS07Json: JSON.stringify(pList)};
			
		},

		/*==============================
		删除
		==============================*/
		onDelete: function (oEvent) {
			var that = this;
			that._setBusy(true);
			var view = this.getView();
			var selectedIndices = this._TableList("detailTable");
			if (selectedIndices) {
				if(this.checkDelete(selectedIndices)){
					this._callCdsAction("/SYS07_DELETE_DATA_L", this._getData(selectedIndices), this).then((oData) => {
						that._setBusy(false); 
					})
				}

	
			}else {
				that._setBusy(false); 
			}
			
		},
 		/*==============================
		删除check
		==============================*/
		checkDelete: function (selectedIndices) {
			let boo = true;
			selectedIndices.forEach((odata) => {
				var sta = odata.STATUS_SALES;
				if(sta ){
					if('E' == sta || 'F' == sta || 'G' == sta){
						sap.m.MessageToast.show(this.MessageTools._getI18nMessage("SYS07_ERROR_MSG1", [odata.STATUS_SALES_NAME]));
						boo = false;
					}
					
					
				    
				}else{
					sap.m.MessageToast.show(this.MessageTools._getI18nMessage("SYS07_ERROR_MSG1", [""]));
					boo = false;
				}
				

			})
			return boo;
		},
  		/*==============================
		下载excel
		==============================*/
		onDownload: function (selectedIndices) {
			var that = this;
			that._setBusy(true);
			
			var selectedIndices = this._TableList("detailTable");
			if (selectedIndices) {
				if(this.checkDelete(selectedIndices)){
					this._callCdsAction("/SYS07_EXCEL", this._getDataDow(selectedIndices), this).then((oData) => {
						const downloadLink = document.createElement("a");
						const blob = that._base64Blob(oData.SYS07_EXCEL,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
						const blobUrl = URL.createObjectURL(blob);
						downloadLink.href = blobUrl;
						downloadLink.download = "test.xlsx";
						downloadLink.click();
						that._setBusy(false); 
					})
				}

	
			}else {
				that._setBusy(false); 
			}
		},
		_getDataDow(selectedIndices){
			var jsondata = Array();
			selectedIndices.forEach((odata) => {
			    
				jsondata.push(odata);
			})

			var a = JSON.stringify({ list: jsondata });
			var oPrams = {
			  SYS07Json: a,
			};
			return oPrams;
			
		},
    });
  }
);
