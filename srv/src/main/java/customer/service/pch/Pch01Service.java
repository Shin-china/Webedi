package customer.service.pch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.messages.Messages;


import cds.gen.pch.PchT03PoC;
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
        if (list.getList().size() == 0) {
            list.setReTxt("表为空");
        } else {
            // 遍历上传文件 检查相应字段
            for (Pch01 s : list.getList()) {

                // ckd.check_auth(s);

                // 检查采购订单号
                if (s.getPO_NO() != null && s.getPO_NO() != "") {
                    ckd.checkPO_NO(s);
                }

                // 检查明细号
                if (s.getSUCCESS() && s.getPO_NO() != null && s.getPO_NO() != "" && s.getD_NO() != null) {
                    ckd.checkD_NO(s);
                }
                
                // 根据区分，检查时间
                Set<String> PO_TYPES = Set.of("1", "2");

                if (PO_TYPES.contains(s.getPO_TYPE())) {

                    if (s.getSUCCESS()) {
                        ckd.checkPO_TYPE(s);
                    }

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

        for (Pch01 s : list.getList()){

            PchT03PoC byIDsave = savaDao.getByIDsave(s.getPO_NO(),s.getD_NO(), s.getDELIVERY_DATE());
            //PchT03PoC byIDchange = savaDao.getByIDchange(s.getPO_NO(),s.getD_NO());
            if ( byIDsave == null && s.getPO_TYPE().equals("1")){
                //如果是1 则是新规 
                Integer seq = savaDao.getSeq(s.getPO_NO(),s.getD_NO()) + 1;
                PchT03PoC t03 = PchT03PoC.create();

                t03.setDNo(s.getD_NO());                            //设置采购订单编号
                t03.setPoNo(s.getPO_NO());                          //采购订单明细行号
                t03.setSeq(seq);                                    //序号
                t03.setDeliveryDate(s.getDELIVERY_DATE());	        //交货日期
                t03.setQuantity( s.getQUANTITY());				    //交货数量			

                Boolean success = savaDao.insertt03(t03);

                if (success) {
                    list.setErr(false);//有无错误
                    list.setReTxt("insert success");//返回消息
                    s.setTYPE("success");//返回结果
                } else{
                    list.setErr(true);
                    list.setReTxt("insert faild");
                    s.setTYPE("error");//返回结果
                }

            }else if ( 
                //byIDchange != null && 
                s.getPO_TYPE().equals("2")) {
                //如果是2 则是修改
                Integer seq = savaDao.getSeq(s.getPO_NO(),s.getD_NO());

                PchT03PoC t03 = PchT03PoC.create();

                t03.setDNo(s.getD_NO());                        //设置采购订单编号
                t03.setPoNo(s.getPO_NO());                      //采购订单明细行号    
                t03.setSeq(2);                                //序号
                t03.setDeliveryDate(s.getDELIVERY_DATE());	    //交货日期
                t03.setQuantity(s.getQUANTITY());		        //交货数量

                //通过id修改
                Boolean success = savaDao.update(t03);
                if (success) { 
                    list.setErr(false);//有无错误
                    list.setReTxt("update success");//返回消息
                    s.setTYPE("success");//返回结果
                }else{
                    list.setErr(true);
                    list.setReTxt("update faild");
                    s.setTYPE("error");//返回结果
                }
                
            }else if("3".equals(s.getPO_TYPE())){
                   Boolean success = savaDao.delete(s.getPO_NO(), s.getD_NO(), s.getDELIVERY_DATE());

                if (success) {
                    list.setErr(false);//有无错误
                    list.setReTxt("delete success");//返回消息
                    s.setTYPE("success");//返回结果
                }else{
                    list.setErr(true);
                    list.setReTxt("delete faild");
                    s.setTYPE("error");//返回结果
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
