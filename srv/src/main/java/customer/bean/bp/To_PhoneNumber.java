package customer.bean.bp;

import java.util.List;

import lombok.Data;

@Data
public class To_PhoneNumber {

    private String PhoneNumber;

    private List<To_PhoneNumber> results;

    public void setResults(List<To_PhoneNumber> results) {
        this.results = results;
    }

    public List<To_PhoneNumber> getResults() {
        return results;
    }

}
