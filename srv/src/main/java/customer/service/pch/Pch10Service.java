package customer.service.pch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T07ComOpH;
import cds.gen.sys.T08ComOpD;
import ch.qos.logback.core.status.Status;
import customer.bean.pch.Pch10;
import customer.dao.pch.Pch10Dao;

@Component
public class Pch10Service {

    @Autowired
    private Pch10Dao Pch10Dao;

    public void setQuostatus(String sal_Num) {

        // 获取明细的status
        String Status = Pch10Dao.getStatus(sal_Num);
        String Hstatus = Pch10Dao.getHStatus(sal_Num);

        List<T07QuotationD> items = Pch10Dao.getItems(sal_Num);

        // for (T07QuotationD item : items) {

        // switch (item.getStatus()) {

        // case "1":
        // item.setStatus("2");
        // Pch10Dao.Setstatus(item);

        // case "2":
        // item.setStatus("2");
        // Pch10Dao.Setstatus(item);

        // case "3":
        // item.setStatus("4");
        // Pch10Dao.Setstatus(item);

        // case "4":
        // item.setStatus("4");
        // Pch10Dao.Setstatus(item);

        // default:

        // }

        // }

        // 获取全部条目
        List<T07QuotationD> items2 = Pch10Dao.getItems(sal_Num);
        String headerStatus = "5"; // 假设全部为"5"
        boolean allStatus2 = true; // 用于记录是否所有项状态都为"2"
        for (T07QuotationD item2 : items2) {

            if (item2.getStatus().equals("1")) {
                headerStatus = "1";
                break; // 优先级最高，直接退出循环
            }

            if (item2.getStatus().equals("2")) {
                // 保持 allStatus2 为 true
            } else {
                allStatus2 = false; // 一旦发现不是 "2"，设为 false
            }

            if (item2.getStatus().equals("3") && headerStatus.equals("5")) {
                headerStatus = "3";
            } else if (item2.getStatus().equals("4") && headerStatus.equals("5")) {
                headerStatus = "4";
            }
        }

        // 如果全部状态都为 "2"，并且当前 headerStatus 为 "1"，将 headerStatus 更新为 "2"
        if (allStatus2 && Hstatus.equals("1")) {
            headerStatus = "2";
        }

        // 输出头部状态
        System.out.println("Header Status: " + headerStatus);

    }
}
