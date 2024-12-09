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
import java.util.*;

@Component
public class Pch08Service {


    @Autowired
    private Pch08Dao pch08Dao;

    @Autowired
    private PchD007 d007Dao;

    /*
     * pch08
     * 保存pch06，修改pch07表
     */
    public void detailsSave(Pch08DataList list) {

        list.getList().forEach(value -> {
            List<T07QuotationD> oldItems = getOldT07Data(value);
            List<T07QuotationD> newItems = extractT07Data(value);


            pch08Dao.updatePch08(oldItems, newItems);

        });
    }

    public List<T07QuotationD> getOldT07Data(Pch08 pch08) {
        return pch08Dao.getT07ByQuoNumberItem(pch08.getQUO_NUMBER(), pch08.getQUO_ITEM());
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


        for (Pch08DetailParam detailParam : detailParams) {
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
            for (T07QuotationD t07Item : t07Items) {
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
            for (T07QuotationD t07Item : t07Items) {
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

                String[] materialArr = materialKey.split("::", -1);
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

    public void updateDetail(String param) {
        JSONArray array = JSON.parseArray(param);

        for (Object o : array) {
            JSONObject object = (JSONObject) o;
            int personSize = object.getIntValue("PERSON_SIZE");
            int qtySize = object.getIntValue("QUANTITY_SIZE");
            updateT07New(object, personSize, qtySize);
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
            t07New.setUpTime(DateTools.getInstantNow());
            t07New.setUpBy(pch08Dao.getUserId());
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

    public List<Pch08Template> downloadTemplate(String param){
        String[] paramKeys = param.split(",");
        List<Pch08DetailParam> detailParams = new ArrayList<>();
        List<Pch08Template> resultList = new ArrayList<>();

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

        Pch08QueryResult queryResult = pch08Dao.queryTemplateData(detailParams);
        List<T06QuotationH> headerList = queryResult.getHeaderList();
        List<T07QuotationD> itemList = queryResult.getItemList();

        if (headerList == null || itemList == null) {
            return new ArrayList<>();
        }

        for(T07QuotationD item : itemList){
            headerList.stream().filter(e->e.getQuoNumber().equals(item.getQuoNumber()))
                          .findFirst()
                          .ifPresent(header->{
                              Pch08Template template = setTemplateData(header, item);
                              resultList.add(template);
                          });

        }

        return resultList;
    }

    private Pch08Template setTemplateData(T06QuotationH header, T07QuotationD item){
        Pch08Template template = new Pch08Template();
        template.setQUO_NUMBER(item.getQuoNumber());
        template.setQUO_ITEM(item.getQuoItem());
        template.setSALES_NUMBER(item.getSalesNumber());
        template.setSALES_D_NO(item.getSalesDNo());
        template.setNO(item.getNo());
        template.setQUANTITY(header.getQuantity());
        template.setREFRENCE_NO(item.getRefrenceNo());
        template.setCUSTOMER(header.getCustomer());
        template.setMACHINE_TYPE(header.getMachineType());
        template.setVALIDATE_START(header.getValidateStart());
        template.setVALIDATE_END(header.getValidateEnd());
        template.setITEM(header.getItem());
        template.setTIME(header.getTime());
        template.setLOCATION(header.getLocation());
        template.setMATERIAL_NUMBER(item.getMaterialNumber());
        template.setCUST_MATERIAL(item.getCustMaterial());
        template.setMANUFACT_MATERIAL(item.getManufactMaterial());
        template.setAttachment(item.getAttachment());
        template.setMaterial(item.getMaterial());
        template.setMAKER(item.getMaker());
        template.setUWEB_USER(item.getUwebUser());
        template.setBP_NUMBER(item.getBpNumber());
        template.setPERSON_NO1(item.getPersonNo1());
        template.setYLP(item.getYlp());
        template.setMANUL(item.getManul());
        template.setMANUFACT_CODE(item.getManufactCode());
        template.setCUSTOMER_MMODEL(item.getCustomerMmodel());
        template.setMID_QF(item.getMidQf());
        template.setSMALL_QF(item.getSmallQf());
        template.setOTHER_QF(item.getOtherQf());
        template.setCURRENCY(item.getCurrency());
        template.setQTY(item.getQty());
        template.setPRICE(item.getPrice());
        template.setPRICE_CONTROL(item.getPriceControl());
        template.setLEAD_TIME(item.getLeadTime());
        template.setMOQ(item.getMoq());
        template.setUNIT(item.getUnit());
        template.setSPQ(item.getSpq());
        template.setKBXT(item.getKbxt());
        template.setPRODUCT_WEIGHT(item.getProductWeight());
        template.setORIGINAL_COU(item.getOriginalCou());
        template.setEOL(item.getEol());
        template.setISBOI(item.getIsboi());
        template.setIncoterms(item.getIncoterms());
        template.setIncoterms_Text(item.getIncotermsText());
        template.setMEMO1(item.getMemo1());
        template.setMEMO2(item.getMemo2());
        template.setMEMO3(item.getMemo3());
        template.setSL(item.getSl());
        template.setTZ(item.getTz());
        template.setRMATERIAL(item.getRmaterial());
        template.setRMATERIAL_CURRENCY(item.getRmaterialCurrency());
        template.setRMATERIAL_PRICE(item.getRmaterialPrice());
        template.setRMATERIAL_LT(item.getRmaterialLt());
        template.setRMATERIAL_MOQ(item.getRmaterialMoq());
        template.setRMATERIAL_KBXT(item.getRmaterialKbxt());
        template.setUMC_COMMENT_1(item.getUmcComment1());
        template.setUMC_COMMENT_2(item.getUmcComment2());
        return template;
    }

    public Pch08UploadResult upload(String param, boolean isTestRun){
        Pch08Template uploadData;
        Pch08UploadResult checkResult = new Pch08UploadResult();
        try{
            uploadData = JSONObject.parseObject(param, Pch08Template.class);
        }catch (Exception e){
            checkResult.setSTATUS("E");
            checkResult.setMESSAGE("データエラー");
            return checkResult;
        }

        if (uploadData == null){
            checkResult.setSTATUS("E");
            checkResult.setMESSAGE("データエラー");
            return checkResult;
        }

        checkResult = uploadCheck(uploadData);

        if (checkResult == null){
            return null;
        }

        if (checkResult.getSTATUS().equals("E")){
            return checkResult;
        }

        if (isTestRun){
            return checkResult;
        }

        //update db
        String id = pch08Dao.getT07QuotationId(checkResult.getQUO_NUMBER(), checkResult.getQUO_ITEM(),
                checkResult.getSALES_NUMBER(), checkResult.getSALES_D_NO());

        T07QuotationD t07 = d007Dao.getByT07Id(id);
        T07QuotationD t07New = T07QuotationD.create();
        BeanUtils.copyProperties(t07, t07New);

        t07.setDelFlag("Y");
        t07.setUpTime(DateTools.getInstantNow());
        t07.setUpBy(pch08Dao.getUserId());
        d007Dao.update(t07);

        t07New.setId(null);
        t07New.setUpTime(DateTools.getInstantNow());
        t07New.setUpBy(pch08Dao.getUserId());
        t07New.setStatus("3");
        t07New.setDelFlag("N");
        t07New.setYlp(uploadData.getYLP());
        t07New.setManul(uploadData.getMANUL());
        t07New.setManufactCode(uploadData.getMANUFACT_CODE());
        t07New.setCustomerMmodel(uploadData.getCUSTOMER_MMODEL());
        t07New.setMidQf(uploadData.getMID_QF());
        t07New.setSmallQf(uploadData.getSMALL_QF());
        t07New.setOtherQf(uploadData.getOTHER_QF());
        t07New.setCurrency(uploadData.getCURRENCY());
        t07New.setQty(uploadData.getQTY());
        t07New.setPrice(uploadData.getPRICE());
        t07New.setPriceControl(uploadData.getPRICE_CONTROL());
        t07New.setLeadTime(uploadData.getLEAD_TIME());
        t07New.setMoq(uploadData.getMOQ());
        t07New.setUnit(uploadData.getUNIT());
        t07New.setSpq(uploadData.getSPQ());
        t07New.setKbxt(uploadData.getKBXT());
        t07New.setProductWeight(uploadData.getPRODUCT_WEIGHT());
        t07New.setOriginalCou(uploadData.getORIGINAL_COU());
        t07New.setEol(uploadData.getEOL());
        t07New.setIsboi(uploadData.getISBOI());
        t07New.setIncoterms(uploadData.getIncoterms());
        t07New.setIncotermsText(uploadData.getIncoterms_Text());
        t07New.setMemo1(uploadData.getMEMO1());
        t07New.setMemo2(uploadData.getMEMO2());
        t07New.setMemo3(uploadData.getMEMO3());
        t07New.setSl(uploadData.getSL());
        t07New.setTz(uploadData.getTZ());
        t07New.setRmaterial(uploadData.getRMATERIAL());
        t07New.setRmaterialCurrency(uploadData.getRMATERIAL_CURRENCY());
        t07New.setRmaterialPrice(uploadData.getRMATERIAL_PRICE());
        t07New.setRmaterialLt(uploadData.getRMATERIAL_LT());
        t07New.setRmaterialMoq(uploadData.getRMATERIAL_MOQ());
        t07New.setRmaterialKbxt(uploadData.getRMATERIAL_KBXT());
        t07New.setPersonNo1(uploadData.getPERSON_NO1());
        d007Dao.insert(t07New);

        checkResult.setSTATUS("S");
        checkResult.setMESSAGE("実行成功");

        return checkResult;
    }

    public Pch08UploadResult uploadCheck(Pch08Template uploadData){
        Pch08UploadResult uploadResult = new Pch08UploadResult();

        String quoNumber = uploadData.getQUO_NUMBER();
        Integer quoItem = uploadData.getQUO_ITEM();
        String salesNumber = uploadData.getSALES_NUMBER();
        String salesDNo = uploadData.getSALES_D_NO();

        uploadResult.setQUO_NUMBER(quoNumber);
        uploadResult.setQUO_ITEM(quoItem);
        uploadResult.setSALES_NUMBER(salesNumber);
        uploadResult.setSALES_D_NO(salesDNo);

        if (quoNumber == null || quoNumber.isEmpty()){
            uploadResult.setSTATUS("E");
            uploadResult.setMESSAGE("購買見積番号は空欄です");
            return uploadResult;
        }

        if (quoItem == null){
            uploadResult.setSTATUS("E");
            uploadResult.setMESSAGE("管理Noは空欄です");
            return uploadResult;
        }

        if (salesNumber == null || salesNumber.isEmpty()){
            uploadResult.setSTATUS("E");
            uploadResult.setMESSAGE("販売見積案件は空欄です");
            return uploadResult;
        }

        if (salesDNo == null || salesDNo.isEmpty()){
            uploadResult.setSTATUS("E");
            uploadResult.setMESSAGE("販売見積案件明細は空欄です");
            return uploadResult;
        }

//        if (customer == null || customer.isEmpty()){
//            uploadResult.setSTATUS("E");
//            uploadResult.setMESSAGE("客先は空欄です");
//            return uploadResult;
//        }

        BigDecimal price = uploadData.getPRICE();
        if (price == null){
            uploadResult.setSTATUS("E");
            uploadResult.setMESSAGE("单价は空欄です");
            return uploadResult;
        }

//        Integer personNo = uploadData.getPERSON_NO1();
//        if (personNo == null){
//            uploadResult.setSTATUS("E");
//            uploadResult.setMESSAGE("员数は空欄です");
//            return uploadResult;
//        }

        //判断采购报价单+销售订单的组合是否存在
        String id = pch08Dao.getT07QuotationId(quoNumber, quoItem, salesNumber, salesDNo);
        if (id == null || id.isEmpty()){
            uploadResult.setSTATUS("E");
            uploadResult.setMESSAGE("購買見積番号、管理No、販売見積案件、販売見積案件明細は存在しません");
            return uploadResult;
        }

        uploadResult.setSTATUS("S");
        uploadResult.setMESSAGE("チェック成功");
        return uploadResult;
    }

}
