package customer.bean.pch;

import java.util.ArrayList;

public class Pch10ListEmail {

    private ArrayList<Pch10EMAILSTATUS> list = new ArrayList<Pch10EMAILSTATUS>();
    // 返回成功失败msg
    private String reTxt = "";
    // 返回是否有错误:有值指有错误
    private Boolean err = false;

    /**
     * @return the list
     */
    public ArrayList<Pch10EMAILSTATUS> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ArrayList<Pch10EMAILSTATUS> list) {
        this.list = list;
    }

    public String getReTxt() {
        return reTxt;
    }

    public void setReTxt(String reTxt) {

        if (!this.reTxt.contains(reTxt))
            this.reTxt = this.reTxt + "||" + reTxt;
    }

    public Boolean getErr() {
        return err;
    }

    public void setErr(Boolean err) {
        this.err = err;
    }


}
