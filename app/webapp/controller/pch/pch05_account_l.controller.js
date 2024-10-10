sap.ui.define([
    "umc/app/Controller/BaseController",
    "sap/ui/model/Filter",
    "umc/app/model/formatter",
    "sap/ui/export/Spreadsheet"
], function (Controller, Filter, formatter,Spreadsheet) {
    "use strict";   
    return Controller.extend("umc.app.controller.pch.pch05_account_l", {
        formatter : formatter,
        onInit: function () {
            this._LocalData = this.getOwnerComponent().getModel("local");
            this._oDataModel = this.getOwnerComponent().getModel();
            this._ResourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
            this._oDataModel.attachBatchRequestCompleted(function (oEvent) {
                this.setBusy(false);
                var errors = this._LocalData.getProperty("/errors");
                if (errors) {
                    // 处理错误
                }
                this._LocalData.setProperty("/errors", "");
            }.bind(this));
        },
        setBusy: function (busy) {
            this._LocalData.setProperty("/busy", busy);
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
            // 获取选中的数据
            var aSelectedData = aSelectedIndices.map(function (index) {
                return oTable.getContextByIndex(index).getObject();
            });
            // 检查 TAX_CODE 是否相同
            var taxCodes = aSelectedData.map(function (oData) {
                return oData.TAX_CODE;
            });
            // 使用 Set 来判断是否有不同的税コード
            var uniqueTaxCodes = new Set(taxCodes);
            if (uniqueTaxCodes.size > 1) {
                // 如果 TAX_CODE 不同，抛出错误
                sap.m.MessageToast.show("同じ税コードを選択してください"); 
                oEvent.preventDefault(); // 取消导出操作
                return; // 终止导出流程
            }

             // 生成递增的 invoiceId
                aSelectedData.forEach(function (data, index) {
                    data.invoiceId1 = (index + 1).toString(); // 根据顺序生成递增的 ID
                });
        
            var oSettings = oEvent.getParameter("exportSettings");
        
            oSettings.workbook.columns = [
                // 第一组字段的标题
                // { label: "ヘッダデータ", property: "headerData", colspan: 11 }, // 合并的标题
                // 第一组字段
                { label: "*請求書ID", property: "INVOICEID" },
                { label: "*会社コード(4)", property: "PO_BUKRS" },
                { label: "*取引(1)1=請求書、2=クレジットメモ", property: "TRANSACTION" },
                { label: "*請求元(10)", property: "SUPPLIER" },
                { label: "参照(16)", property: "REFERENCE" },
                { label: "*伝票日付", property: "documentDate" },
                { label: "*転記日付", property: "postingDate" },
                { label: "*伝票タイプ", property: "DOCUMENTTYPE" },
                { label: "伝票ヘッダテキスト(25)", property: "HEADERTEXT" },
                { label: "*通貨(5)", property: "CURRENCY" },
                { label: "*伝票通貨での請求書総額", property: "DIFF_TAX_AMOUNT" },
                { label: "税率定義の日付", property: "taxRateDate" },
            
                // 第二组字段的标题
                // { label: "G/L勘定明細", property: "glAccountDetails", colspan: 7 }, // 合并的标题
                // 第二组字段
                { label: "*会社コード(4)", property: "PO_BUKRS" },
                { label: "勘定(10)", property: "ACCOUNT" },
                { label: "明細テキスト", property: "DETAILTEXT" },
                { label: "借方/貸方フラグ(1)s=借方 H=貸方", property: "SHKZG_FLAG" },
                { label: "金額(伝票通貨)", property: "DIFF_TAX_AMOUNT" },
                { label: "税コード(2)", property: "TAX_CODE" },
                { label: "伝票通貨での課税基準額", property: "TAX_BASE_AMOUNT" }
            ];
                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
                oSettings.fileName = `消費税差額差処理_${sDate}${sTime}.xlsx`;
        },
   
       },
        
    );
});