package customer.bean.mst;
import java.time.Instant;

import lombok.Data;

@Data
public class Results {
    private String Product;
    private Instant CreationDateTime;
    private String CreatedByUser;
    private Instant LastChangeDateTime;
    private String LastChangedByUser;
    private String ProductDescription; 

}
