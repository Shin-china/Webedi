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
public class test {
    @ExcelProperty(index = 0, value = "序号")
    private Integer id;
    @ExcelProperty(index = 1, value = "物料")
    private String mat;
    @ExcelProperty(index = 2, value = "数量")
    private Integer qty;
    @ExcelProperty(index = 3, value = "单价")
    private float unp;
}
