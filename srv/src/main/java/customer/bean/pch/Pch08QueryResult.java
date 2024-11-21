package customer.bean.pch;

import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pch08QueryResult {
    private List<T06QuotationH> headerList;
    private List<T07QuotationD> itemList;
}
