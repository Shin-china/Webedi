package customer.bean.sys;

import java.util.ArrayList;

import cds.gen.tableservice.PchT06QuotationH;
import lombok.Data;

@Data
public class Ifm054Bean {
    private ArrayList<PchT06QuotationH> pch06 = new ArrayList<PchT06QuotationH>();

}
