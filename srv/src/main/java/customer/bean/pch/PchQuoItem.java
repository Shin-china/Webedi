package customer.bean.pch;

import java.math.BigDecimal;

public class PchQuoItem {

    private BigDecimal qty;
    private BigDecimal price;
    private Integer personNo;
    private String quoNumber;
    private String quoItem;
    private String t07Id;

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getT07Id() {
        return t07Id;
    }

    public void setT07Id(String t07Id) {
        this.t07Id = t07Id;
    }

    public Integer getPersonNo() {
        return personNo;
    }

    public void setPersonNo(Integer personNo) {
        this.personNo = personNo;
    }

    public String getQuoNumber() {
        return quoNumber;
    }

    public void setQuoNumber(String quoNumber) {
        this.quoNumber = quoNumber;
    }

    public String getQuoItem() {
        return quoItem;
    }

    public void setQuoItem(String quoItem) {
        this.quoItem = quoItem;
    }
}
