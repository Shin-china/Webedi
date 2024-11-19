package customer.service.pch;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T07QuotationD;
import cds.gen.pch.T10EmailSendLog;
import cds.gen.pch.T10EmailSendLog;
import cds.gen.tableservice.PchT03PoItemPrint;
import customer.bean.com.UmcConstants;
import customer.bean.pch.Pch08DataList;
import customer.bean.pch.PchQuoH;
import customer.bean.pch.PchQuoItem;
import customer.comm.tool.StringTool;
import customer.dao.pch.Pch01saveDao;
import customer.dao.pch.Pch08Dao;
import customer.dao.pch.PchD002;
import customer.dao.pch.PchD003;
import customer.dao.pch.PchD004;
import customer.dao.pch.PchD007;
import customer.dao.pch.PchD010Dao;

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
public class PchService {
    @Autowired
    PchD002 pchD002;
    @Autowired
    PchD003 pchD003;
    @Autowired
    PchD004 pchD004;
    @Autowired
    PchD010Dao pchD010;

    // 根据传入的po和po明细修改po明细状态
    public void setPoStu(String po, String dNo) {
        T02PoD byID = pchD002.getByID(po, Integer.parseInt(dNo));
        byID.setStatus(UmcConstants.PCH02_STATUS_02);
        pchD002.updateD002(byID);
    }

    // 根据传入的SUPLI明细状态
    public void setSendflag(String supp) {

        Map<String, Object> data = new HashMap<>();
        data.put("SEND_FLAG", UmcConstants.PCH04_STATUS_02);
        Map<String, Object> keys = new HashMap<>();
        keys.put("SUPPLIER", supp);
        // 数据是否被修改修改标记

        pchD004.updateMap(data, keys);
    }

    // 根据传入的INV_NO明细状态
    public void setinvdateconfirm(String object) {

        Map<String, Object> data = new HashMap<>();
        data.put("SUPPLIER_DESCRIPTION", UmcConstants.PCH05_CONFIRM);
        Map<String, Object> keys = new HashMap<>();
        keys.put("SUPPLIER", object);
        // 数据是否被修改修改标记

        pchD004.updateMap(data, keys);
    }

    // 根据传入的INV_NO明细状态
    public void setinvdatecancel(String object) {

        Map<String, Object> data = new HashMap<>();
        data.put("SUPPLIER_DESCRIPTION", UmcConstants.PCH05_CANCEL);
        Map<String, Object> keys = new HashMap<>();
        keys.put("SUPPLIER", object);
        // 数据是否被修改修改标记

        pchD004.updateMap(data, keys);
    }

    public BigDecimal getSysD006(String string) {
        return BigDecimal.ONE;
    }

    public String getQrCode(PchT03PoItemPrint pchd03) {
        return pchd03.getPodno();
    }

    public void setT09LogData(JSONObject jsonObject) {
        String po = jsonObject.getString("po");
        String dNo = jsonObject.getString("dNo");
        String t = jsonObject.getString("t");
        T02PoD byID = pchD002.getByID(po, Integer.parseInt(dNo));
        T10EmailSendLog byID2 = pchD010.getByID(po, Integer.parseInt(dNo));
        // 如果t10为空则为新规，如果不为空则为 修改
        if (byID2 == null) {

            T10EmailSendLog t10 = T10EmailSendLog.create();
            t10.setPoNo(po);
            t10.setDNo(Integer.parseInt(dNo));
            t10.setQuantity(byID.getPoPurQty());
            t10.setInputDate(byID.getPoDDate());
            t10.setDelFlag(byID.getDelFlag());
            t10.setPoType(byID.getPoType());
            // 如果t不为空，则进行写Y操作
            if (!StringTool.isEmpty(t)) {
                t10.setType("Y");
            }

            pchD010.insert(t10);
        } else {
            T10EmailSendLog t10 = T10EmailSendLog.create();

            t10.setQuantity(byID.getPoPurQty());
            t10.setInputDate(byID.getPoDDate());
            t10.setDelFlag(byID.getDelFlag());
            t10.setPoType(byID.getPoType());
            // 如果t不为空，则进行写Y操作
            if (!StringTool.isEmpty(t)) {
                t10.setType("Y");
            }

            pchD010.update(t10);
        }

    }

    public void updatePch03(String parms) {
        // 直接从上下文中获取参数
        JSONArray jsonArray = JSONArray.parseArray(parms);
        // 根据传入的po和po明细修改po明细状态
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            T03PoC byID = pchD003.getByID(jsonObject.getString("PONO"), Integer.parseInt(jsonObject.getString("DNO")),
                    Integer.parseInt(jsonObject.getString("SEQ")));

            byID.setStatus(UmcConstants.DOC_D_STATUS_2);
            pchD003.updateD003(byID);

        }
    }

    public String getPoSendPDFZWSType(String po) {
        T10EmailSendLog t10 = pchD010.getByPo(po);
        List<T02PoD> byPo = pchD002.getByPo(po);
        Boolean flag1 = false;
        // 判断明细是否为D
        for (T02PoD t02PoD : byPo) {
            if (!t02PoD.getPoType().equals("D")) {
                flag1 = true;
                break;
            }
        }
        // 如果flag1为false，则说明明细都是D，则返回Dtype
        if (!flag1) {
            return UmcConstants.DOC_D_STATUS_3;
        }
        // 如果T10EmailSendLog中type有type为Y的则为type1，否则为type2
        if (t10 != null) {
            if (t10.getType().equals("Y")) {
                return UmcConstants.ZWS_TYPE_2;
            } else {
                return UmcConstants.ZWS_TYPE_1;
            }
        } else {
            return UmcConstants.ZWS_TYPE_1;
        }
    }

}
