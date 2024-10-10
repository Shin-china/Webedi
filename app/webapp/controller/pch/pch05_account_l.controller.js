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
        
            var oSettings = oEvent.getParameter("exportSettings");
        
            oSettings.workbook.columns = [
                // 第一组字段的标题
                // { label: "ヘッダデータ", property: "headerData", colspan: 11 }, // 合并的标题
                // 第一组字段
                { label: "*請求書ID", property: "invoiceId" },
                { label: "*会社コード(4)", property: "PO_BUKRS" },
                { label: "*取引(1)1=請求書、2=クレジットメモ", property: "transaction" },
                { label: "*請求元(10)", property: "SUPPLIER" },
                { label: "参照(16)", property: null },
                { label: "*伝票日付", property: "documentDate" },
                { label: "*転記日付", property: "postingDate" },
                { label: "*伝票タイプ", property: "documentType" },
                { label: "伝票ヘッダテキスト(25)", property: "headertext" },
                { label: "*通貨(5)", property: "CURRENCY" },
                { label: "*伝票通貨での請求書総額", property: "invoiceAmount" },
                { label: "税率定義の日付", property: "taxRateDate" },
            
                // 第二组字段的标题
                // { label: "G/L勘定明細", property: "glAccountDetails", colspan: 7 }, // 合并的标题
                // 第二组字段
                { label: "*会社コード(4)", property: "PO_BUKRS" },
                { label: "勘定(10)", property: "account" },
                { label: "明細テキスト", property: null },
                { label: "借方/貸方フラグ(1)s=借方 H=貸方", property: "debitCreditFlag" },
                { label: "金額(伝票通貨)", property: "amount" },
                { label: "税コード(2)", property: "taxCode" },
                { label: "伝票通貨での課税基準額", property: "taxBaseAmount" }
            ];
            // 生成包含数据的参数
                var oParams = this._buildParams(aSelectedData);
                console.log("Export Data:", oParams); // Debug 输出，检查数据结构
                // 确保数据结构正确，赋值给 Excel
                oSettings.workbook.data = oParams;
                // 设置文件名
                var oDate = new Date();
                var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
                var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
                oSettings.fileName = `消費税差額差処理_${sDate}${sTime}.xlsx`;
        },
            getData: function () {
                const oModel = this.getView().getModel();
                oModel.read("/PCH_T05_ACCOUNT_DETAIL_EXCEL", {
                    success: (oData) => {
                        // 假设 oData.results 是返回的记录数组
                        const oParams = this._buildParams(oData.results);
                        // 继续处理导出逻辑...
                    },
                    error: (oError) => {
                        console.error("Error fetching data", oError);
                    }
                });
            },
        
        _buildParams: function (aSelectedData) {
            let invoiceCounter = 1; // 从1开始初始化流水号计数器
            return aSelectedData.map(function (oData) {
                const poBukrs = oData.PO_BUKRS; // 从选中的数据中获取会社コード
                const diffTaxAmount10 = oData.DIFF_TAX_AMOUNT_10; // 直接从oData获取DIFF_TAX_AMOUNT_10
                const diffTaxAmount8 = oData.DIFF_TAX_AMOUNT_8; // 直接从oData获取DIFF_TAX_AMOUNT_8
                const supplier = oData.SUPPLIER; 
                const currency = oData.CURRENCY; 
                const taxCode = oData.TAX_CODE; 
                // 生成 invoiceId，每次生成一个唯一的 ID
                const invoiceId = invoiceCounter.toString(); // 生成流水号
                invoiceCounter++; // 累加
                // 设置 debitCreditFlag，根据差额的值确定
                const debitCreditFlag = (diffTaxAmount10 === null && diffTaxAmount8 === null) ? null :
                (diffTaxAmount10 >= 0 || diffTaxAmount8 >= 0) ? "S" : "H";
                // 根据 DIFF_TAX_AMOUNT_10 或 DIFF_TAX_AMOUNT_8 设置 transaction
                const transaction = (diffTaxAmount10 === null && diffTaxAmount8 === null) ? null :
                (diffTaxAmount10 >= 0 || diffTaxAmount8 >= 0) ? 1 : 2;
                // 计算 taxBaseAmount
                const taxBaseAmount = (diffTaxAmount10 !== null && diffTaxAmount10 !== undefined) ? 
                Math.floor(diffTaxAmount10 / 0.1) : // 除以10%并保留整数
                (diffTaxAmount8 !== null && diffTaxAmount8 !== undefined) ? 
                Math.floor(diffTaxAmount8 / 0.08) : // 除以8%并保留整数
                null; // 如果两个都为空，则为null
                // 假设 oData 是包含 INV_MONTH 的数据对象
                const invMonth = oData.INV_MONTH; // 例如 "202409"
                // 从 INV_MONTH 提取年份和月份
                const year = parseInt(invMonth.slice(0, 4), 10); // 提取年份，2024
                const month = parseInt(invMonth.slice(4, 6), 10); // 提取月份，09
                // 获取该月份的最后一天
                const lastDayOfMonth = new Date(year, month, 0); // month 为 09，因此获取 2024-09-30
                // 格式化日期为 yyyy-MM-dd 形式
                const lastDate = lastDayOfMonth.toISOString().split('T')[0]; // "2024-09-30"
                return {
                    invoiceId: invoiceId,     // *請求書ID
                    companyCode1: poBukrs,    // 从选中的数据获取会社コード
                    transaction: transaction, // 根据条件处理取引
                    supplier: supplier,       // 从选中的数据获取請求元，若为空则设置默认值
                    reference: null,          // 根据需求设置为 NULL
                    documentDate: lastDate,   // *伝票日付
                    postingDate: lastDate,    // *転記日付
                    documentType: "RE",       // *伝票タイプ
                    headertext: "TAX",        //伝票ヘッダテキスト(25)
                    currency: currency || "",       // *通貨(5)
                    invoiceAmount: diffTaxAmount10 || diffTaxAmount8 || null, // 根据 DIFF_TAX_AMOUNT_10 或 DIFF_TAX_AMOUNT_8 赋值，若都无值则为 null
                    taxRateDate: lastDate,    // 税率定義の日付
                    companyCode2: poBukrs,    // G/L勘定明細中的会社コード
                    account: "12600000",      // 勘定(10)
                    itemText: null,           // 明細テキスト
                    debitCreditFlag: debitCreditFlag, // 借方/貸方フラグ(1)
                    amount: diffTaxAmount10 || diffTaxAmount8 || null, // 金額(伝票通貨)
                    taxCode: taxCode || "",            // 税コード(2)
                    taxBaseAmount: taxBaseAmount       // 伝票通貨での課税基準額
                };
            });
          },
       },
        
    
    );
});