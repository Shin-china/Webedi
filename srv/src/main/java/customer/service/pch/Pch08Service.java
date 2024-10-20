package customer.service.pch;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import customer.bean.pch.Pch08;
import customer.bean.pch.Pch08DataList;
import customer.dao.pch.Pch01saveDao;
import customer.dao.pch.Pch08Dao;
import customer.dao.pch.PchD002;
import customer.dao.pch.PchD003;
import customer.tool.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;

@Component
public class Pch08Service {

    @Autowired
    private Pch01saveDao Pch01saveDao;
    @Autowired
    private PchD002 pchD002;
    @Autowired
    private PchD003 pchD003;

    @Autowired
    private Pch08Dao pch08Dao;

    HashMap<String, BigDecimal> hs = new HashMap<>();

    /*
     * pch08
     * 保存pch06，修改pch07表
     */
    public void detailsSave(Pch08DataList list) throws ParseException {
        // 如果没有错误
        // 修改po明细状态
        hs.keySet().forEach(value -> {
            String[] split = value.split(",");
            T02PoD byID = Pch01saveDao.getByID(split[0], Integer.parseInt(split[1]));
            byID.setStatus("2");
            pchD002.updateD002(byID);
            // 如果没有错误，
            // 删除对应的pch03数据
            // 没有减少数量才能删除
            pchD003.deleteD002ByPoDno(split[0], Integer.parseInt(split[1]));
            // 修改pch03对应的减少数量
            //pch08Dao.updatePch08();
        });

//        for (Pch06 iterable_element : list.getList()) {
//            // 没有减少数量才能追加
//            if (iterable_element.getRQ() == null) {
//                T03PoC t03PoC = T03PoC.create();
//                t03PoC.setPoNo(iterable_element.getPO_NO());
//                t03PoC.setDNo(iterable_element.getD_NO());
//                t03PoC.setSeq(iterable_element.getSEQ());
//                t03PoC.setDeliveryDate(DateTools.Iso86012Date(iterable_element.getDELIVERY_DATE()));
//                t03PoC.setQuantity(iterable_element.getQUANTITY());
//                t03PoC.setStatus("2");
//                t03PoC.setExtNumber(iterable_element.getExtNumber());
//
//                pchD003.insertD003(t03PoC);
//            } else {
//                // 有减少数量则修改
//                T03PoC byID = pchD003.getByID(iterable_element.getPO_NO(), iterable_element.getD_NO(),
//                        iterable_element.getSEQ());
//                byID.setQuantity(byID.getRelevantQuantity());
//                pchD003.updateD003(byID);
//            }
//
//        }
    }

    /*
     * pch08
     * check数据检查
     */
    public void check(Pch08DataList list) {

        // 通过key累加数量
//        for (Pch08 iterable_element : list.getList()) {
//            String poNo = iterable_element.getPO_NO();
//            int poDNo = iterable_element.getD_NO();
//            String key = poNo + "," + poDNo;
//
//            BigDecimal qty = BigDecimal.ZERO;
//            if (iterable_element.getRQ() != null) {
//                qty = iterable_element.getRQ();
//            } else {
//                qty = iterable_element.getQUANTITY();
//            }
//
//            BigDecimal bigDecimal = hs.get(key);
//            if (bigDecimal != null) {
//                hs.put(key, qty.add(bigDecimal));
//            } else {
//                hs.put(key, qty);
//            }
//
//        }
//        // 通过累加量进行判断
//        hs.keySet().forEach(value -> {
//            // 根据需要对 value 进行处理
//            String[] split = value.split(",");
//            T02PoD byID = Pch01saveDao.getByID(split[0], Integer.parseInt(split[1]));
//            BigDecimal poPurQty = byID.getPoPurQty();
//            if (poPurQty.compareTo(hs.get(value)) != 0) {
//                list.setErr(true);
//                list.setReTxt("PCH_06_ERROR_MSG1");
//            }
//        });

    }

}
