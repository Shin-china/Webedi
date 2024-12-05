package customer.bean.pch;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Pch10L {

    @JSONField(name = "ID")
    private String ID;

    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;

    @JSONField(name = "QUO_VERSION")
    private String QUO_VERSION;

    @JSONField(name = "SALES_NUMBER")
    private String SALES_NUMBER;

    @JSONField(name = "CUSTOMER")
    private String CUSTOMER;

    @JSONField(name = "MACHINE_TYPE")
    private String MACHINE_TYPE;

    @JSONField(name = "Item")
    private String Item;

    @JSONField(name = "QUANTITY")
    private String QUANTITY;

    @JSONField(name = "TIME")
    private String TIME;

    @JSONField(name = "LOCATION")
    private String LOCATION;

    @JSONField(name = "VALIDATE_START")
    private LocalDate VALIDATE_START;

    @JSONField(name = "VALIDATE_END")
    private LocalDate VALIDATE_END;

    @JSONField(name = "PLANT_ID")
    private String PLANT_ID;

}
