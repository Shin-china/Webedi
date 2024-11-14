package customer.bean.tmpl;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Pch05List {
    private ArrayList<Pch05> list = new ArrayList<Pch05>();
    // 返回成功失败件数
    private String reTxt;
    // 返回是否有错误:有值指有错误
    private String err;

}
