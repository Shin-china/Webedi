package customer.handlers.sys;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.excel.write.metadata.fill.FillConfig;

import customer.bean.sys.Ifm054Bean;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.dao.sys.DocNoDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.BaseMoveService;
import cds.gen.common.PchT06QuotationH;
import cds.gen.common.PchT07QuotationD;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T11IfManager;
import customer.bean.com.UmcConstants;
import customer.bean.pch.Pch07;

@Component
public class SendService {

    @Autowired
    PchD006 PchD006;
    @Autowired
    PchD007 PchD007;
    @Autowired
    DocNoDao docNoDao;
    @Autowired
    BaseMoveService base;
    @Autowired
    private IFSManageDao ifsManageDao;

    // 发送t06数据，传入json,返回ArrayList<T06QuotationH>
    public ArrayList<T06QuotationH> getJson(String json) {
        ArrayList<T06QuotationH> pch06List = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        // 直接从上下文中获取参数
        JSONArray jsonArray = JSONArray.parseArray(json);
        // 根据传入的po和po明细修改po明细状态
        // 获取要传入的字符串
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String quoNumber = jsonObject.getString("QUO_NUMBER");
            String salesNumber = jsonObject.getString("SALES_NUMBER");
            String quoVersion = jsonObject.getString("QUO_VERSION");
            String key = quoNumber + salesNumber + quoVersion;
            String salesDno = jsonObject.getString("SALES_D_NO");
            if (map.containsKey(key)) {
                String string = map.get(key);
                map.put(key, salesDno + "," + string);
            } else {
                map.put(key, salesDno);
            }

            T06QuotationH t06QuotationH = PchD006.get(quoNumber, salesNumber, quoVersion);

            if (t06QuotationH != null)
                pch06List.add(t06QuotationH);
        }
        // pch06List.forEach(pch06 -> {
        // ArrayList<T07QuotationD> list = new ArrayList<T07QuotationD>();
        // String quoNumber = pch06.getQuoNumber();
        // String salesNumber = pch06.getSalesNumber();
        // String quoVersion = pch06.getQuoVersion();
        // String key = quoNumber + salesNumber + quoVersion;
        // String salesDno = map.get(key);
        // String[] split = salesDno.split(",");
        // for (String string : split) {
        // T07QuotationD t07QuotationD = PchD007.get(quoNumber, salesNumber, quoVersion,
        // string);
        // list.add(t07QuotationD);
        // }
        // pch06.setToItemPo(list);

