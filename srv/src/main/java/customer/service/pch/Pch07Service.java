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

import cds.gen.pch.T03PoC;
import cds.gen.tableservice.PoTypePop;
import customer.dao.mst.MstD007;
import customer.bean.pch.Pch07;
import customer.service.Service;

import customer.bean.pch.Pch01;
import customer.bean.pch.Pch07DataList;
import customer.comm.tool.MessageTools;
// import customer.usapbe.comm.tool.UniqueIDTool;
// import customer.usapbe.service.Service;
import customer.dao.pch.Pch01saveDao;
import customer.dao.sys.IFSManageDao;

@Component
public class Pch07Service {

    @Autowired
    private MstD007 mstD007;  // 注入 MstD007 DAO

    /**
     * 验证上传的记录详情。
     * 
     * @param list 上传的记录列表
     */
    public void detailsCheck(Pch07DataList list) {
        // 遍历上传的每条记录,
        for (Pch07 item : list.getList()) {
            // 1. 检查 SAP 品目代码 (MATERIAL_NUMBER -> MAT_ID)
            // if (!mstD007.isSapMaterialValid(item.getMaterialNumber())) {
            //     // 如果未能从表中获取数据，记录错误
            //     item.setSuccess(false);
            //     item.setMessage("SAP品目コード" + item.getMaterialNumber() + "が登録されていません。チェックしてください。");
            // }

            // // 2. 检查供应商 ID (BP_NUMBER -> BP_ID)
            // if (!mstD007.isSapBpValid(item.getBpNumber())) {
            //     // 如果未能从表中获取数据，记录错误
            //     item.setSuccess(false);
            //     item.setMessage("仕入先" + item.getBpNumber() + "が登録されていません。チェックしてください。");
            // }

            // // 3. 检查日期格式 (VALIDATE_START, VALIDATE_END)
            // if (!isValidDateFormat(item.getValidateStart()) || !isValidDateFormat(item.getValidateEnd())) {
            //     // 如果日期格式不符合要求
            //     item.setSuccess(false);
            //     item.setMessage("日付形式はYYYY/MM/DDではないので、調整してください。");
            // }
        }
    }

    // 验证日期格式是否为 YYYY/MM/DD
    private boolean isValidDateFormat(String dateStr) {
        try {
            // 使用正则表达式匹配 YYYY/MM/DD 格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}