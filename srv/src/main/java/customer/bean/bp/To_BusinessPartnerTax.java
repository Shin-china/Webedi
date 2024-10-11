package customer.bean.bp;

import java.util.List;

import lombok.Data;

@Data
public class To_BusinessPartnerTax {

    private String BPTaxNumber;

    private List<To_BusinessPartnerTax> results;

    public void setResults(List<To_BusinessPartnerTax> results) {
        this.results = results;
    }

    public List<To_BusinessPartnerTax> getResults() {
        return results;
    }
}
