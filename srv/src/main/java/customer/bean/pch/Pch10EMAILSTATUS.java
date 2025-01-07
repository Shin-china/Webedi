package customer.bean.pch;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;

public class Pch10EMAILSTATUS {

    
    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;

    public String getQUO_NUMBER() {
        return QUO_NUMBER;
    }

    public void setQUO_NUMBER(String qUO_NUMBER) {
        QUO_NUMBER = qUO_NUMBER;
    }
    
}
