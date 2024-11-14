package customer.bean.tmpl;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Pch04List {
    private ArrayList<Pch04> list = new ArrayList<Pch04>();
    // 返回成功失败件数
    private String reTxt;
    // 返回是否有错误:有值指有错误
    private String err;

}
