package customer.service.pch;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import customer.bean.pch.Pch06;
import customer.bean.pch.Pch06DataList;
import customer.dao.pch.Pch01saveDao;
import customer.dao.pch.PchD002;
import customer.dao.pch.PchD003;
import customer.tool.DateTools;

@Component
public class Pch06Service {

    @Autowired
    private Pch01saveDao Pch01saveDao;
    @Autowired
    private PchD002 pchD002;
    @Autowired
    private PchD003 pchD003;

    /*
     * pch06
     * 保存pch03，修改pch02表
     */
    public void detailsSave(Pch06DataList list) throws ParseException {

        for (Pch06 iterable_element : list.getList()) {
            T03PoC t03PoC = T03PoC.create();
            t03PoC.setPoNo(iterable_element.getPO_NO());
            t03PoC.setDNo(iterable_element.getD_NO());
            t03PoC.setSeq(iterable_element.getSEQ());
            t03PoC.setDeliveryDate(DateTools.Iso86012Date(iterable_element.getDELIVERY_DATE()));
            t03PoC.setQuantity(iterable_element.getQUANTITY());
            t03PoC.setStatus("2");
            t03PoC.setExtNumber(iterable_element.getExtNumber());

            pchD003.insertD003(t03PoC);
        }
    }

    /*
     * pch06
     * check数量状态
     */
    public void check(Pch06DataList list) {
        HashMap<String, BigDecimal> hs = new HashMap<>();

        // 通过key累加数量
        for (Pch06 iterable_element : list.getList()) {
            String poNo = iterable_element.getPO_NO();
            int poDNo = iterable_element.getD_NO();
            String key = poNo + "," + poDNo;

            BigDecimal qty = iterable_element.getQUANTITY();

            BigDecimal bigDecimal = hs.get(key);
            if (bigDecimal != null) {
                hs.put(key, qty.add(bigDecimal));
            } else {
                hs.put(key, qty);
            }

        }
        // 通过累加量进行判断
        hs.keySet().forEach(value -> {
            // 根据需要对 value 进行处理
            String[] split = value.split(",");
            T02PoD byID = Pch01saveDao.getByID(split[0], Integer.parseInt(split[1]));
            BigDecimal poPurQty = byID.getPoPurQty();
            if (poPurQty.compareTo(hs.get(value)) != 0) {
                list.setErr(true);
                list.setReTxt("PCH_06_ERROR_MSG1");
            }
        });

        // 如果没有错误
        // 修改po明细状态
        if (!list.getErr()) {
            hs.keySet().forEach(value -> {
                String[] split = value.split(",");
                T02PoD byID = Pch01saveDao.getByID(split[0], Integer.parseInt(split[1]));
                byID.setStatus("2");
                pchD002.updateD002(byID);
                // 如果没有错误，
                // 删除对应的pch03数据
                pchD003.deleteD002ByPoDno(split[0], Integer.parseInt(split[1]));
            });

        }

    }

}
