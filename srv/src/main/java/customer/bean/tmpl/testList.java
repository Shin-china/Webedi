package customer.bean.tmpl;

import java.util.ArrayList;

import customer.bean.pch.Pch06;
import lombok.Data;

@Data
public class testList {
    private ArrayList<test> list = new ArrayList<test>();
}
