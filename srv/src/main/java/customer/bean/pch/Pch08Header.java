package customer.bean.pch;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Pch08Header {
    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;

    @JSONField(name = "CUSTOMER")
    private String CUSTOMER;

    @JSONField(name = "MACHINE_TYPE")
    private String  MACHINE_TYPE;

    @JSONField(name = "QUANTITY")
    private Double QUANTITY;

    @JSONField(name = "VALIDATE_START")
    private LocalDate VALIDATE_START;

    @JSONField(name = "VALIDATE_END")
    private LocalDate VALIDATE_END;

}
