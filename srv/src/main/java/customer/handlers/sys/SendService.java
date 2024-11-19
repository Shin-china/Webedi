package customer.handlers.sys;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
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

import cds.gen.pch.T06QuotationH;
import cds.gen.sys.T11IfManager;

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
        // 直接从上下文中获取参数
        JSONArray jsonArray = JSONArray.parseArray(json);
        // 根据传入的po和po明细修改po明细状态
        // 获取要传入的字符串
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            T06QuotationH t06QuotationH = PchD006.get(jsonObject.getString("QUO_NUMBER"));
            pch06List.add(t06QuotationH);
        }
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

}
