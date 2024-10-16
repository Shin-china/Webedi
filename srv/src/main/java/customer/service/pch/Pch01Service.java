package customer.service.pch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

// import org.apache.tomcat.util.openssl.pem_password_cb;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.messages.Messages;
import com.sap.cloud.sdk.result.IntegerExtractor;

import cds.gen.pch.T03PoC;
import cds.gen.tableservice.PoTypePop;
import customer.bean.pch.Pch01List;
import customer.service.Service;

import customer.bean.pch.Pch01;
import customer.comm.tool.MessageTools;
// import customer.usapbe.comm.tool.UniqueIDTool;
// import customer.usapbe.service.Service;
import customer.dao.pch.Pch01saveDao;
import customer.dao.sys.IFSManageDao;

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

        int lastdn = 0;
        String lastpo = "0";

        BigDecimal afquantity = BigDecimal.ZERO; // 初始化数量累加变量 有减少数量的最大日期之后的合计

        BigDecimal bequantity = BigDecimal.ZERO; // 初始化数量累加变量 有减少数量的最大日期之前的合计（RelevantQuantity）

        BigDecimal allquantity = BigDecimal.ZERO;

        BigDecimal POquantity = BigDecimal.ZERO;

        int index = 0;

        if (list.getList().size() == 0) {
            list.setReTxt("表为空");
        } else {
            // 遍历上传文件 检查相应字段
            for (Pch01 s : list.getList()) {

                index++;

                if (lastdn == 0 && lastpo == "0") {
                    // 第一次循环，直接赋值
                    lastdn = s.getD_NO();
                    lastpo = s.getPO_NO();
                }

                // ckd.check_auth(s);

                // 检查采购订单号
                if (s.getPO_NO() != null && s.getPO_NO() != "") {
                    ckd.checkPO_NO(s);
                }

                // 检查明细号
                if (s.getSUCCESS() && s.getPO_NO() != null && s.getPO_NO() != "" && s.getD_NO() != null) {
                    ckd.checkD_NO(s);
                }

                LocalDate lastrelevantdate = ckd.RelevantQuantitydate(s.getD_NO(), s.getPO_NO());

                // 不是最后一条且，和上一条一样同属于同一个po dn
                if (lastpo.equals(s.getPO_NO()) && lastdn == s.getD_NO() && index != list.getList().size()) {

                    // 合计有减少数量日期之后的 上传数量（未来纳期数量）
                    if (lastrelevantdate != null) {

                        if (s.getDELIVERY_DATE().isAfter(lastrelevantdate)) {

                            afquantity = afquantity.add(s.getQUANTITY());

                        }

                    } else {
                        // 如果为空，则全部都是 afquantity
                        afquantity = afquantity.add(s.getQUANTITY());

                    }

                } else {
                    // 换po 或者dn
                    // 如果不是最后一条，那就是换po 或者dn 了
                    if (index != list.getList().size()) {
                        // 先集计 上一条的结果。
                        bequantity = ckd.checkQUANTITY(lastpo, lastdn);
                        allquantity = afquantity.add(bequantity).setScale(3);

                        POquantity = ckd.getT02POquantity(lastpo, lastdn);

                        // 如果发注数量，等于 减少数量+未来回答数量 则没问题
                        if (POquantity.equals(allquantity)) {

                            s.setSUCCESS(true);

                        } else {

                            for (Pch01 s1 : list.getList()) {

                                if (s1.getPO_NO().equals(lastpo) && s1.getD_NO().equals(lastdn)) {

                                    s1.setSUCCESS(false);
                                    s1.setI_CON("sap-icon://error");
                                    s1.setSTATUS("Error");
                                    String addmessages = ("購買発注" + s1.getPO_NO() + "明細" + s1.getD_NO()
                                            + "の納期回答数プラス入庫済数は発注数と不一致です。チェックしてください");
                                    s1.setMSG_TEXT(addmessages);

                                }
                            }
                        }
                        // 因为不是最后一天，因此 准备本条的信息，参考相等时候的做法
                        // 因为换po dn了 更新一下
                        lastpo = s.getPO_NO();
                        lastdn = s.getD_NO();
                        afquantity = BigDecimal.ZERO;

                        if (lastrelevantdate != null) {

                            if (s.getDELIVERY_DATE().isAfter(lastrelevantdate)) {

                                afquantity = afquantity.add(s.getQUANTITY());

                            }

                        } else {
                            // 如果为空，则全部都是 afquantity
                            afquantity = afquantity.add(s.getQUANTITY());

                        }

                        bequantity = BigDecimal.ZERO;

                    } else {

                        // 最后一条、
                        // 因为后续没有了，所以应该先集计本条
                        lastpo = s.getPO_NO();
                        lastdn = s.getD_NO();

                        if (lastrelevantdate != null) {

                            if (s.getDELIVERY_DATE().isAfter(lastrelevantdate)) {

                                afquantity = afquantity.add(s.getQUANTITY());

                            }
                        } else {
                            // 如果为空，则全部都是 afquantity
                            afquantity = afquantity.add(s.getQUANTITY());

                        }

                        POquantity = ckd.getT02POquantity(lastpo, lastdn);

                        bequantity = ckd.checkQUANTITY(lastpo, lastdn);
                        allquantity = afquantity.add(bequantity).setScale(3);

                        if (POquantity.equals(allquantity)) {

                            s.setSUCCESS(true);

                        } else {

                            s.setSUCCESS(false);

                            for (Pch01 s1 : list.getList()) {

                                if (s1.getPO_NO().equals(lastpo) && s1.getD_NO().equals(lastdn)) {

                                    s1.setSUCCESS(false);
                                    s1.setI_CON("sap-icon://error");
                                    s1.setSTATUS("Error");
                                    String addmessages = ("購買発注" + s1.getPO_NO() + "明細" + s1.getD_NO()
                                            + "の納期回答数プラス入庫済数は発注数と不一致です。チェックしてください");
                                    s1.setMSG_TEXT(addmessages);
                                    addmessages = "";

                                }
                            }
                        }
                    }
                }
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
                    list.setErr(true);

                }

                list.setReTxt(

                        MessageTools.getMsgText(rbms, "PCH01_CHECK_RETURN_TEXT", list.getList().size(), sucC, errC));

            }
            ;
        }
    }

    public void detailsSave(Pch01List list) {
        int delC = 0;
        int incC = 0;
        int index = 0;

        int seq = 0;

        int lastdn = 0;
        String lastpo = "0";

        Boolean delsuccess = false;

        for (Pch01 s : list.getList()) {

            index++;

            // 换po，或者是换dn了。 或者是第一条
            if ((!s.getPO_NO().equals(lastpo) && s.getD_NO() != lastdn) || index == 1) {
                // 获取新dn
                lastdn = s.getD_NO();
                // 获取新po
                lastpo = s.getPO_NO();

                // 当没有减少数量日期的时候
                delsuccess = savaDao.delete_pono(s.getPO_NO(), s.getD_NO());

                seq = savaDao.getSeq(lastpo, lastdn);
            }

            if (delsuccess) {

                seq++;

                T03PoC t03 = T03PoC.create();
                t03.setDNo(s.getD_NO()); // 设置采购订单编号
                t03.setPoNo(s.getPO_NO()); // 采购订单明细行号
                t03.setSeq(seq); // 序号
                t03.setDeliveryDate(s.getDELIVERY_DATE()); // 交货日期
                t03.setQuantity(s.getQUANTITY()); // 交货数量

                Boolean success = savaDao.insertt03(t03);

                if (success) {

                    list.setErr(false);// 有无错误
                    list.setReTxt("insert success");// 返回消息
                    s.setTYPE("success");// 返回结果

                } else {

                    list.setErr(true);
                    list.setReTxt("insert faild");
                    s.setTYPE("error");// 返回结果

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
        s.setTYPE(MessageTools.getMsgText(rbms, "PCH01_DELETE_RETURN_TEXT", ""));
        delC++;
        return delC;
    }

}