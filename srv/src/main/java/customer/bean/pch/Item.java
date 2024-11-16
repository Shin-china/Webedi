package customer.bean.pch;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Item {

    private String Purchaseorder;
    private String Supplier;
    private String Companycode;
    private String Purchasingdocumentdeletioncode;
    private String Purchaseorderdate;
    private String Creationdate;
    private String Createdbyuser;
    private String Lastchangedatetime;
    private String Purchaseorderitem;
    private String Documentcurrency;
    private String Material;
    private String Plant;
    private String Purchaseorderitemtext;
    private String Orderquantity;
    private String Purchaseorderquantityunit;
    private String Netpricequantity;
    private String Netamount;
    private String Netpriceamount;
    private String Storagelocation;
    private String Storagelocationname;
    private String Textobjecttype;
    private String Plainlongtext;
    private String Schedulelinedeliverydate;
    private String SupplierMaterialNumber;
    private String Internationalarticlenumber;
    private String Requisitionername;
    private String Correspncinternalreference;

    private List<Confirmation> Confirmation = new ArrayList<Confirmation>();

}
