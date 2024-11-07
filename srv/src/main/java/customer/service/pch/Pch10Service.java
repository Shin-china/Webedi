package customer.service.pch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.pch.T06QuotationH;
import ch.qos.logback.core.status.Status;
import customer.bean.pch.Pch10;
import customer.dao.pch.Pch10Dao;

@Component
public class Pch10Service {

    @Autowired
    private Pch10Dao Pch10Dao;

    public void setQuostatus(String quo_no) {

        String Status = Pch10Dao.getStatus(quo_no);

        T06QuotationH o = Pch10Dao.getById(quo_no);

        switch (Status) {

            case "1":
                o.setStatus("2");
                Pch10Dao.Setstatus(o);
                break;
            case "2":
                o.setStatus("2");
                Pch10Dao.Setstatus(o);
                break;
            case "3":
                o.setStatus("4");
                Pch10Dao.Setstatus(o);
                break;
            case "4":
                o.setStatus("4");
                Pch10Dao.Setstatus(o);
                break;

            default:

        }
    }
}
