package customer.bean.sys;

import java.time.LocalDate;
import java.util.ArrayList;
import lombok.*;

@Data
public class Sys001User {
    private String userId;
    private String userType;
    private String userName;
    private LocalDate validDateTo;
    private LocalDate validDateFrom;

    private ArrayList<String> roles;

    private ArrayList<String> plants;
}
