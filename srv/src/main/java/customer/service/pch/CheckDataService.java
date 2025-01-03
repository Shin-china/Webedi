package customer.service.pch;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.messages.Messages;

import cds.gen.pch.T01PoH;
import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.tableservice.Pch01Auth1;
import cds.gen.tableservice.SysT09User2Plant;
import customer.bean.pch.Pch01;
import customer.bean.sys.Sys07;
import customer.comm.tool.MessageTools;
import customer.comm.tool.StringTool;
import customer.dao.pch.DeliverydateDao;
import customer.dao.pch.PodataDao;
import customer.dao.pch.PodndataDao;
import customer.dao.pch.UserdataDao;
import customer.service.Service;

@Component
public class CheckDataService extends Service {
    @Autowired
    Messages messages;

    @Autowired
    ResourceBundleMessageSource rbms;

    @Autowired
    DeliverydateDao deliverydateDao;

    @Autowired
    PodataDao podataDao;

    @Autowired
    PodndataDao podndataDao;

    @Autowired
    UserdataDao userdataDao;

    public void checkPO_TYPE(Pch01 s) {
        int dNO = s.getD_NO();
        // Define a formatter to parse the date strings

        T03PoC deliverydateData = deliverydateDao.getByID(s.getPO_NO(), dNO, s.getDELIVERY_DATE());
        // 納期回答ファイルの区分=1:新規、
        // かつ納品日はDBにすでに存在する場合に、下記のエラーメッセージを表示する
        if ("1".equals(s.getPO_TYPE())) {
            // 納期回答ファイルの区分=1:新規、
            // かつ納品日はDBにすでに存在する場合に、下記のエラーメッセージを表示する
            if (deliverydateData == null) {
                s.setSUCCESS(true);
            } else {
                s.setMSG_TEXT("新規納期回答なので、同じ納品日はすでに存在します。データをチェックしてください。");
                s.setSUCCESS(false);
            }

        } else if ("2".equals(s.getPO_TYPE())) {
            // 納期回答ファイルの区分=2:変更、
            // かつ納品日はDBに存在しない場合に、下記のエラーメッセージを表示する
            if (deliverydateData == null) {

                s.setMSG_TEXT("変更納期回答なので、同じ納品日の納期回答データは存在しません。データをチェックしてください。");
                s.setSUCCESS(false);
            } else {
                s.setSUCCESS(true);
            }

        } else {
            s.setSUCCESS(false);
        }

    }

    public void checkPO_NO(Pch01 s) {
        // T01_PO_H

        T01PoH poData = podataDao.getByID(s.getPO_NO());
        // 如果能取到poData 则值正确，为null 则无

        if (poData != null) {
            s.setSUCCESS(true);
        } else {
            s.setMSG_TEXT("購買伝票" + s.getPO_NO() + "が登録されていません。チェックしてください。");
            s.setSUCCESS(false);
        }
    }

    public void checkD_NO(Pch01 s) {

        int dNO = s.getD_NO();
        T02PoD podnData = podndataDao.getByID(s.getPO_NO(), dNO);

        if (podnData != null) {
            s.setSUCCESS(true);
        } else {
            s.setMSG_TEXT("購買伝票" + s.getPO_NO() + "明細" + s.getD_NO() + "が登録されていません。チェックしてください。");
            s.setSUCCESS(false);
        }

    }

    public void check_auth(Pch01 s) {

        Pch01Auth1 userData = userdataDao.getUSER_DATA();// 取得类型
        SysT09User2Plant plantData = userdataDao.getT09_PLANT();// 取得SYS_T09_USER_2_PLANT中的plant id

        if ("1".equals(userData.getUserType())) {

            int dNO = s.getD_NO();
            T02PoD podnData = podndataDao.getByID(s.getPO_NO(), dNO);// 取得T02_PO_D中的plant id

            // 两个相等则有权限。
            if (podnData.getPlantId().equals(plantData.getPlantId())) {
                s.setSUCCESS(true);
            } else {
                s.setMSG_TEXT("購買伝票" + s.getPO_NO() + "明細" + s.getD_NO() + "に対して権限がありません。チェックしてください。");
                s.setSUCCESS(false);
            }

        } else if ("2".equals(userData.getUserType())) {
            T01PoH poData = podataDao.getByID(s.getPO_NO());

            if (userData.getBpNumber().equals(poData.getSupplier())) {
                s.setSUCCESS(true);
            } else {
                s.setMSG_TEXT("購買伝票" + s.getPO_NO() + "明細" + s.getD_NO() + "に対して権限がありません。チェックしてください。");
                s.setSUCCESS(false);
            }
        }

    }

