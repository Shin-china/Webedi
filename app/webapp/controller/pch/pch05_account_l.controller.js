sap.ui.define([
    "umc/app/controller/BaseController",
    "sap/ui/model/Filter",
    "umc/app/model/formatter",
    "sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
    "use strict";   

/**
	 * 共通用对象 全局
	 */
var _objectCommData = {
    _entity: "/PCH_T05_FOREXCEL", //此本页面操作的对象//绑定的数据源视图
    _aFilters: undefined

};

    return Controller.extend("umc.app.controller.pch.pch05_account_l", {
        formatter : formatter,

        onInit: function () {

            var oViewModel = new sap.ui.model.json.JSONModel({
				// isButtonEnabled: false // 默认值为 true
			});
			this.getView().setModel(oViewModel, "viewModel");
            // 初始化代码
            // 这里可以添加其他初始化逻辑，比如绑定数据等
            console.log("Controller initialized.");
            this.getRouter().getRoute("RouteCre_pch05").attachPatternMatched(this._onRouteMatched, this);
            //  设置版本号
			this._setOnInitNo("PCH05", ".20250209.01");
        },
        _onRouteMatched: function (oEvent) {
			this._setAuthByMenuAndUser("PCH05");
		},
        onExport: function () {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();
            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("noSelection")); // 提示未选择数据
                return;
            }
            var aSelectedData = aSelectedIndices.map(function (iIndex) {
                return oTable.getContextByIndex(iIndex).getObject();
            });
            if (aSelectedData.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("noSelection")); // 提示没有数据
                return;
            }
            var aColumns = oTable.getColumns().map(function (oColumn) {
                return {
                    label: oColumn.getLabel().getText(),
                    type: "string",
                    property: oColumn.getTemplate().getBindingPath("text"),
                    width: parseFloat(oColumn.getWidth())
                };
            });
            new Spreadsheet({
                dataSource: aSelectedData, // 仅传递选中的数据
                columns: aColumns,
                fileName: "Export.xlsx",
                worker: false,  // Disable web worker for simplicity
                beforeExport: this.onBeforeExport.bind(this)
            }).build().finally(function () {
                sap.m.MessageToast.show(this._ResourceBundle.getText("exportFinished"));
            }.bind(this));
        },
        onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();
        
            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください。")); // 提示未选择数据
                oEvent.preventDefault(); // 取消导出操作
                return;
            }

                // 获取导出设置
                var oSettings = oEvent.getParameter("exportSettings");

                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
                oSettings.fileName = `差額レポート_${sDate}${sTime}.xlsx`;
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
				// that._setBusy(false);
				// return;
			}
			this._readEntryByServiceAndEntity(_objectCommData._entity, aFilters, null).then((oData) => {

                // 如果获取到数据
        if (oData) {
            // 动态生成文件名
            var oDate = new Date();
            var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, ''); // 格式化为YYYYMMDD
            var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, ''); // 格式化为HHMMSS

            // 生成文件名
            var fileName = `消費税差額差処理_${sDate}${sTime}.xlsx`;}

			var selectedIndices = this._TableList("detailTable");
			if (selectedIndices) {
				// if(this.checkDelete(selectedIndices)){
					this._callCdsAction("/PCH05_EXCELDOWNLOAD", this._getDataDow(oData), this).then((oData) => {
						const downloadLink = document.createElement("a");
						const blob = that._base64Blob(oData.PCH05_EXCELDOWNLOAD,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
						const blobUrl = URL.createObjectURL(blob);
						downloadLink.href = blobUrl;
						// downloadLink.download = "消費税差額差処理.xlsx";
                        downloadLink.download = fileName; // 使用动态生成的文件名
						downloadLink.click();
						that._setBusy(false); 
					})
				}
            });
		},
		_getDataDow(oData){
			var jsondata = Array();
			oData.results.forEach((odata) => {
                //ADD BY STANLEY 20250104
                odata.LASTDATE = new Date(odata.INV_MONTH.substring(0,4),odata.INV_MONTH.substring(4,6),0);
                //END ADD
				jsondata.push(odata);
			})

			var a = JSON.stringify({ list: jsondata });
			var oPrams = {
                parms: a,
			};
			return oPrams;
			
		},
   
       },
        
    );
});