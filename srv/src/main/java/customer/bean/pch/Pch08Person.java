package customer.bean.pch;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Pch08Person {
    private String id;
    private BigDecimal personNo;
    private String quoNumber;
    private Integer quoItem;
}
