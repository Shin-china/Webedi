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
import cds.gen.common.PchT06QuotationH;
import cds.gen.common.PchT07QuotationD;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
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

    public void extracted(ArrayList<PchT06QuotationH> pch06List, ArrayList<cds.gen.pch.T06QuotationH> pch06List2) {
        System.out.println("开始插入");
        pch06List.forEach(pchT06QuotationH -> {

            // 获取購買見積番号
            // pchT06QuotationH.setQuoNumber(docNoDao.getPJNo(1));

            // pchT06QuotationH.setQuoNumber(docNoDao.getQuoNumber("", 1));
            pchT06QuotationH.setQuoNumber(pchT06QuotationH.getQuoNumber());

            // 插入头标，首先删除原key值数据
            T06QuotationH t06QuotationH = T06QuotationH.create();

            // 复制类属性
            BeanUtils.copyProperties(pchT06QuotationH, t06QuotationH);
            // 如果已经存在则更新，如果不存在则插入
            T06QuotationH byID = PchD006.getByIdOnle(t06QuotationH.getId());
            pch06List2.add(t06QuotationH);
            t06QuotationH.remove("TO_ITEM_PO");
            t06QuotationH.remove("TO_ITEMS");

            if (byID != null) {
                PchD006.update(t06QuotationH);
            } else {
                PchD006.insert(t06QuotationH);
            }

            // 插入明细
            List<PchT07QuotationD> toItems = pchT06QuotationH.getToItemPo();
            if (toItems != null) {
                toItems.forEach(pchT07QuotationD -> {
                    // 获取購買見積番号
                    pchT07QuotationD.setQuoNumber(pchT06QuotationH.getQuoNumber());

                    T07QuotationD t07QuotationD = T07QuotationD.create();

                    // // 复制类属性
                    BeanUtils.copyProperties(pchT07QuotationD, t07QuotationD);
                    // // 如果已经存在则更新，如果不存在则插入
                    T07QuotationD byID2 = PchD007.getByT07Id(t07QuotationD.getId());

                    if (byID2 != null) {
                        PchD007.update(t07QuotationD);
                    } else {
                        PchD007.insert(t07QuotationD);
                    }
                });
            }
        });
    }
}
