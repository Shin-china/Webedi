package customer.bean.pch;

import java.util.ArrayList;

import lombok.Data;

public class SapSupRoot {

    private ArrayList<SupList> Items;

    public ArrayList<SupList> getItems() {
        return Items;
    }

    public void setItems(ArrayList<SupList> items) {
        this.Items = items;
    }

}