        // });
        return pch06List;

    }

    public String sendPost(ArrayList<T06QuotationH> pch06List) throws Exception {
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("pch06", pch06List);
        // 获取 Web Service 配置信息
        T11IfManager webServiceConfig = ifsManageDao.getByCode("IFM050");

        // 调用送信接口
        String postMove = base.postMove(webServiceConfig, retMap, null);

        // 使用fastjson将字符串转换为JSONObject

        String re = getJsonObject2(postMove);
        return re;
    }

    private String getJsonObject2(String postMove) {
        JSONObject jsonObject2 = JSONObject.parseObject(postMove);

        return (String) jsonObject2.get("value");
    }

    public void extracted(ArrayList<PchT06QuotationH> pch06List) {
        ArrayList<cds.gen.pch.T06QuotationH> pch06List2 = new ArrayList<>();
        System.out.println("开始插入");
        pch06List.forEach(pchT06QuotationH -> {

            // 获取購買見積番号
            pchT06QuotationH.setQuoNumber(pchT06QuotationH.getQuoNumber());

            // 插入头标，首先删除原key值数据
            T06QuotationH t06QuotationH = T06QuotationH.create();

            // 复制类属性
            BeanUtils.copyProperties(pchT06QuotationH, t06QuotationH);

            pch06List2.add(t06QuotationH);
            t06QuotationH.remove("TO_ITEMS");
            t06QuotationH.remove("TO_ITEM_PO");
            // 如果已经存在则更新，如果不存在则插入
            PchD006.modefind(t06QuotationH);


            // 插入明细
            List<PchT07QuotationD> toItems = pchT06QuotationH.getToItemPo();
            if (toItems != null) {
                toItems.forEach(pchT07QuotationD -> {

                    T07QuotationD t07QuotationD = T07QuotationD.create();

                    // // 复制类属性
                    BeanUtils.copyProperties(pchT07QuotationD, t07QuotationD);

                    // // 如果已经存在则更新，如果不存在则插入
                    T07QuotationD byID2 = PchD007.getId(t07QuotationD.getQuoNumber(),t07QuotationD.getSalesNumber(),t07QuotationD.getQuoVersion(),t07QuotationD.getQuoItem(),t07QuotationD.getSalesDNo());

                    if (byID2 != null) {

                        // PchD007.update(t07QuotationD);

                        Map<String, Object> data = new HashMap<>();
                        getT07DaoData(t07QuotationD, data);
                        Map<String, Object> keys = new HashMap<>();
                        keys.put("SALES_NUMBER",t07QuotationD.getSalesNumber());
                        keys.put("QUO_NUMBER",t07QuotationD.getQuoNumber());
                        keys.put("QUO_VERSION",t07QuotationD.getQuoVersion());
                        keys.put("SALES_D_NO",t07QuotationD.getSalesDNo());
                        keys.put("QUO_ITEM",t07QuotationD.getQuoItem());
                        PchD007.update(data, keys);

                    } else {
                        PchD007.insert(t07QuotationD);
                    }
                });
            }
        });
    }

    public void getT07DaoData(T07QuotationD t07QuotationD, Map<String, Object> data) {
        data.put("SALES_NUMBER", t07QuotationD.getSalesNumber());
        data.put("QUO_VERSION", t07QuotationD.getQuoVersion());
        data.put("SALES_D_NO", t07QuotationD.getSalesDNo());
        data.put("QUO_NUMBER", t07QuotationD.getQuoNumber());
        data.put("QUO_ITEM", t07QuotationD.getQuoItem());
        data.put("SAP_MAT_ID", t07QuotationD.getSapMatId());
        data.put("DEVELOP_MAT", t07QuotationD.getDevelopMat());
        data.put("NO", t07QuotationD.getNo());
        data.put("REFRENCE_NO", t07QuotationD.getRefrenceNo());
        data.put("MATERIAL_NUMBER", t07QuotationD.getMaterialNumber());
        data.put("INITIAL_OBJ", t07QuotationD.getInitialObj());
        data.put("QTY", t07QuotationD.getQty());
        data.put("CUST_MATERIAL", t07QuotationD.getCustMaterial());
        data.put("MANUFACT_MATERIAL", t07QuotationD.getManufactMaterial());
        data.put("SMI_CM_MM", t07QuotationD.getSmiCmMm());
        data.put("PLANT_ID", t07QuotationD.getPlantId());
        data.put("Attachment", t07QuotationD.getAttachment());
        data.put("Material", t07QuotationD.getMaterial());
        data.put("MAKER", t07QuotationD.getMaker());
        data.put("UWEB_USER", t07QuotationD.getUwebUser());
        data.put("PERSON_NO1", t07QuotationD.getPersonNo1());
        data.put("YLP", t07QuotationD.getYlp());
        data.put("MANUL", t07QuotationD.getManul());
        data.put("MANUFACT_CODE", t07QuotationD.getManufactCode());
        data.put("CUSTOMER_MMODEL", t07QuotationD.getCustomerMmodel());
        data.put("MID_QF", t07QuotationD.getMidQf());
        data.put("SMALL_QF", t07QuotationD.getSmallQf());
        data.put("OTHER_QF", t07QuotationD.getOtherQf());
        data.put("CURRENCY", t07QuotationD.getCurrency());
        data.put("PRICE", t07QuotationD.getPrice());
        data.put("PRICE_CONTROL", t07QuotationD.getPriceControl());
        data.put("LEAD_TIME", t07QuotationD.getLeadTime());
        data.put("MOQ", t07QuotationD.getMoq());

        data.put("UNIT", t07QuotationD.getUnit());
        data.put("SPQ", t07QuotationD.getSpq());
        data.put("KBXT", t07QuotationD.getKbxt());

        data.put("PRODUCT_WEIGHT", t07QuotationD.getProductWeight());
        data.put("ORIGINAL_COU", t07QuotationD.getOriginalCou());
        data.put("EOL", t07QuotationD.getEol());
        data.put("ISBOI", t07QuotationD.getIsboi());
        data.put("Incoterms", t07QuotationD.getIncoterms());
        data.put("Incoterms_Text", t07QuotationD.getIncotermsText());

        data.put("MEMO1", t07QuotationD.getMemo1());
        data.put("MEMO2", t07QuotationD.getMemo2());
        data.put("MEMO3", t07QuotationD.getMemo3());
        data.put("SL", t07QuotationD.getSl());
        data.put("TZ", t07QuotationD.getTz());
        data.put("RMATERIAL", t07QuotationD.getRmaterial());

        data.put("RMATERIAL_CURRENCY", t07QuotationD.getRmaterialCurrency());
        data.put("RMATERIAL_PRICE", t07QuotationD.getRmaterialPrice());
        data.put("RMATERIAL_LT", t07QuotationD.getRmaterialLt());

        data.put("RMATERIAL_MOQ", t07QuotationD.getRmaterialMoq());
        data.put("RMATERIAL_KBXT", t07QuotationD.getRmaterialKbxt());
        data.put("UMC_SELECTION", t07QuotationD.getUmcSelection());

        data.put("UMC_COMMENT_1", t07QuotationD.getUmcComment1());
        data.put("UMC_COMMENT_2", t07QuotationD.getUmcComment2());
        data.put("FINAL_CHOICE", t07QuotationD.getFinalChoice());
        data.put("SUPPLIER_MAT", t07QuotationD.getSupplierMat());
        data.put("STATUS", t07QuotationD.getStatus());
        data.put("CD_DATE", t07QuotationD.getCdDate());
        data.put("CD_DATE_TIME", t07QuotationD.getCdDateTime());

    }

    public void update(ArrayList<T06QuotationH> pch06List) {
        pch06List.forEach(pch06 -> {
            pch06.getToItemPo().forEach(toItemPo -> {
                toItemPo.setStatus(UmcConstants.T07_STATUS_05);
                PchD007.update(toItemPo);
            });
        });
    }

    public void update(String json) {
        // 直接从上下文中获取参数
        JSONArray jsonArray = JSONArray.parseArray(json);
        // 根据传入的po和po明细修改po明细状态
        // 获取要传入的字符串
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String quoNumber = jsonObject.getString("QUO_NUMBER");
            String salesNumber = jsonObject.getString("SALES_NUMBER");
            String quoVersion = jsonObject.getString("QUO_VERSION");
            T06QuotationH t06QuotationH = PchD006.getNot3(quoNumber, salesNumber, quoVersion);

            if (t06QuotationH.getToItems() != null) {
                // 如果所有明细状态为5，则更新头表状态为5
                if (t06QuotationH.getToItems().stream()
                        .allMatch(toItemPo -> toItemPo.getStatus().equals(UmcConstants.T07_STATUS_05))) {
                    t06QuotationH.setStatus(UmcConstants.T07_STATUS_05);
                    PchD006.update(t06QuotationH);
                }
            }

        }
    }
}
