package customer.bean.bp;

import lombok.Data;

@Data
public class ResultsH {
    String BusinessPartner;

    private To_PhoneNumber to_PhoneNumber;
    private To_FaxNumber to_FaxNumber;

}
