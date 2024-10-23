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
    @ExcelProperty(index = 0, value = "seq")
    private Integer id;
    @ExcelProperty(index = 1, value = "mat")
    private String mat;
    @ExcelProperty(index = 2, value = "qty")
    private Integer qty;
    @ExcelProperty(index = 3, value = "unp")
    private float unp;
}
