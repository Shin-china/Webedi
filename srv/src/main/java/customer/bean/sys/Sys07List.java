package customer.bean.sys;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Sys07List {
    private ArrayList<Sys07> list = new ArrayList<Sys07>();
    // 返回成功失败件数
    private String reTxt;
    // 返回是否有错误:有值指有错误
    private String err;

}
