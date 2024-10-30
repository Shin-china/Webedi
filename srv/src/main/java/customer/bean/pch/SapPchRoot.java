package customer.bean.pch;

import java.util.ArrayList;

import lombok.Data;

public class SapPchRoot {
    private ArrayList<Item> Items;

    public ArrayList<Item> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Item> items) {
        this.Items = items;
    }
}
