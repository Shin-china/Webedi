sap.ui.define([
	"umc/app/Controller/BaseController",
	"sap/ui/model/Filter",
	"umc/app/model/formatter",
	"sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
	"use strict";	

	return Controller.extend("umc.app.controller.pch.pch01_pocf_d", {
		formatter : formatter,

		onInit: function () {
		// 设置自己的 OData模型为默认模型
		this._setDefaultDataModel("TableService");
		//  设置版本号
		this._setOnInitNo("PCH01", ".20240812.01");
		},

		onRebind: function (oEvent) {
		// this._onListRebindDarft(oEvent);
		this._onListRebindDarft(oEvent, true);
		},

		onExport: function () {
			
		},
				
		onBeforeExport: function (oEvent) {
			var oTable = this.getView().byId("detailTable");

			// 获取表格中的所有行数据
			var aAllItems = oTable.getBinding("rows").getContexts();

			if (aAllItems.length === 0) {
				sap.m.MessageToast.show(this._ResourceBundle.getText("noData")); // 提示表中无数据
				oEvent.preventDefault(); // 取消导出操作
				return;
			}

			// 获取所有数据
			var aAllData = aAllItems.map(function (oContext) {
				return oContext.getObject();
			});

			var oSettings = oEvent.getParameter("exportSettings");

			// 更新导出列设置
			oSettings.workbook.columns = [
				{ label: "発注番号", property: "PO_NO" },
				{ label: "明細番号", property: "D_NO" },
				{ label: "品目コード", property: "MAT_ID" },
				{ label: "品目テキスト", property: "PO_D_TXZ01" },
				{ label: "発注数量", property: "PO_PUR_QTY" },
				{ label: "単位", property: "PO_PUR_UNIT" },
				{ label: "仕入先品目コード", property: "SUPPLIER_MAT"},
				{label: "*納品日", property: "" },
				{ label: "*納品数量", property:"" },
				{ label: "参照" , property:"" }
			];

			// 将自定义头部添加到导出设置的 context 中
			oSettings.workbook.context = {
				sheetName: "Sheet1",
				// headers: aCustomHeader // 插入自定义的表头信息
			};
			
			// 设置文件名
			var oDate = new Date();
			var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
			var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
			oSettings.fileName = `仕入先コード_納期回答_${sDate}${sTime}.xlsx`;
		},

	});
});