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

import org.springframework.beans.BeanUtils;
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
            //T06QuotationH t06 = extractT06Data(value);
            List<T07QuotationD> oldItems = getOldT07Data(value);
            List<T07QuotationD> newItems = extractT07Data(value);


            pch08Dao.updatePch08(oldItems, newItems);

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

        t06.setStatus("3");
        t06.setUpTime(DateTools.getInstantNow());
        t06.setUpBy(pch08Dao.getUserId());

        return t06;
    }

    public List<T07QuotationD> getOldT07Data(Pch08 pch08) {
        return pch08Dao.getT07ByQuoNumber(pch08.getQUO_NUMBER());
    }

    public List<T07QuotationD> extractT07Data(Pch08 pch08) {
        List<T07QuotationD> items = getOldT07Data(pch08);
        if (items == null) {
            return null;
        }

        List<T07QuotationD> newItems = new ArrayList<>();

        items.forEach(value -> {
            T07QuotationD t07New = T07QuotationD.create();
            BeanUtils.copyProperties(value, t07New);

            t07New.setId(null); //Create New data
            t07New.setStatus("3");
            t07New.setUpTime(DateTools.getInstantNow());
            t07New.setUpBy(pch08Dao.getUserId());
            t07New.setRefrenceNo(pch08.getREFRENCE_NO());
            t07New.setMaterialNumber(pch08.getMATERIAL_NUMBER());
            t07New.setCustMaterial(pch08.getCUST_MATERIAL());
            t07New.setManufactMaterial(pch08.getMANUFACT_MATERIAL());
            t07New.setAttachment(pch08.getAttachment());
            t07New.setMaterial(pch08.getMaterial());
            t07New.setMaker(pch08.getMAKER());
            t07New.setUwebUser(pch08.getUWEB_USER());

            if (pch08.getBP_NUMBER() !=null) {
                t07New.setBpNumber(Integer.parseInt(pch08.getBP_NUMBER()));
            }

            t07New.setPersonNo1(pch08.getPERSON_NO1());
            t07New.setPersonNo2(pch08.getPERSON_NO2());
            t07New.setPersonNo3(pch08.getPERSON_NO3());
            t07New.setPersonNo4(pch08.getPERSON_NO4());
            t07New.setPersonNo5(pch08.getPERSON_NO5());

            t07New.setYlp(pch08.getYLP());
            t07New.setManul(pch08.getYLP());
            t07New.setManufactCode(pch08.getMANUFACT_CODE());
            t07New.setCustomerMmodel(pch08.getCUSTOMER_MMODEL());
            t07New.setMidQf(pch08.getMID_QF());
            t07New.setSmallQf(pch08.getSMALL_QF());
            t07New.setOtherQf(pch08.getOTHER_QF());
            t07New.setCurrency(pch08.getCURRENCY());
            t07New.setPriceControl(pch08.getPRICE_CONTROL());
            t07New.setLeadTime(pch08.getLEAD_TIME());
            t07New.setMoq(pch08.getMOQ());
            t07New.setUnit(pch08.getUNIT());
            t07New.setSpq(pch08.getSPQ());
            t07New.setKbxt(pch08.getKBXT());
            t07New.setProductWeight(pch08.getPRODUCT_WEIGHT());
            t07New.setOriginalCou(pch08.getORIGINAL_COU());
            t07New.setEol(pch08.getEOL());
            t07New.setIsboi(pch08.getISBOI());
            t07New.setIncoterms(pch08.getIncoterms());
            t07New.setIncotermsText(pch08.getIncoterms_Text());
            t07New.setMemo1(pch08.getMEMO1());
            t07New.setMemo2(pch08.getMEMO2());
            t07New.setMemo3(pch08.getMEMO3());
            t07New.setSl(pch08.getSL());
            t07New.setTz(pch08.getTZ());
            t07New.setRmaterial(pch08.getRMATERIAL());
            t07New.setRmaterialCurrency(pch08.getRMATERIAL_CURRENCY());

            if (pch08.getRMATERIAL_PRICE() != null) {
                t07New.setRmaterialPrice(BigDecimal.valueOf(pch08.getRMATERIAL_PRICE()));
            }

            t07New.setRmaterialLt(pch08.getRMATERIAL_LT());
            t07New.setRmaterialMoq(pch08.getRMATERIAL_MOQ());
            t07New.setRmaterialKbxt(pch08.getRMATERIAL_KBXT());
            t07New.setUmcComment1(pch08.getUMC_COMMENT_1());
            t07New.setUmcComment2(pch08.getUMC_COMMENT_2());

            newItems.add(t07New);
        });

        return newItems;
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
                T07QuotationD t07New = d007Dao.getByT07Id(t07Id);

                if(t07 != null) {
                    t07.setDelFlag("N");
                    d007Dao.update(t07);
                }

                if (t07New != null) {
                    t07New.setQty(qty == null ? BigDecimal.ZERO : new BigDecimal(qty));
                    t07New.setPrice(price == null ? BigDecimal.ZERO : new BigDecimal(price));
                    t07New.setId(null);
                    t07New.setStatus("3");
                    d007Dao.insert(t07New);
                }
            }
        }
    }

}
