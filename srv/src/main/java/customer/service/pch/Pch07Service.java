package customer.service.pch;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

// import org.apache.tomcat.util.openssl.pem_password_cb;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.services.messages.Messages;

import cds.gen.mst.T01SapMat;
import cds.gen.mst.T03SapBp;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T11IfManager;
import cds.gen.tableservice.PoTypePop;
import customer.dao.mst.MstD001;
import customer.dao.mst.MstD003;
import customer.bean.pch.Pch07;
import customer.service.Service;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.bean.pch.Pch01;
import customer.bean.pch.Pch07DataList;
import customer.comm.tool.MessageTools;
// import customer.usapbe.comm.tool.UniqueIDTool;
// import customer.usapbe.service.Service;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

@Component
public class Pch07Service {

    private boolean isValidDateFormat(String date) {
        // 使用正则表达式检查日期格式 YYYY/MM/DD
        String regex = "^\\d{4}/\\d{2}/\\d{2}$";
        return date != null && date.matches(regex);
    }

    @Autowired
    private MstD001 mstD001;  
    @Autowired
    private MstD003 mstD003;
    @Autowired
    private PchD006 pchD006; 
    @Autowired
    private PchD007 pchD007; 
    @Autowired
    private IFSManageDao ifsManageDao;

    public void detailsCheck(Pch07DataList list) {
        // 遍历上传的每条记录,
        for (Pch07 item : list.getList()) {
            // 1. 检查 SAP 品目代码 (MATERIAL_NUMBER -> MAT_ID)

            String matno =  item.getMATERIAL_NUMBER();
            String plant = item.getPLANT_ID();
            String bpno  =  item.getBP_NUMBER();
            String valstart =  item.getVALIDATE_START();
            String valend  =  item.getVALIDATE_END();

        // 1. 检查 SAP 品目代码 (MATERIAL_NUMBER -> MAT_ID)
        T01SapMat matid = mstD001.getByID(matno);
        if (matid == null || "Y".equals(matid.getDelFlag())) {
            item.setSUCCESS(false);
            item.setMESSAGE("SAP品目コード" + matno + "が未登録或已删除。チェックしてください。");
            item.setRESULT("失敗");
            item.setI_CON("sap-icon://error");
            item.setSTATUS("Error");
        }else {
            item.setSUCCESS(true);
            item.setRESULT("成功");
            item.setI_CON("sap-icon://sys-enter-2");
            item.setSTATUS("Success");
        }

        if (!item.getSUCCESS()) {
            // 如果前面已经失败，直接跳过后续检查
            continue;
        }

        // 2. 检查供应商 ID (BP_NUMBER -> BP_ID)
        T03SapBp bpid = mstD003.getByID(bpno);
        if (bpid == null || "Y".equals(bpid.getDelFlag())) {
            item.setSUCCESS(false);
            item.setMESSAGE("仕入先" + bpno + "が未登録或已删除。チェックしてください。");
            item.setRESULT("失敗");
            item.setI_CON("sap-icon://error");
            item.setSTATUS("Error");
        }else {
            item.setSUCCESS(true);
            item.setRESULT("成功");
            item.setI_CON("sap-icon://sys-enter-2");
            item.setSTATUS("Success");
        }

        if (!item.getSUCCESS()) {
            // 如果前面已经失败，直接跳过后续检查
            continue;
        }

        // 3. 检查日期格式 (VALIDATE_START, VALIDATE_END)
        if (!isValidDateFormat(valstart) || !isValidDateFormat(valend)) {
            item.setSUCCESS(false);
            item.setMESSAGE("日付形式はYYYY/MM/DDではないので、調整してください。");
            item.setRESULT("失敗");
            item.setI_CON("sap-icon://error");
            item.setSTATUS("Error");
        } else {
            item.setSUCCESS(true);
            item.setRESULT("成功");
            item.setI_CON("sap-icon://sys-enter-2");
            item.setSTATUS("Success");
        }
    }
}

