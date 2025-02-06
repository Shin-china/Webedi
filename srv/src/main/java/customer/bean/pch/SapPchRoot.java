package customer.bean.pch;

import java.util.ArrayList;
import java.util.List;


public class SapPchRoot {
    private List<Item> Items =new ArrayList<Item>();

    public List<Item> getItems() {
        return Items;
    }

    public void setItems(List<Item> items) {
        this.Items = items;
    }
}
