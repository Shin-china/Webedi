package customer.bean.pch;

import java.util.List;

public class PchQuoH {
    private String quoNo;
    private String material;
    List<PchQuoItem> list;
   

    public String getQuoNo() {
        return quoNo;
    }

    public void setQuoNo(String quoNo) {
        this.quoNo = quoNo;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<PchQuoItem> getList() {
        return list;
    }

    public void setList(List<PchQuoItem> list) {
        this.list = list;
    }

 
}