    public void detailsSave(Pch07DataList list) throws Exception {
        HashMap<String,String> hs = new HashMap<>();
        for (Pch07 data : list.getList()) {

                String plant =  data.getPLANT_ID();
                String matno =  data.getMATERIAL_NUMBER();
                String no = "";
                if(hs.get(plant+matno) == null){
                     no = this.getNo(data);
                    hs.put(plant+matno, no);

                    // 调用 Web Service 获取响应
                String response = getResponse(data);
                // 处理响应并设置 t07QuotationD2 的字段
                // createT07(data, response); // 传递响应给 createT07 方法      
                    this.createT06(data);
                }else{
                     no = hs.get(plant+matno);             
                }
                data.setQUO_ITEM(no);
                this.createT07(data);
                        
        }

    }

    private void createT07(Pch07 data) throws IOException{

        String response = getResponse(data);
        System.out.println(response);
        T07QuotationD t07QuotationD2 = T07QuotationD.create();

        JSONObject jb=  (JSONObject)JSON.parse(response);
            // String string = jb.getString("Currency");
        T01SapMat number = mstD001.getByID(data.getMATERIAL_NUMBER());
        T03SapBp bpid = mstD003.getByID(data.getBP_NUMBER());
  
       
        // t07QuotationD2.setQuoNumber("MM0002");
        // t07QuotationD2.setQuoItem(Integer.parseInt(data.getQUO_ITEM()) );
        // t07QuotationD2.setMaterial(number.getMAT_NAME());
        // t07QuotationD2.setMaker(number.getMANUFACT_CODE());
        // t07QuotationD2.setManuMaterial(number.getMANUFACT_MATERIAL());
        // t07QuotationD2.setUwebUser(bpid.getBP_NAME1());
        // t07QuotationD2.setCustMaterial(number.getCUST_MATERIAL());   
        // t07QuotationD2.setQty(data.getQTY());
        // t07QuotationD2.setStatus("1"); 
        // t07QuotationD2.setInitialObj(data.getINITIAL_OBJ());

        t07QuotationD2.setUnit(jb.getString("Baseunit"));
        t07QuotationD2.setOriginalCou(jb.getString("Suppliercertorigincountry"));
        t07QuotationD2.setPriceControl(jb.getString("Pricingdatecontrol"));       
        t07QuotationD2.setMoq(jb.getString("Minimumpurchaseorderquantity"));
        t07QuotationD2.setIncoterms(jb.getString("Incotermsclassification"));
        t07QuotationD2.setIncotermsText(jb.getString("Incotermslocation1"));
        t07QuotationD2.setLeadTime(jb.getString("Materialpriceunitqty"));
        t07QuotationD2.setPrice(jb.getString("Netpriceamount"));
        t07QuotationD2.setCurrency(jb.getString("Currency"));
        
        pchD007.insert(t07QuotationD2);
    }

    private String getNo(Pch07 data) {
        return "MM0001";
     }

    private void createT06(Pch07 data) {
        T06QuotationH t06QuotationH = T06QuotationH.create();
        
        // t06QuotationH.setQUO_NUMBER(data.getQUO_NUMBER());
        // t06QuotationH.setValidateStart(data.getVALIDATE_START());
        // t06QuotationH.setValidateEnd(data.getVALIDATE_END());
        // t06QuotationH.setPLANT_ID(data.getPLANT_ID());

        pchD006.insert(t06QuotationH);
    }


    private String getResponse(Pch07 data) throws IOException {
        ArrayList ar = new ArrayList<>();
        HashMap<String ,String> map = new HashMap<>();
        map.put("material_number", data.getMATERIAL_NUMBER());
        map.put("bp_number", data.getBP_NUMBER());
        map.put("plant_id", data.getPLANT_ID());
        ar.add(map);
   
            // 获取 Web Service 配置信息
            T11IfManager webServiceConfig = ifsManageDao.getByCode("MM036");
            // 调用 Web Service 的 get 方法
            String response = S4OdataTools.post(webServiceConfig, JSON.toJSONString(ar), null);
        return response;

    
    }}