package customer.service.pch;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T08Upload;
import customer.bean.pch.Pch06;
import customer.bean.pch.Pch06DataList;
import customer.dao.pch.Pch01saveDao;
import customer.dao.pch.PchD002;
import customer.dao.pch.PchD003;
import customer.dao.pch.PchD008Dao;
import customer.tool.DateTools;
import customer.tool.UWebConstants;

@Component
public class Pch06Service {

    @Autowired
    private Pch01saveDao Pch01saveDao;
    @Autowired
    private PchD002 pchD002;
    @Autowired
    private PchD003 pchD003;
    HashMap<String, BigDecimal> hs = new HashMap<>();
    @Autowired
    private PchD008Dao pchD008;

    /*
     * pch06
     * 保存pch03，修改pch02表
     */
    public void detailsSave(Pch06DataList list) throws ParseException {
        // 如果没有错误
        for (Pch06 iterable_element : list.getList()) {
            // 没有减少数量才能追加
            if (iterable_element.getRQ() == null) {
                // 判断是否需要追加履历表
                if (this.isT08Up(iterable_element)) {
                    T08Upload t08Upload = getT08Up(iterable_element);
                    pchD008.insert(t08Upload);
                }
            }
        }

        // 修改po明细状态
        hs.keySet().forEach(value -> {
            String[] split = value.split(",");
            T02PoD byID = Pch01saveDao.getByID(split[0], Integer.parseInt(split[1]));
            byID.setStatus(UWebConstants.PCH03_STATUS_2);
            pchD002.updateD002(byID);
            // 如果没有错误，
            // 删除对应的pch03数据
            // 没有减少数量才能删除
            pchD003.deleteD002ByPoDno(split[0], Integer.parseInt(split[1]));
            // 修改pch03对应的减少数量

        });

        for (Pch06 iterable_element : list.getList()) {
            // 没有减少数量才能追加
            if (iterable_element.getRQ() == null) {
                T03PoC t03PoC = getT03PoC(iterable_element);
                pchD003.insertD003(t03PoC);

            } else {
                // 有减少数量则修改
                T03PoC byID = pchD003.getByID(iterable_element.getPO_NO(), iterable_element.getD_NO(),
                        iterable_element.getSEQ());
                // iterable_element.setQUANTITY(iterable_element.getRQ());
                // iterable_element.setDELIVERY_DATE(DateTools.date2String(byID.getDeliveryDate()));

                byID.setQuantity(byID.getRelevantQuantity());
                pchD003.updateD003(byID);
            }

        }
    }

    private T08Upload getT08Up(Pch06 i) throws ParseException {
        T02PoD byID = pchD002.getByID(i.getPO_NO(), i.getD_NO());
        T08Upload t08Upload = T08Upload.create();
        t08Upload.setPoNo(i.getPO_NO());
        t08Upload.setDNo(i.getD_NO());
        t08Upload.setPoNoDno(i.getPO_NO() + i.getD_NO());
        t08Upload.setMatId(i.getMAT_ID());
        t08Upload.setMatName(byID.getPoDTxz01());
        t08Upload.setQuantity(byID.getPoPurQty());
        t08Upload.setPlantId(byID.getPlantId());
        t08Upload.setLocationId(byID.getStorageLoc());
        t08Upload.setInputDate(DateTools.stringToDate(i.getDELIVERY_DATE().substring(0, 10)));
        t08Upload.setInputQty(i.getQUANTITY());
        t08Upload.setExtNumber(i.getExtNumber());

        t08Upload.setType(UWebConstants.PCH03_STATUS_1);

        return t08Upload;
    }

    private T03PoC getT03PoC(Pch06 iterable_element) throws ParseException {
        T03PoC t03PoC = T03PoC.create();
        t03PoC.setPoNo(iterable_element.getPO_NO());
        t03PoC.setDNo(iterable_element.getD_NO());
        t03PoC.setSeq(iterable_element.getSEQ());
        t03PoC.setDeliveryDate(DateTools.stringToDate(iterable_element.getDELIVERY_DATE().substring(0, 10)));
        t03PoC.setQuantity(iterable_element.getQUANTITY());
        t03PoC.setStatus(UWebConstants.PCH03_STATUS_1);
        t03PoC.setExtNumber(iterable_element.getExtNumber());
        return t03PoC;
    }

    /**
     * 判断是否修改，返回true为修改
     * 
     * @param iterable_element
     * @return
     * @throws ParseException
     */
    private boolean isT08Up(Pch06 iterable_element) throws ParseException {
        T03PoC byID = pchD003.getByID(iterable_element.getPO_NO(), iterable_element.getD_NO(),
                iterable_element.getSEQ());
        if (byID == null) {
            return true;
        }
        if (!byID.getDeliveryDate()
                .equals(DateTools.stringToDate(iterable_element.getDELIVERY_DATE().substring(0, 10)))) {
            return true;
        }
        if (!(byID.getQuantity().compareTo(iterable_element.getQUANTITY()) == 0)) {
            return true;
        }
        if (!(byID.getExtNumber() == null ? "" : byID.getExtNumber())
                .equals(iterable_element.getExtNumber() == null ? "" : iterable_element.getExtNumber())) {
            return true;
        }
        return false;
    }

    /*
     * pch06
     * check数量状态
     */
    public void check(Pch06DataList list) throws ParseException {
        hs = new HashMap<>();
        // 通过key累加数量
        for (Pch06 iterable_element : list.getList()) {
            String poNo = iterable_element.getPO_NO();
            int poDNo = iterable_element.getD_NO();
            String key = poNo + "," + poDNo;

            BigDecimal qty = BigDecimal.ZERO;
            if (iterable_element.getRQ() != null) {
                qty = iterable_element.getRQ();
            } else {
                qty = iterable_element.getQUANTITY();
            }

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

        // 是否是今天之前，如果有以纳入数则无视
        for (Pch06 iterable_element : list.getList()) {
            // 没有减少数量才能check
            if (iterable_element.getRQ() == null) {
                LocalDate stringToDate = DateTools.stringToDate(iterable_element.getDELIVERY_DATE().substring(0, 10));
                if (DateTools.isBeforeToday(stringToDate)) {
                    list.setErr(true);
                    list.setReTxt("PCH_06_ERROR_MSG3");
                }
            }
        }
    }

}
