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
public class Pch04 {
    @ExcelProperty(index = 0, value = "発注明細NO")
    private String NO_DETAILS;
    @ExcelProperty(index = 1, value = "品目番号")
    private String MAT_ID;
    @ExcelProperty(index = 2, value = "品目名称")
    private String MAT_DESC;  
    @ExcelProperty(index = 3, value = "入荷日")
    private String GR_DATE;  
    @ExcelProperty(index = 4, value = "仕入単位数")
    private String QUANTITY;   
    @ExcelProperty(index = 5, value = "基準通貨単価")
    private String UNIT_PRICE_IN_YEN;  
    @ExcelProperty(index = 6, value = "基準通貨金額税抜")
    private String BASE_AMOUNT_EXCLUDING_TAX; 
    @ExcelProperty(index = 7, value = "消費税率")
    private String TAX_RATE;  
    @ExcelProperty(index = 8, value = "備考")
    private String PO_TRACK_NO;  
    @ExcelProperty(index = 9, value = "支払日")
    private String INV_BASE_DATE;  
    @ExcelProperty(index = 10, value = "仕入金額計(8%対象)")
    private String TOTAL_PRICE_AMOUNT_8;  
    @ExcelProperty(index = 11, value = "消費税計(8%対象)")
    private String CONSUMPTION_TAX_8;  
    @ExcelProperty(index = 12, value = "税込支払金額(8%対象)")
    private String TOTAL_PAYMENT_AMOUNT_8_END; 
    @ExcelProperty(index = 13, value = "仕入金額計(10%対象)")
    private String TOTAL_PRICE_AMOUNT_10; 
    @ExcelProperty(index = 14, value = "消費税計(10%対象)")
    private String CONSUMPTION_TAX_10;  
    @ExcelProperty(index = 15, value = "税込支払金額(10%対象)")
    private String TOTAL_PAYMENT_AMOUNT_10_END;  
    @ExcelProperty(index = 16, value = "対象外金額")
    private String NON_APPLICABLE_AMOUNT;  
    @ExcelProperty(index = 17, value = "総合計")
    private String TOTAL_PAYMENT_AMOUNT_FINAL;  
    @ExcelProperty(index = 18, value = "INV_MONTH_FORMATTED")
    private String INV_MONTH_FORMATTED;  
    @ExcelProperty(index = 19, value = "SUPPLIER_DESCRIPTION")
    private String SUPPLIER_DESCRIPTION; 
    @ExcelProperty(index = 20, value = "登録番号")
    private String LOG_NO;  
    @ExcelProperty(index = 21, value = "会社")
    private String Company_Name; 
    @ExcelProperty(index = 22, value = "発行日：")
    private String CURRENT_DAY;  


}
