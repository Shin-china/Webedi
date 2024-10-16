package customer.bean.bp;

import lombok.Data;

@Data
public class Results {
    private String Supplier;
    private String OrganizationBPName1;
    private String OrganizationBPName2;
    private String OrganizationBPName3;
    private String OrganizationBPName4;
    private To_AddressIndependentFax to_AddressIndependentFax;
    private To_BusinessPartnerAddress to_BusinessPartnerAddress;
    private To_BusinessPartnerTax to_BusinessPartnerTax;
}