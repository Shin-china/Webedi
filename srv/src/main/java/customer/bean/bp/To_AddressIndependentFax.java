package customer.bean.bp;

import java.util.List;

import lombok.Data;

@Data
public class To_AddressIndependentFax {

    private String InternationalFaxNumber;

    private List<To_AddressIndependentFax> results;

    public void setResults(List<To_AddressIndependentFax> results) {
        this.results = results;
    }

    public List<To_AddressIndependentFax> getResults() {
        return results;
    }

}
