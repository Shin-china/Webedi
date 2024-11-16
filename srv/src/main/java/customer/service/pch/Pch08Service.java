package customer.service.pch;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;

import customer.bean.pch.Pch08;
import customer.bean.pch.Pch08DataList;
import customer.bean.pch.PchQuoH;
import customer.bean.pch.PchQuoItem;
import customer.comm.tool.DateTools;
import customer.dao.pch.*;
import customer.tool.StringTool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

import java.util.LinkedHashMap;

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

    @Autowired
    private PchD007 d007Dao;

    HashMap<String, BigDecimal> hs = new HashMap<>();

    /*
     * pch08
     * 保存pch06，修改pch07表
     */
    public void detailsSave(Pch08DataList list) throws ParseException {
        // 如果没有错误
        // 修改po明细状态
//        hs.keySet().forEach(value -> {
//            String[] split = value.split(",");
//            T02PoD byID = Pch01saveDao.getByID(split[0], Integer.parseInt(split[1]));
//            byID.setStatus("2");
//            pchD002.updateD002(byID);
//            // 如果没有错误，
//            // 删除对应的pch03数据
//            // 没有减少数量才能删除
//            pchD003.deleteD002ByPoDno(split[0], Integer.parseInt(split[1]));
//            // 修改pch03对应的减少数量
//            // pch08Dao.updatePch08();
//        });

        list.getList().forEach(value -> {
            T06QuotationH t06 = extractT06Data(value);
            List<T07QuotationD> items = extractT07Data(value);

            pch08Dao.updatePch08(t06, items);

        });


        // for (Pch06 iterable_element : list.getList()) {
        // // 没有减少数量才能追加
        // if (iterable_element.getRQ() == null) {
        // T03PoC t03PoC = T03PoC.create();
        // t03PoC.setPoNo(iterable_element.getPO_NO());
        // t03PoC.setDNo(iterable_element.getD_NO());
        // t03PoC.setSeq(iterable_element.getSEQ());
        // t03PoC.setDeliveryDate(DateTools.Iso86012Date(iterable_element.getDELIVERY_DATE()));
        // t03PoC.setQuantity(iterable_element.getQUANTITY());
        // t03PoC.setStatus("2");
        // t03PoC.setExtNumber(iterable_element.getExtNumber());
        //
        // pchD003.insertD003(t03PoC);
        // } else {
        // // 有减少数量则修改
        // T03PoC byID = pchD003.getByID(iterable_element.getPO_NO(),
        // iterable_element.getD_NO(),
        // iterable_element.getSEQ());
        // byID.setQuantity(byID.getRelevantQuantity());
        // pchD003.updateD003(byID);
        // }
        //
        // }
    }

    public T06QuotationH extractT06Data(Pch08 pch08) {
        T06QuotationH t06 = pch08Dao.getT06ByQuoNumber(pch08.getQUO_NUMBER());
        if (t06 == null) {
            return null;
        }

        t06.setStatus("2");
        t06.setUpTime(DateTools.getInstantNow());
        t06.setUpBy(pch08Dao.getUserId());

        return t06;
    }

    public List<T07QuotationD> extractT07Data(Pch08 pch08) {
        List<T07QuotationD> items = pch08Dao.getT07ByQuoNumber(pch08.getQUO_NUMBER());
        if (items == null) {
            return null;
        }

        items.forEach(value -> {
            value.setStatus("2");
            value.setUpTime(DateTools.getInstantNow());
            value.setUpBy(pch08Dao.getUserId());
            value.setRefrenceNo(pch08.getREFRENCE_NO());
            value.setMaterialNumber(pch08.getMATERIAL_NUMBER());
            value.setCustMaterial(pch08.getCUST_MATERIAL());
            value.setManufactMaterial(pch08.getMANUFACT_MATERIAL());
            value.setAttachment(pch08.getAttachment());
            value.setMaterial(pch08.getMaterial());
            value.setMaker(pch08.getMAKER());
            value.setUwebUser(pch08.getUWEB_USER());

            if (pch08.getBP_NUMBER() !=null) {
                value.setBpNumber(Integer.parseInt(pch08.getBP_NUMBER()));
            }

            value.setPersonNo1(pch08.getPERSON_NO1());
            value.setPersonNo2(pch08.getPERSON_NO2());
            value.setPersonNo3(pch08.getPERSON_NO3());
            value.setPersonNo4(pch08.getPERSON_NO4());
            value.setPersonNo5(pch08.getPERSON_NO5());

            value.setYlp(pch08.getYLP());
            value.setManul(pch08.getYLP());
            value.setManufactCode(pch08.getMANUFACT_CODE());
            value.setCustomerMmodel(pch08.getCUSTOMER_MMODEL());
            value.setMidQf(pch08.getMID_QF());
            value.setSmallQf(pch08.getSMALL_QF());
            value.setOtherQf(pch08.getOTHER_QF());
            value.setCurrency(pch08.getCURRENCY());
            value.setPriceControl(pch08.getPRICE_CONTROL());
            value.setLeadTime(pch08.getLEAD_TIME());
            value.setMoq(pch08.getMOQ());
            value.setUnit(pch08.getUNIT());
            value.setSpq(pch08.getSPQ());
            value.setKbxt(pch08.getKBXT());
            value.setProductWeight(pch08.getPRODUCT_WEIGHT());
            value.setOriginalCou(pch08.getORIGINAL_COU());
            value.setEol(pch08.getEOL());
            value.setIsboi(pch08.getISBOI());
            value.setIncoterms(pch08.getIncoterms());
            value.setIncotermsText(pch08.getIncoterms_Text());
            value.setMemo1(pch08.getMEMO1());
            value.setMemo2(pch08.getMEMO2());
            value.setMemo3(pch08.getMEMO3());
            value.setSl(pch08.getSL());
            value.setTz(pch08.getTZ());
            value.setRmaterial(pch08.getRMATERIAL());
            value.setRmaterialCurrency(pch08.getRMATERIAL_CURRENCY());

            if (pch08.getRMATERIAL_PRICE() != null) {
                value.setRmaterialPrice(BigDecimal.valueOf(pch08.getRMATERIAL_PRICE()));
            }

            value.setRmaterialLt(pch08.getRMATERIAL_LT());
            value.setRmaterialMoq(pch08.getRMATERIAL_MOQ());
            value.setRmaterialKbxt(pch08.getRMATERIAL_KBXT());
            value.setUmcComment1(pch08.getUMC_COMMENT_1());
            value.setUmcComment2(pch08.getUMC_COMMENT_2());

        });

        return items;
    }

    /*
     * pch08
     * check数据检查
     */
    public void check(Pch08DataList list) {

        // 通过key累加数量
        // for (Pch08 iterable_element : list.getList()) {
        // String poNo = iterable_element.getPO_NO();
        // int poDNo = iterable_element.getD_NO();
        // String key = poNo + "," + poDNo;
        //
        // BigDecimal qty = BigDecimal.ZERO;
        // if (iterable_element.getRQ() != null) {
        // qty = iterable_element.getRQ();
        // } else {
        // qty = iterable_element.getQUANTITY();
        // }
        //
        // BigDecimal bigDecimal = hs.get(key);
        // if (bigDecimal != null) {
        // hs.put(key, qty.add(bigDecimal));
        // } else {
        // hs.put(key, qty);
        // }
        //
        // }
        // // 通过累加量进行判断
        // hs.keySet().forEach(value -> {
        // // 根据需要对 value 进行处理
        // String[] split = value.split(",");
        // T02PoD byID = Pch01saveDao.getByID(split[0], Integer.parseInt(split[1]));
        // BigDecimal poPurQty = byID.getPoPurQty();
        // if (poPurQty.compareTo(hs.get(value)) != 0) {
        // list.setErr(true);
        // list.setReTxt("PCH_06_ERROR_MSG1");
        // }
        // });

    }

    public List<LinkedHashMap<String, Object>> getDetailData(String param) {

        String[] arr = param.split(",");
        List<LinkedHashMap<String, Object>> reList = new ArrayList<>();
        List<PchQuoH> headList = new ArrayList<>();
        for (String quoNum : arr) {

            List<T07QuotationD> t07List = d007Dao.getList(quoNum);
            // 根据物料号分组 每个物料一行
            Map<String, List<T07QuotationD>> tempMap = t07List.stream()
                    .filter(t07 -> !StringTool.isEmpty(t07.getMaterial()))
                    .collect(Collectors.groupingBy(T07QuotationD::getMaterial));

            Set<String> keySet = tempMap.keySet();
            for (String mat : keySet) {
                PchQuoH head = new PchQuoH();
                List<PchQuoItem> itemList = new ArrayList<>();
                head.setQuoNo(quoNum);
                head.setMaterial(mat);
                List<T07QuotationD> list = tempMap.get(mat);
                for (T07QuotationD t07 : list) {
                    PchQuoItem item = new PchQuoItem();
                    item.setPrice(t07.getPrice());
                    item.setQty(t07.getQty());
                    item.setT07Id(t07.getId());
                    itemList.add(item);
                }
                head.setList(itemList);
                headList.add(head);
            }

        }
        for (PchQuoH h : headList) {
            LinkedHashMap<String, Object> m = new LinkedHashMap<>();
            m.put("QUO_NO", h.getQuoNo());
            m.put("MAT", h.getMaterial());
            Integer i = 1;
            List<PchQuoItem> list = h.getList();
            for (int j = 0; j < list.size(); j++) {
                m.put("QTY_" + i, list.get(j).getQty());
                m.put("PRICE_" + i, list.get(j).getPrice());
                m.put("KEY_" + i, list.get(j).getT07Id());

                if (j == list.size() - 1) {
                    m.put("MAX", i);
                }
                i++;
            }
            // for (PchQuoItem item : h.getList()) {

            // i++;
            // }
            reList.add(m);
        }

        return reList;

    }

    public void updateDetail(String param) {
        JSONArray array = JSON.parseArray(param);

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            // size - 3 去除QuoNo,Mat,MAX 剩下的qty price key 三个为一组
            int count = (object.size() - 3) / 3;
            updateT07(object, count);
        }
    }

    public void updateT07(JSONObject o, int size) {
        for (int i = 1; i <= size; i++) {
            if (o.getString("KEY_" + i) != null) {
                String t07Id = o.getString("KEY_" + i);
                String qty = o.getString("QTY_" + i);
                String price = o.getString("PRICE_" + i);
                T07QuotationD t07 = d007Dao.getByT07Id(t07Id);
                if (t07 != null) {
                    t07.setQty(qty == null ? BigDecimal.ZERO : new BigDecimal(qty));
                    t07.setPrice(price == null ? BigDecimal.ZERO : new BigDecimal(price));
                    d007Dao.update(t07);
                }
            }
        }
    }

}
