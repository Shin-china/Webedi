package customer.service.pch;

import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;

import customer.bean.pch.*;
import customer.comm.tool.DateTools;
import customer.dao.pch.*;

import org.apache.log4j.helpers.LogLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Component
public class Pch08Service {


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

        list.getList().forEach(value -> {
            //T06QuotationH t06 = extractT06Data(value);
            List<T07QuotationD> oldItems = getOldT07Data(value);
            List<T07QuotationD> newItems = extractT07Data(value);


            pch08Dao.updatePch08(oldItems, newItems);

        });
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

            if (pch08.getBP_NUMBER() != null) {
                t07New.setBpNumber(Integer.parseInt(pch08.getBP_NUMBER()));
            }

            t07New.setYlp(pch08.getYLP());
            t07New.setManul(pch08.getMANUL());
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

    private Pch08Keys extractDetailKeys(T07QuotationD t07Item) {
        String materialNumber = t07Item.getMaterialNumber() != null ? t07Item.getMaterialNumber() : "";
        String custMaterial = t07Item.getCustMaterial() != null ? t07Item.getCustMaterial() : "";
        String manufactMaterial = t07Item.getManufactMaterial() != null ? t07Item.getManufactMaterial() : "";
        String sapMaterial = t07Item.getSapMatId() != null ? t07Item.getSapMatId() : "";
        String devMaterial = t07Item.getDevelopMat() != null ? t07Item.getDevelopMat() : "";

        String machineKey = sapMaterial + "::" + devMaterial;
        String materialKey = materialNumber + "::" + custMaterial + "::" + manufactMaterial;

        return new Pch08Keys(machineKey, materialKey);
    }

