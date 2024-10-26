package customer.service.pch;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T07QuotationD;
import cds.gen.tableservice.PchT03PoItemPrint;
import customer.bean.com.UmcConstants;
import customer.bean.pch.Pch08DataList;
import customer.bean.pch.PchQuoH;
import customer.bean.pch.PchQuoItem;
import customer.dao.pch.Pch01saveDao;
import customer.dao.pch.Pch08Dao;
import customer.dao.pch.PchD002;
import customer.dao.pch.PchD003;
import customer.dao.pch.PchD007;

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

}
