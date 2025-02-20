package customer.bean.bp;

import java.util.List;

import lombok.Data;

@Data
public class To_FaxNumber {

    private String FaxNumber;

    private List<To_FaxNumber> results;

    public void setResults(List<To_FaxNumber> results) {
        this.results = results;
    }

    public List<To_FaxNumber> getResults() {
        return results;
    }

}
