sap.ui.define([
	"umc/app/controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch02_user_l", {
		formatter : formatter,

		onInit: function () {
		// 设置自己的 OData模型为默认模型
		this._setDefaultDataModel("TableService");
		//  设置版本号
		this._setOnInitNo("PCH02", ".20240812.01");
		// 设置选中框 无
		// this._setSelectionNone("detailTable");
		// this._setEditableAuth(true);
		},

		onRebind: function (oEvent) {
		// this._onListRebindDarft(oEvent);
		this._onListRebindDarft(oEvent, true);
		},
	
		onBeforeExport: function (oEvent) {
            var oTable = this.getView().byId("detailTable");
            var aSelectedIndices = oTable.getSelectedIndices();

            if (aSelectedIndices.length === 0) {
                sap.m.MessageToast.show(this._ResourceBundle.getText("選択されたデータがありません、データを選択してください。")); // 提示未选择数据
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
                oSettings.fileName = `納期回答履歴_${sDate}${sTime}.xlsx`;

			   // 设置 DELIVERY_DATE 和 CD_DATE 列格式
				oSettings.workbook.columns.forEach(function (oColumn) {
					if (oColumn.property === "INPUT_DATE" || oColumn.property === "CD_DATE") {
						oColumn.type = sap.ui.export.EdmType.Date; // 设置为日期类型
						oColumn.format = "yyyy/M/d"; // 设置日期格式，去掉时间
					}
				});

            }
        },
		
	});
});