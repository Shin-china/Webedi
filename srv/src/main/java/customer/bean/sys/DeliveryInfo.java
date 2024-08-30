package customer.bean.sys;

import com.alibaba.fastjson.annotation.JSONField;

public class DeliveryInfo {
    @JSONField(name = "PONO")
    private String poNo;

    @JSONField(name = "DNO")
    private String dNo;

    @JSONField(name = "SEQ")
    private String seq;

    @JSONField(name = "DELIVERYDATE")
    private String deliveryDate;

    @JSONField(name = "QUANTITY")
    private String quantity;

    @JSONField(name = "DELFLAG")
    private String delFlag;

    // Getters and Setters
    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public String getDNo() {
        return dNo;
    }

    public void setDNo(String dNo) {
        this.dNo = dNo;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
