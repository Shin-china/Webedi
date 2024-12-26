package customer.service.sys;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.messages.Messages;

import cds.gen.sys.T16EmailH;
import cds.gen.sys.T17EmailD;
import customer.bean.sys.Sys07;
import customer.bean.sys.Sys07List;
import customer.comm.tool.MessageTools;
import customer.comm.tool.StringTool;
import customer.comm.tool.TranscationTool;
import customer.dao.sys.T16EmailHDao;
import customer.dao.sys.T17EmailDDao;
import customer.service.Service;
import customer.service.pch.CheckDataService;
import customer.tool.UniqueIDTool;

@Component
public class Sys07Service {
    @Autowired
    Messages messages;

    @Autowired
    CheckDataService ckd;
    @Autowired
    ResourceBundleMessageSource rbms;

    @Autowired
    T16EmailHDao t16EmailHDao;
    @Autowired
    T17EmailDDao t17EmailDDao;

    public void sys07DetailsCheck(Sys07List list) {
        int errC = 0;
        int sucC = 0;
        int count = 0;
        // 关键字第一次重复列表
        HashSet<String> dno = new HashSet<String>();
        if (list.getList().size() == 0) {
            list.setReTxt(MessageTools.getMsgText(rbms,
                    "SD07_CHECK_RETURN_ERROR_TEXT"));
        } else {
            // 判断明细行是否有重复的数据

            for (Sys07 s : list.getList()) {
                // 清除数据中msg
                setClean(s);

                // 新规check
                ckd.checkNull(s);
                // ckd.checkData(s);

                if (s.getSUCCESS()) {
                    // 设置图标
                    s.setI_CON("sap-icon://sys-enter-2");
                    s.setSTATUS("Success");
                    sucC++;
                } else {
                    // 设置图标
                    s.setI_CON("sap-icon://error");
                    s.setSTATUS("Error");
                    errC++;
                    list.setErr("getUserId()");
                }

            }
            list.setReTxt(MessageTools.getMsgText(rbms, "SD07_CHECK_RETURN_TEXT",
                    list.getList().size(), sucC, errC));
        }
    }

    public void detailsSave(Sys07List list) {
        // 是否创建过头
        HashMap<String, String> hashMap = new HashMap<>();
        ArrayList<Sys07> al = list.getList();

        for (int i = 0; i < list.getList().size(); i++) {
            Sys07 s = al.get(i);

            // 新规登录
            createHDA(s, hashMap);

            if (s.getSUCCESS()) {
                // 设置图标
                s.setI_CON("sap-icon://sys-enter-2");
                s.setSTATUS("Success");
                setImsg(s);
            } else {
                // 设置图标
                s.setI_CON("sap-icon://error");
                s.setSTATUS("Error");
                list.setErr("getUserId()");
            }
        }
    }

    private void createHDA(Sys07 s, HashMap<String, String> hashMap) {
        // 创建头
        // 如果没有之前存在的头，则创建新的头，然后插入其中
        if (StringTool.isEmpty(hashMap.get(s.getH_CODE() + s.getBP_ID()))) {
            createH(s, hashMap);
        }

        createD(s, hashMap);
    }

    private void setClean(Sys07 s) {
        s.setMSG_TEXT("");
        // s.setSTATUS("");
        s.setSUCCESS(true);
        s.setI_CON(null);

    }

    private void createD(Sys07 s, HashMap<String, String> hashMap) {
        T17EmailD t = T17EmailD.create(UniqueIDTool.getUUID());
        // EMAIL_ADDRESSY
        t.setEmailAddress(s.getEMAIL_ADDRESSY());
        // EMAIL_ADDRESS_NAME
        t.setEmailAddressName(s.getEMAIL_ADDRESS_NAME());
        // H_ID
        t.setHId(hashMap.get(s.getH_CODE() + s.getBP_ID()));
        t17EmailDDao.insert(t);

    }

    private void createH(Sys07 s, HashMap<String, String> hashMap) {

        T16EmailH t = T16EmailH.create(UniqueIDTool.getUUID());
        // H_CODE
        t.setHCode(s.getH_CODE());
        // H_NAME
        t.setHName(s.getH_NAME());
        // BP_ID
        t.setBpId(s.getBP_ID());

        t16EmailHDao.insert(t);
        // 设置新規头Id
        hashMap.put(s.getH_CODE() + s.getBP_ID(), t.getId());

    }

    // 设置新規登録消息
    private void setImsg(Sys07 s) {
        s.setMSG_TEXT(MessageTools.getMsgText(rbms, "MST07_RETURN_TEXT", ""));

    }

}