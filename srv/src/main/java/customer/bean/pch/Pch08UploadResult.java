package customer.bean.pch;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Pch08UploadResult {
    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;

    @JSONField(name = "QUO_ITEM")
    private Integer QUO_ITEM;

    @JSONField(name = "SALES_NUMBER")
    private String SALES_NUMBER;

    @JSONField(name = "SALES_D_NO")
    private String SALES_D_NO;

    @JSONField(name = "MESSAGE")
    private String MESSAGE;

    @JSONField(name = "STATUS")
    private String STATUS;
}
