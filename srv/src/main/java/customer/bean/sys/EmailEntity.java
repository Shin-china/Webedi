package customer.bean.sys;

import java.lang.reflect.Array;
import java.util.List;

import lombok.Data;

@Data
public class EmailEntity {
    private String TEMPLATE_ID;
    private String MAIL_TO;
    private List<InnerEmailEntity> MAIL_BODY;

}
