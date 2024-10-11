package customer.bean.bp;

import java.util.List;

import lombok.Data;

@Data
public class To_BusinessPartnerAddress {
    private String PostalCode;
    private String StreetName;
    private String CityName;

    private List<To_BusinessPartnerAddress> results;

    public void setResults(List<To_BusinessPartnerAddress> results) {
        this.results = results;
    }

    public List<To_BusinessPartnerAddress> getResults() {
        return results;
    }
}
