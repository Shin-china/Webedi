package customer.service.pch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

// import org.apache.tomcat.util.openssl.pem_password_cb;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.messages.Messages;

import cds.gen.mst.T01SapMat;
import cds.gen.mst.T03SapBp;
import cds.gen.pch.T03PoC;
import cds.gen.tableservice.PoTypePop;
import customer.dao.mst.MstD001;
import customer.dao.mst.MstD003;
import customer.bean.pch.Pch07;
import customer.service.Service;

import customer.bean.pch.Pch01;
import customer.bean.pch.Pch07DataList;
import customer.comm.tool.MessageTools;
// import customer.usapbe.comm.tool.UniqueIDTool;
// import customer.usapbe.service.Service;
import customer.dao.sys.IFSManageDao;

@Component
public class Pch07Service {

    @Autowired
    private MstD001 mstD001;  
    @Autowired
    private MstD003 mstD003;

    public void detailsCheck(Pch07DataList list) {
        // 遍历上传的每条记录,
        for (Pch07 item : list.getList()) {
            // 1. 检查 SAP 品目代码 (MATERIAL_NUMBER -> MAT_ID)

            String matno =  item.getMATERIAL_NUMBER();
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

        // // 3. 检查日期格式 (VALIDATE_START, VALIDATE_END)
        // if (!isValidDateFormat(valstart) || !isValidDateFormat(valend)) {
        //     item.setSUCCESS(false);
        //     item.setMESSAGE("日付形式はYYYY/MM/DDではないので、調整してください。");
        // }else {
        //     item.setSUCCESS(true);
        // }

    }

}}