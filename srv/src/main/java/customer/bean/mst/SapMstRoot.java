/**
  * Copyright 2024 bejson.com 
  */
package customer.bean.mst;

import java.util.ArrayList;
import java.util.List;

public class SapMstRoot {

    private Integer __count = 0;

    private List<Value> value = new ArrayList<Value>();

    public void setValue(List<Value> value) {
        this.value = value;
    }

    public List<Value> getValue() {
        return value;
    }

    /**
     * @return the __count
     */
    public Integer get__count() {
        return __count;
    }

    /**
     * @param __count the __count to set
     */
    public void set__count(Integer __count) {
        this.__count = __count;
    }

}