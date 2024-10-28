package customer.bean.pch;

import java.util.ArrayList;
import java.util.List;

public class SapPchRoot {


    private List<Items> items = new ArrayList<Items>();

    public void setValue(List<Items> items) {
        this.items = items;
    }

    public List<Items> getItems() {
        return items;
    }

}