    public List<Map<String, Object>> getDetailData(String param) {
        List<Pch08DetailParam> detailParams = new ArrayList<>();

        String[] paramKeys = param.split(",");

        for (String quoKey : paramKeys) {
            int index = quoKey.lastIndexOf("-");
            if (index == -1) {
                continue;
            }
            String quoNumber = quoKey.substring(0, index);
            Integer quoItem = Integer.parseInt(quoKey.substring(index + 1));
            detailParams.add(new Pch08DetailParam(quoNumber, quoItem));
        }

        if (detailParams.isEmpty()) {
            return new ArrayList<>();
        }


        List<T07QuotationD> t07Items;
        Map<String, List<PchQuoItem>> quantityMap;
        Map<String, List<Pch08Person>> personMap;
        Map<String, List<String>> keyMap;
        Map<String, Object> resultMap;
        List<Map<String, Object>> resultList = new ArrayList<>();


        for (int i = 0; i < detailParams.size(); i++) {
            Pch08DetailParam detailParam = detailParams.get(i);
            String quoNumber = detailParam.getQUO_NUMBER();
            Integer quoItem = detailParam.getQUO_ITEM();

            if (quoNumber == null || quoItem == null) {
                continue;
            }

            //获取报价单明细信息
            t07Items = pch08Dao.getT07ByQuoNumberItem(quoNumber, quoItem);
            if (t07Items == null || t07Items.isEmpty()) {
                continue;
            }

            quantityMap = new LinkedHashMap<>();
            personMap = new LinkedHashMap<>();
            keyMap = new HashMap<>();
            resultMap = new HashMap<>();

            //循环所有明细
            for (int j = 0; j < t07Items.size(); j++) {
                T07QuotationD t07Item = t07Items.get(j);
                Pch08Keys keys = extractDetailKeys(t07Item);
                String machineKey = keys.getMachine();
                String materialKey = keys.getMaterial();

                //先把key分组：物料,机种
                if (keyMap.containsKey(materialKey)) {
                    List<String> keyList = keyMap.get(materialKey);

                    if (!keyList.contains(machineKey)) {
                        keyList.add(materialKey);
                    }
                } else {
                    List<String> keyList = new ArrayList<>();
                    keyList.add(machineKey);
                    keyMap.put(materialKey, keyList);
                }

            }

            //循环分组
            for (int j = 0; j < t07Items.size(); j++) {
                T07QuotationD t07Item = t07Items.get(j);
                Pch08Keys keys = extractDetailKeys(t07Item);
                String materialKey = keys.getMaterial();

                //分组
                List<String> materialKeys = keyMap.get(materialKey);

                //case 1. 相同物料存在多个几种，那么数量合并，员数扩展
                if (materialKeys.size() > 1) {
                    if (quantityMap.containsKey(materialKey)) {
                        List<PchQuoItem> quantityList = quantityMap.get(materialKey);
                        PchQuoItem item = quantityList != null ? quantityList.get(0) : new PchQuoItem();
                        if (item.getQty() != null) {
                            item.setQty(item.getQty().add(t07Item.getQty()));
                        }
                    } else {
                        List<PchQuoItem> quantityList = new ArrayList<>();
                        PchQuoItem item = new PchQuoItem();
                        item.setQty(t07Item.getQty());
                        item.setQuoNumber(t07Item.getQuoNumber());
                        item.setQuoItem(t07Item.getQuoItem());
                        item.setPrice(t07Item.getPrice());
                        item.setT07Id("");
                        quantityList.add(item);
                        quantityMap.put(materialKey, quantityList);
                    }

                    Pch08Person person = new Pch08Person();
                    person.setPersonNo(t07Item.getPersonNo1());
                    person.setId(t07Item.getId());

                    if (personMap.containsKey(materialKey)) {
                        List<Pch08Person> personList = personMap.get(materialKey);
                        personList.add(person);
                    } else {
                        List<Pch08Person> personList = new ArrayList<>();
                        personList.add(person);
                        personMap.put(materialKey, personList);
                    }
                } else {
                    //case 2. 相同物料只有一种，那么数量扩展，员数直接赋值
                    PchQuoItem item = new PchQuoItem();
                    item.setQty(t07Item.getQty());
                    item.setQuoNumber(t07Item.getQuoNumber());
                    item.setQuoItem(t07Item.getQuoItem());
                    item.setPrice(t07Item.getPrice());
                    item.setT07Id(t07Item.getId());

                    if (quantityMap.containsKey(materialKey)) {
                        List<PchQuoItem> quantityList = quantityMap.get(materialKey);
                        quantityList.add(item);
                    } else {
                        List<PchQuoItem> quantityList = new ArrayList<>();
                        quantityList.add(item);
                        quantityMap.put(materialKey, quantityList);
                    }

                    if (personMap.containsKey(materialKey)) {
                        List<Pch08Person> personList = personMap.get(materialKey);
                        Pch08Person person = !personList.isEmpty() ? personList.get(0) : new Pch08Person();
                        person.setPersonNo(t07Item.getPersonNo1());
                    } else {
                        List<Pch08Person> personList = new ArrayList<>();
                        Pch08Person person = new Pch08Person();
                        person.setPersonNo(t07Item.getPersonNo1());
                        person.setQuoNumber(t07Item.getQuoNumber());
                        person.setQuoItem(t07Item.getQuoItem());
                        person.setId("");
                        personList.add(person);
                        personMap.put(materialKey, personList);
                    }
                }
            }

            //生成动态结果
            for (String materialKey : keyMap.keySet()) {
                resultMap.put("QUO_NO", detailParam.getQUO_NUMBER());
                resultMap.put("QUO_ITEM", detailParam.getQUO_ITEM());
                resultMap.put("MAT", materialKey);
                List<PchQuoItem> quantityList = quantityMap.get(materialKey);
                List<Pch08Person> personList = personMap.get(materialKey);

                int quantitySize = quantityList != null ? quantityList.size() : 0;
                if (quantityList != null) {
                    for (int j = 0; j < quantityList.size(); j++) {
                        PchQuoItem item = quantityList.get(j);
                        resultMap.put("QTY_" + (j + 1), item.getQty());
                        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                        resultMap.put("PRICE_" + (j + 1), price);
                        resultMap.put("QTY_KEY_" + (j + 1), item.getT07Id());
                    }
                }

                resultMap.put("QUANTITY_SIZE", quantitySize);

                int personSize = personList != null ? personList.size() : 0;
                if (personList != null) {
                    for (int j = 0; j < personList.size(); j++) {
                        Pch08Person person = personList.get(j);
                        resultMap.put("PERSON_" + (j + 1), person.getPersonNo());
                        resultMap.put("PERSON_KEY_" + (j + 1), person.getId());
                    }
                }

                resultMap.put("PERSON_SIZE", personSize);

                String[] materialArr = materialKey.split("::");
                if (materialArr.length == 3) {
                    resultMap.put("MATERIAL_NUMBER", materialArr[0]);
                    resultMap.put("CUST_MATERIAL", materialArr[1]);
                    resultMap.put("MANUFACT_MATERIAL", materialArr[2]);
                }

                resultList.add(resultMap);

            }


        }

        return resultList;

    }

//    public List<LinkedHashMap<String, Object>> getDetailData(String param) {
//
//        String[] arr = param.split(",");
//        List<LinkedHashMap<String, Object>> reList = new ArrayList<>();
//        List<PchQuoH> headList = new ArrayList<>();
//
//        for (String quoNum : arr) {
//
//            List<T07QuotationD> t07List = d007Dao.getList(quoNum);
//            // 根据物料号分组 每个物料一行
//            Map<String, List<T07QuotationD>> tempMap = t07List.stream()
//                    .filter(t07 -> !StringTool.isEmpty(t07.getMaterial()))
//                    .collect(Collectors.groupingBy(T07QuotationD::getMaterial));
//
//            Set<String> keySet = tempMap.keySet();
//            for (String mat : keySet) {
//                PchQuoH head = new PchQuoH();
//                List<PchQuoItem> itemList = new ArrayList<>();
//                head.setQuoNo(quoNum);
//                head.setMaterial(mat);
//                List<T07QuotationD> list = tempMap.get(mat);
//                for (T07QuotationD t07 : list) {
//                    PchQuoItem item = new PchQuoItem();
//                    item.setPrice(t07.getPrice());
//                    item.setQty(t07.getQty());
//                    item.setT07Id(t07.getId());
//                    itemList.add(item);
//                }
//                head.setList(itemList);
//                headList.add(head);
//            }
//
//
//        }
//        for (PchQuoH h : headList) {
//            LinkedHashMap<String, Object> m = new LinkedHashMap<>();
//            m.put("QUO_NO", h.getQuoNo());
//            m.put("MAT", h.getMaterial());
//            Integer i = 1;
//            List<PchQuoItem> list = h.getList();
//            for (int j = 0; j < list.size(); j++) {
//                m.put("QTY_" + i, list.get(j).getQty());
//                m.put("PRICE_" + i, list.get(j).getPrice());
//                m.put("KEY_" + i, list.get(j).getT07Id());
//
//                if (j == list.size() - 1) {
//                    m.put("MAX", i);
//                }
//                i++;
//            }
//            reList.add(m);
//        }
//
//        return reList;
//
//    }

