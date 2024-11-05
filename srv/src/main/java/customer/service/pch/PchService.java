package customer.service.pch;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T07QuotationD;
import cds.gen.pch.T10Upload;
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
    PchD010Dao pchD010;

    // 根据传入的po和po明细修改po明细状态
    public void setPoStu(String po, String dNo) {
        T02PoD byID = pchD002.getByID(po, Integer.parseInt(dNo));
        byID.setStatus(UmcConstants.PCH02_STATUS_02);
        pchD002.updateD002(byID);
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
        T10Upload byID2 = pchD010.getByID(po, Integer.parseInt(dNo));
        // 如果t10为空则为新规，如果不为空则为 修改
        if (byID2 == null) {

            T10Upload t10Upload = T10Upload.create();
            t10Upload.setPoNo(po);
            t10Upload.setDNo(Integer.parseInt(dNo));
            t10Upload.setQuantity(byID.getPoPurQty());
            t10Upload.setInputDate(byID.getPoDDate());
            t10Upload.setDelFlag(byID.getDelFlag());
            t10Upload.setPoType(byID.getPoType());
            // 如果t不为空，则进行写Y操作
            if (!StringTool.isEmpty(t)) {
                t10Upload.setType("Y");
            }

            pchD010.insert(t10Upload);
        } else {
            T10Upload t10Upload = T10Upload.create();

            t10Upload.setQuantity(byID.getPoPurQty());
            t10Upload.setInputDate(byID.getPoDDate());
            t10Upload.setDelFlag(byID.getDelFlag());
            t10Upload.setPoType(byID.getPoType());
            // 如果t不为空，则进行写Y操作
            if (!StringTool.isEmpty(t)) {
                t10Upload.setType("Y");
            }

            pchD010.update(t10Upload);
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

}
