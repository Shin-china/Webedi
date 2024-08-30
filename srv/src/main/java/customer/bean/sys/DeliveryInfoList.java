package customer.bean.sys;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class DeliveryInfoList {
   
 private List<DeliveryInfo> items = new ArrayList<DeliveryInfo>();

public List<DeliveryInfo> getItems() {
    return items;
}

public void setItems(List<DeliveryInfo> items) {
    this.items = items;
}

}