    public BigDecimal checkQUANTITY(String lastpo, int lastdn) {

        BigDecimal relevantquantityData = podataDao.getByID2(lastdn, lastpo);

        return relevantquantityData;
    }

    public LocalDate RelevantQuantitydate(Integer d_NO, String po_NO) {

        LocalDate lastdeliverydateData = podataDao.getLastDeliveryDate(d_NO, po_NO);
        return lastdeliverydateData;

    }

    public BigDecimal getT02POquantity(String PO_NO, Integer D_NO) {

        BigDecimal PoQuantity = podndataDao.getQuantity(PO_NO, D_NO);
        return PoQuantity;

    }

    /**
     * sys07用通用不能为空字段
     * 
     * @param s
     */
    public void checkNull(Sys07 s) {
        // H_CODE
        s = this.checkNull(s.getH_CODE(), "業務区分", s);
        s = this.checkNull(s.getH_NAME(), "業務名", s);
        s = this.checkNull(s.getBP_ID(), "仕入先", s);
        s = this.checkNull(s.getEMAIL_ADDRESSY(), "メールアドレス", s);
        s = this.checkNull(s.getEMAIL_ADDRESS_NAME(), "担当者", s);
        // BP_ID
        // SYS07_EMAIL_ADDRESSY

    }

    public void checkData(Sys07 s, HashSet<String> dno) {
        String id = s.getH_CODE() + s.getBP_ID() + s.getEMAIL_ADDRESSY();
        Boolean boo = false;

        boo = setCheckCount(dno, id, boo);
        if (boo) {
            s.setError(MessageTools.getMsgText(rbms, "EROOR_CHECK_DATA_COUNT", "明細"));
        }

    }

    public Boolean setCheckCount(HashSet<String> dno, String id) {
        Boolean boo = false;
        if (StringUtils.isNotBlank(id)) {
            int countA = dno.size();
            dno.add(id);
            int countB = dno.size();
            // 判断明细行号是否重复的逻辑
            if (countA == countB) {
                boo = true;

            }
        }

        return boo;
    }

    /**
     * 
     * 共通方法上传文件判空
     * 
     * @param t 传入要增加的类
     * @param s 要判空的值
     * @param v 如果为空报错信息
     * 
     */
    public <T> T checkNull(String s, String v, T t) {

        if (StringTool.isNull(s)) {
            Class<? extends Object> class1 = t.getClass();

            try {
                Method setCreatedby = class1.getMethod("setError", String.class);
                setCreatedby.invoke(t, MessageTools.getMsgText(rbms, "UPLOAD_EROOR_SNULL", v));
            } catch (Exception e) {
                throw new ServiceException(ErrorStatuses.GATEWAY_TIMEOUT, "ERROR");
            }

        }
        return t;
    }

    /**
     * 
     * 共通方法上传文件判空 SYS07用
     * 
     * @param s 要判空的值
     * @param v 如果为空报错信息
     * 
     */
    public Boolean checkNullD(String s, String v, String id, String red, Boolean isDelete) {

        if (StringUtils.isBlank(s)) {
            messages.error("UPLOAD_EROOR_SNULL", v)
                    .target("/T17_EMAIL_D(ID=guid'" + id
                            + "',IsActiveEntity=false" + ")/" + red);
            isDelete = true;
            return isDelete;
        }
        return isDelete;
    }

    /**
     * 
     * 共通方法上传文件判空 SYS07用
     * 
     * @param s 要判空的值
     * @param v 如果为空报错信息
     * 
     */
    public Boolean checkNullH(String s, String v, String id, String red, Boolean isDelete) {

        if (StringUtils.isBlank(s)) {
            messages.error("UPLOAD_EROOR_SNULL", v)
                    .target("/T16_EMAIL_H(ID=guid'" + id
                            + "',IsActiveEntity=false" + ")/" + red);
            isDelete = true;
            return isDelete;
        }
        return isDelete;
    }
}