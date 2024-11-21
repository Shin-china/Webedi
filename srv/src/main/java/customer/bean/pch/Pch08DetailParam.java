package customer.bean.pch;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pch08DetailParam {
    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;
    @JSONField(name = "QUO_ITEM")
    private Integer QUO_ITEM;
}
