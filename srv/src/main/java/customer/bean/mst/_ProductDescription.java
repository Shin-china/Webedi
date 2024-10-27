/**
  * Copyright 2024 bejson.com 
  */
package customer.bean.mst;

import java.util.List;

public class _ProductDescription {

    private String Product;
    private String Language;
    private String ProductDescription;
    private List<String> SAP__Messages;

    public void setProduct(String Product) {
        this.Product = Product;
    }

    public String getProduct() {
        return Product;
    }

    public void setLanguage(String Language) {
        this.Language = Language;
    }

    public String getLanguage() {
        return Language;
    }

    public void setProductDescription(String ProductDescription) {
        this.ProductDescription = ProductDescription;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setSAP__Messages(List<String> SAP__Messages) {
        this.SAP__Messages = SAP__Messages;
    }

    public List<String> getSAP__Messages() {
        return SAP__Messages;
    }

}