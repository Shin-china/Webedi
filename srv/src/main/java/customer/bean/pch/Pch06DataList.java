package customer.bean.pch;

import java.util.ArrayList;

public class Pch06DataList {
    private ArrayList<Pch06> list = new ArrayList<Pch06>();
    // 返回成功失败件数
    private String reTxt;
    // 返回是否有错误:有值指有错误
    private String err;

    /**
     * @return the list
     */
    public ArrayList<Pch06> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ArrayList<Pch06> list) {
        this.list = list;
    }

    public String getReTxt() {
        return reTxt;
    }

    public void setReTxt(String reTxt) {
        this.reTxt = reTxt;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}