    public void updateDetail(String param) {
        JSONArray array = JSON.parseArray(param);

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            // size - 3 去除QuoNo,Mat,MAX 剩下的qty price key 三个为一组
            //int count = (object.size() - 3) / 3;
            int personSize = object.getIntValue("PERSON_SIZE");
            int qtySize = object.getIntValue("QUANTITY_SIZE");
            updateT07New(object, personSize, qtySize);
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

                if (t07 != null) {
                    t07.setDelFlag("Y");
                    t07.setUpTime(DateTools.getInstantNow());
                    t07.setUpBy(pch08Dao.getUserId());
                    d007Dao.update(t07);
                }

                if (t07New != null) {
                    t07New.setQty(qty == null ? BigDecimal.ZERO : new BigDecimal(qty));
                    t07New.setPrice(price == null ? BigDecimal.ZERO : new BigDecimal(price));
                    t07New.setId(null);
                    t07New.setStatus("3");
                    t07New.setDelFlag("N");
                    d007Dao.insert(t07New);
                }
            }
        }
    }

    private void updateT07DetailData(String id, int personNo, BigDecimal price){
        T07QuotationD t07 = d007Dao.getByT07Id(id);

        if (t07 == null){
            LogLog.warn("未找到T07 id为: " + id + "的记录");
            return;
        }

        T07QuotationD t07New = T07QuotationD.create();
        BeanUtils.copyProperties(t07, t07New);

            t07.setDelFlag("Y");
            t07.setUpTime(DateTools.getInstantNow());
            t07.setUpBy(pch08Dao.getUserId());
            d007Dao.update(t07);

            t07New.setPersonNo1(personNo);
            t07New.setPrice(price);
            t07New.setId(null);
            t07New.setStatus("3");
            t07New.setDelFlag("N");
            d007Dao.insert(t07New);
    }

    private void updateCase1(JSONObject o, int size) {
        //Case 1: 员数扩展，所以员数的id即为数据id，按员数id更新，此种情况下所有物料单价一样
        BigDecimal price = BigDecimal.ZERO;
        for (int i = 1; i <= size; i++) {
            String id = o.getString("PERSON_KEY_" + i);
            int personNo = o.getInteger("PERSON_" + i) != null? o.getInteger("PERSON_" + i) : 0;

            // 取第一个不为空的价格
            if (Objects.equals(price, BigDecimal.ZERO)) {
                price = o.getBigDecimal("PRICE_" + i) != null ? o.getBigDecimal("PRICE_" + i) : BigDecimal.ZERO;
            }

            updateT07DetailData(id, personNo, price);
        }
    }

    private void updateCase2(JSONObject o, int size) {
        //Case 2: 数量扩展，所以数量的id即为数据id，按数量id更新，此种情况下所有物料员数一样
        int personNo = 0;
        for (int i = 1; i <= size; i++) {
            String id = o.getString("QTY_KEY_" + i);

            // 取第一个不为空的员数
            if (personNo == 0){
                personNo = o.getInteger("PERSON_" + i) != null ? o.getInteger("PERSON_" + i) : 0;
            }

            BigDecimal price = o.getBigDecimal("PRICE_" + i) != null? o.getBigDecimal("PRICE_" + i) : BigDecimal.ZERO;

            updateT07DetailData(id, personNo, price);
        }
    }

    public void updateT07New(JSONObject o, int personSize, int qtySize) {
        //Case 1: 员数扩展，数量合并，先更新员数-按key更新，数量-因为合并按采购报价单+行号更新
        if (personSize > qtySize){
            updateCase1(o, personSize);
        } else {//Case 2: 数量扩展，所以数量的id即为数据id，按数量id更新，此种情况下所有物料员数一样
            updateCase2(o, qtySize);
        }
    }

}
