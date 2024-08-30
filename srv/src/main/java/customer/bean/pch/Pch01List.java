package customer.bean.pch;

import java.util.ArrayList;

// import io.vavr.collection.Array;

public class Pch01List {
    private ArrayList<Pch01> list = new ArrayList<Pch01>();
    //返回成功失败件数
    private String reTxt;
    //返回是否有错误:有值指有错误
    private boolean err;
     

    /**
     * @return the list
     */
    public ArrayList<Pch01> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ArrayList<Pch01> list) {
        this.list = list;
    }

    public String getReTxt() {
        return reTxt;
    }

    public void setReTxt(String reTxt) {
        this.reTxt = reTxt;
    }

    public Boolean getErr() {
        return err;
    }

    public void setErr(Boolean err) {
        this.err = err;
    }


}
