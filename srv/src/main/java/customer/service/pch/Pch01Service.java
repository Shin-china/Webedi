package customer.service.pch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.messages.Messages;

import cds.gen.pch.PchT02PoD;
import cds.gen.pch.PchT03PoC;
import customer.bean.pch.Pch01List;
import customer.service.Service;

// import cds.gen.mst.T06Shelf;
// import cds.gen.tableservice.MST03SHEFSAVEContext;
// import customer.usapbe.bean.com.CommCheck;
import customer.bean.pch.Pch01;
import customer.comm.tool.MessageTools;
// import customer.usapbe.comm.tool.UniqueIDTool;
// import customer.usapbe.service.Service;
import customer.dao.pch.Pch01saveDao;

@Component
public class Pch01Service extends Service {

    @Autowired
    Messages messages;

    @Autowired
    CheckDataService ckd;

    @Autowired
    ResourceBundleMessageSource rbms;

    @Autowired
    Pch01saveDao savaDao;

    // @Autowired
    // MaterialDao materialDao;

    /**
     * 
     * @param list
     * @return
     */
    public void detailsCheck(Pch01List list) {
        // System.out.println("yyy");
        // list.setReTxt(MessageTools.getMsgText(rbms, "MST07_CHECK_RETURN_TEXT",
        // list.getList().size(), sucC, errC));
        // list.setReTxt("aaaaa");
        int errC = 0;
        int sucC = 0;
        int count = 0;
        if (list.getList().size() == 0) {
            list.setReTxt("表为空");
        } else {
            // 遍历上传文件 检查相应字段
            for (Pch01 s : list.getList()) {

                // ckd.check_auth(s);

                // 检查po号
                if (s.getPO_NO() != null && s.getPO_NO() != "") {
                    ckd.checkPO_NO(s);
                }

                // 检查
                if (s.getSUCCESS() && s.getPO_NO() != null && s.getPO_NO() != "" && s.getD_NO() != null) {
                    ckd.checkD_NO(s);
                }

                // 根据区分，检查时间
                if (s.getSUCCESS()) {
                    ckd.checkPO_TYPE(s);
                }

                if (s.getSUCCESS()) {
                    // 设置图标
                    s.setI_CON("sap-icon://sys-enter-2");
                    s.setSTATUS("Success");
                    sucC++;
                }

                else {
                    // 设置图标
                    s.setI_CON("sap-icon://error");
                    s.setSTATUS("Error");
                    errC++;
                    list.setErr(true);
                }

                list.setReTxt(
                        MessageTools.getMsgText(rbms, "PCH01_CHECK_RETURN_TEXT", list.getList().size(), sucC, errC));
            }
        }
    }

    public void detailsSave(Pch01List list) {
        int delC = 0;
        int incC = 0;

        for (Pch01 s : list.getList()) {
            int dNO = s.getD_NO();
            PchT02PoD byID = savaDao.getByID(s.getPO_NO(), dNO);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (byID == null && s.getPO_TYPE().equals("1")) {
                // 如果是1 则是新规
                PchT02PoD t02 = PchT02PoD.create();
                PchT03PoC t03 = PchT03PoC.create();

                LocalDate date1 = s.getDELIVERY_DATE();

                t02.setDNo(s.getD_NO());
                t02.setPoNo(s.getPO_NO());
                t02.setMatId(s.getMAT_ID());
                t02.setPoDTxz01(s.getPO_D_TXZ01());
                t02.setPoPurQty(s.getPO_PUR_QTY());
                t02.setPoPurUnit(s.getPO_PUR_UNIT());
                t02.setSupplierMat(s.getSUPPLIER_MAT());

                t03.setDNo(s.getD_NO());
                t03.setPoNo(s.getPO_NO());
                // t03.setDeliveryDate(date1);
                t03.setQuantity(s.getQUANTITY());

                savaDao.insert(t02, t03);

                if ("N".equals(s.getDELETE())) {
                    // 设置成功消息
                    incC = setSPch(incC, s);
                    // 如果删除Flag是Y则删除
                } else {
                    // 设置失败消息
                    delC = setEmsg(delC, s);
                }

            } else if (byID != null && s.getPO_TYPE().equals("2")) {
                // 如果是2 则是修改

                PchT02PoD t02 = PchT02PoD.create();
                PchT03PoC t03 = PchT03PoC.create();

                t03.setDNo(s.getD_NO());
                t03.setPoNo(s.getPO_NO());
                // t03.setDeliveryDate(date1);
                t03.setQuantity(s.getQUANTITY());

                // 通过id修改
                savaDao.update(t02, t03);

                if ("N".equals(s.getDELETE())) {
                    // 设置成功消息
                    incC = setSPch(incC, s);
                    // 如果删除Flag是Y则删除
                } else {
                    // 设置失败消息
                    delC = setEmsg(delC, s);
                }

            }
        }
    }

    private int setSPch(int incC, Pch01 s) {
        s.setTYPE(MessageTools.getMsgText(rbms, "PCH01_INSERT_RETURN_TEXT", ""));
        incC++;
        return incC;
    }

    // 设置失败消息
    private int setEmsg(int delC, Pch01 s) {
        s.setTYPE(MessageTools.getMsgText(rbms, "MST07_DELETE_RETURN_TEXT", ""));
        delC++;
        return delC;
    }

}
