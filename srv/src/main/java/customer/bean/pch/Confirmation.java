package customer.bean.pch;

import lombok.Data;

@Data
public class Confirmation {

    private String Purchaseorder;
    private String Purchaseorderitem;
    private String Sequentialnmbrofsuplrconf;
    private String Deliverydate;
    private String Confirmedquantity;
    private String Mrprelevantquantity;
    private String Supplierconfirmationextnumber;

}
