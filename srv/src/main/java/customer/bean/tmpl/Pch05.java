package customer.bean.tmpl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@HeadRowHeight(30)
@ContentRowHeight(20)
public class Pch05 {
    @ExcelProperty(index = 0, value = "*請求書ID")
    private String INVOICEID;
    @ExcelProperty(index = 1, value = "*会社コード(4)")
    private String PO_BUKRS;
    @ExcelProperty(index = 2, value = "*取引(1)1=請求書、2=クレジットメモ")
    private String TRANSACTION;
    @ExcelProperty(index = 3, value = "*請求元(10)")
    private String SUPPLIER;
    @ExcelProperty(index = 4, value = "参照(16)")
    private String REFERENCE;
    @ExcelProperty(index = 5, value = "*伝票日付")
    private String LASTDATE;
    @ExcelProperty(index = 6, value = "*転記日付")
    private String LASTDATE1;
    @ExcelProperty(index = 7, value = "*伝票タイプ")
    private String DOCUMENTTYPE;
    @ExcelProperty(index = 8, value = "伝票ヘッダテキスト(25)")
    private String HEADERTEXT;
    @ExcelProperty(index = 9, value = "*通貨(5)")
    private String CURRENCY;
    @ExcelProperty(index = 10, value = "*伝票通貨での請求書総額")
    private String DIFF_TAX_AMOUNT;
    @ExcelProperty(index = 11, value = "税率定義の日付")
    private String LASTDATE2;
    @ExcelProperty(index = 12, value = "*会社コード(4)")
    private String PO_BUKRS1;
    @ExcelProperty(index = 13, value = "勘定(10)")
    private String ACCOUNT;
    @ExcelProperty(index = 14, value = "明細テキスト")
    private String DETAILTEXT;
    @ExcelProperty(index = 15, value = "借方/貸方フラグ(1)s=借方 H=貸方")
    private String SHKZG_FLAG;
    @ExcelProperty(index = 16, value = "金額(伝票通貨)")
    private String DIFF_TAX_AMOUNT1;
    @ExcelProperty(index = 17, value = "税コード(2)")
    private String TAX_CODE;
    @ExcelProperty(index = 18, value = "伝票通貨での課税基準額")
    private String TAX_BASE_AMOUNT;

}
