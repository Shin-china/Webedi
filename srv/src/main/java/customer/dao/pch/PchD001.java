package customer.dao.pch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import customer.comm.tool.DateTools;
import customer.dao.common.Dao;
import customer.dao.mst.MstD003;
import customer.dao.sys.SysD008Dao;
import customer.service.sys.EmailServiceFun;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cds.gen.pch.T02PoD;
import cds.gen.pch.T02PoD_;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T03PoC_;
import cds.gen.sys.T08ComOpD;
import cds.gen.MailBody;
import cds.gen.MailJson;
import cds.gen.mst.T03SapBp;
import cds.gen.pch.Pch_;

import java.io.IOException;
import java.time.Instant;

@Repository
public class PchD001 extends Dao {

    @Autowired
    SysD008Dao sysd008dao;

    @Autowired
    private MstD003 MSTDao;

    String H_CODE = "MM0004";

    @Autowired
    EmailServiceFun emailServiceFun;
    // 修改
    private static final Logger logger = LoggerFactory.getLogger(PchD001.class);

    public void sendMailToCreatePo(Map<String, String> supplierCreatMap) throws IOException {
        Collection<MailJson> mailJsonList = new ArrayList<>();

        // 创建发信
        if (supplierCreatMap.size() > 0) {
            for (Map.Entry<String, String> entry : supplierCreatMap.entrySet()) {

                // 获取供应商的键
                String supplierKey = entry.getKey();

                // 拆分公司代码和供应商代码
                String companyCode = supplierKey.substring(0, 4); // 前四位是公司代码
                String supplier = supplierKey.substring(4); // 后面是供应商代码

                MailJson mailJson = MailJson.create();
                List<T08ComOpD> emailadd = sysd008dao.getmailaddByHcodeV1(H_CODE, supplier);
                if (emailadd != null) {

                    // 将所有邮件地址合并为一个字符串
                    StringBuilder mailToBuilder = new StringBuilder();
                    for (T08ComOpD email : emailadd) {
                        mailToBuilder.append(email.getValue02()).append(","); // 使用分号分隔
                    }

                    // 去掉最后一个分号
                    if (mailToBuilder.length() > 0) {
                        mailToBuilder.setLength(mailToBuilder.length() - 1);
                    }

                    // 根据公司代码设置模板 ID
                    if ("1100".equals(companyCode)) {
                        mailJson.setTemplateId("UWEB_M004_C_UMCE");
                    }
                    else if ("1400".equals(companyCode)) {
                        mailJson.setTemplateId("UWEB_M004_C_UMCH");
                    }

                    mailJson.setMailTo(mailToBuilder.toString());
                    mailJson.setMailBody(createMailBody(emailadd)); // 设置邮件内容（MailBody）
                    // 添加到邮件列表
                    mailJsonList.add(mailJson);
                    // 调用邮件发送服务

                    emailServiceFun.sendEmailFun(mailJsonList);

                }
            }

        }

    }

    public void sendMailToUpdatePo(Map<String, String> supplierUpdateMap) throws IOException {
        Collection<MailJson> mailJsonList = new ArrayList<>();

        if (supplierUpdateMap.size() > 0) {
            for (Map.Entry<String, String> entry : supplierUpdateMap.entrySet()) {

                // 获取供应商的键
                String supplierKey = entry.getKey();

                // 拆分公司代码和供应商代码
                String companyCode = supplierKey.substring(0, 4); // 前四位是公司代码
                String supplier = supplierKey.substring(4); // 后面是供应商代码

                MailJson mailJson = MailJson.create();
                List<T08ComOpD> emailadd = sysd008dao.getmailaddByHcodeV1(H_CODE, supplier);
                if (emailadd != null) {

                    // 将所有邮件地址合并为一个字符串
                    StringBuilder mailToBuilder = new StringBuilder();
                    for (T08ComOpD email : emailadd) {
                        mailToBuilder.append(email.getValue02()).append(","); // 使用分号分隔
                    }

                    // 去掉最后一个分号
                    if (mailToBuilder.length() > 0) {
                        mailToBuilder.setLength(mailToBuilder.length() - 1);
                    }

                    // 根据公司代码设置模板 ID
                    if ("1100".equals(companyCode)) {
                        mailJson.setTemplateId("UWEB_M004_U_UMCE");
                    }
                    else if ("1400".equals(companyCode)) {
                        mailJson.setTemplateId("UWEB_M004_U_UMCH");
                    }

                    // mailJson.setTemplateId("UWEB_M004_U");
                    mailJson.setMailTo(mailToBuilder.toString());
                    mailJson.setMailBody(createMailBody(emailadd)); // 设置邮件内容（MailBody）
                    // 添加到邮件列表
                    mailJsonList.add(mailJson);
                    // 调用邮件发送服务

                    emailServiceFun.sendEmailFun(mailJsonList);

                }
            }

        }

    }

    public void sendMailToDeletePo(Map<String, String> supplierDeleteMap) throws IOException {
        Collection<MailJson> mailJsonList = new ArrayList<>();

        // 删除发信
        if (supplierDeleteMap.size() > 0) {
            for (Map.Entry<String, String> entry : supplierDeleteMap.entrySet()) {

                // 获取供应商的键
                String supplierKey = entry.getKey();

                String companyCode = supplierKey.substring(0, 4); // 前四位是公司代码
                String supplier = supplierKey.substring(4); // 后面是供应商代码

                MailJson mailJson = MailJson.create();
                List<T08ComOpD> emailadd = sysd008dao.getmailaddByHcodeV1(H_CODE, supplier);
                if (emailadd != null) {

                    // 将所有邮件地址合并为一个字符串
                    StringBuilder mailToBuilder = new StringBuilder();
                    for (T08ComOpD email : emailadd) {
                        mailToBuilder.append(email.getValue02()).append(","); // 使用分号分隔
                    }

                    // 去掉最后一个分号
                    if (mailToBuilder.length() > 0) {
                        mailToBuilder.setLength(mailToBuilder.length() - 1);
                    }

                    // 根据公司代码设置模板 ID
                    if ("1100".equals(companyCode)) {
                        mailJson.setTemplateId("UWEB_M004_D_UMCE");
                    }
                    else if ("1400".equals(companyCode)) {
                        mailJson.setTemplateId("UWEB_M004_D_UMCH");
                    }

                    // mailJson.setTemplateId("UWEB_M004_D");
                    mailJson.setMailTo(mailToBuilder.toString());
                    mailJson.setMailBody(createMailBody(emailadd)); // 设置邮件内容（MailBody）
                    // 添加到邮件列表
                    mailJsonList.add(mailJson);
                    // 调用邮件发送服务

                }
            }

        }

        emailServiceFun.sendEmailFun(mailJsonList);

    }

    // 创建 MailBody 的集合
    private Collection<MailBody> createMailBody(List<T08ComOpD> emailadd) {
        Collection<MailBody> bodies = new ArrayList<>();

        MailBody body = MailBody.create();

        String bpName = getBpName(emailadd.get(0).getValue01());

        body.setObject("vendor"); // 根据具体需求设置
        body.setValue(bpName); // 使用参数内容
        bodies.add(body);
        return bodies;
    }

    // 根据 BP_ID 获取 BP_NAME
    private String getBpName(String BP_ID) {

        T03SapBp T03 = MSTDao.getByID(BP_ID);

        if (T03 != null) {
            return T03.getBpName1();
        }

        return null;
    }

}
