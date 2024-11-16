package customer.bean.pch;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class SupList {

    private String Supplierinvoice;
    private Integer Fiscalyear;
    private String Invoicingparty;
    private LocalDate Documentdate;
    private LocalDate Postingdate;
    private BigDecimal Exchangerate;
    private LocalDate Duecalculationbasedate;
    private String Invoicegrossamount;
    private String Createdbyuser;
    private String Lastchangedbyuser;
    private String Supplierinvoicetaxcounter;
    private String Taxcode;
    private BigDecimal Taxamount;
    private BigDecimal Totalamount;
    private BigDecimal Unitprice;
    private Integer Supplierinvoiceitem;
    private String Purchaseorder;
    private Integer Purchaseorderitem;
    private String Debitcreditcode;
    private String Purchaseorderitemmaterial;
    private String Documentcurrency;
    private BigDecimal Supplierinvoiceitemamount;
    private BigDecimal Quantityinpurchaseorderunit;
    private String Purchaseorderquantityunit;
    private String Costcenter;
    private String Glaccount;
    private String Purchaseorderitemtext;
    private String Requisitionername;
    private String Requirementtracking;
    private String Plant;
    private String Purchasinggroup;
    private String Companycode;
    private String Purchasinggroupname;
    private String Accountingdocument;

}