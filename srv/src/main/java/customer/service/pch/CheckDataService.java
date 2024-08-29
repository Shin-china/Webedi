package customer.service.pch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import cds.gen.pch.PchT01PoH;
import cds.gen.pch.PchT02PoD;
import cds.gen.pch.PchT03PoC;
import cds.gen.tableservice.Pch01Auth1;
import cds.gen.tableservice.SysT01User;
import cds.gen.tableservice.SysT09User2Plant;
import customer.bean.pch.Pch01;
import customer.dao.pch.DeliverydateDao;
import customer.dao.pch.PodataDao;
import customer.dao.pch.PodndataDao;
import customer.dao.pch.UserdataDao;
import customer.service.Service;




@Component
public class CheckDataService extends Service {

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

    /*
     * 检查字段是否为null 为null则报错
     */
    // public void checkNull(Pch01 s) {
    
    // }

    public void checkPO_TYPE(Pch01 s) {
        int dNO = s.getD_NO();
        // Define a formatter to parse the date strings
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");


        PchT03PoC deliverydateData = deliverydateDao.getByID(s.getPO_NO(),dNO);

        try {
            // Parse both date strings to LocalDate
            LocalDate date1 = LocalDate.parse(deliverydateData.getDeliveryDate(), formatter);
            LocalDate date2 = LocalDate.parse(s.getDELIVERY_DATE(), formatter);
            
            //納期回答ファイルの区分=1:新規、																						
	        //かつ納品日はDBにすでに存在する場合に、下記のエラーメッセージを表示する																						
            if ("1".equals(s.getPO_TYPE())) {

                // Compare the two LocalDate objects
                if (date1.equals(date2)) {
                    s.setMSG_TEXT("新規納期回答なので、同じ納品日はすでに存在します。データをチェックしてください。");
                } else {
                    s.setSUCCESS(true);
                }
            }
            //納期回答ファイルの区分=2:変更、
	        //かつ納品日はDBに存在しない場合に、下記のエラーメッセージを表示する

            if ("2".equals(s.getPO_TYPE())) {
                if (date1.equals(date2)) {
                    s.setMSG_TEXT("変更納期回答なので、同じ納品日の納期回答データは存在しません。データをチェックしてください。");
                } else{
                    s.setSUCCESS(true);
                }
            }

        } catch (DateTimeParseException e) {
            s.setMSG_TEXT("Invalid date format. Please check the dates.");
        }
    }

    public void checkPO_NO(Pch01 s) {
        //PCH_T01_PO_H

        PchT01PoH poData = podataDao.getByID(s.getPO_NO());
        //如果能取到poData 则值正确，为null 则无
        if (poData != null) {
            s.setSUCCESS(true);
        }else{
            s.setMSG_TEXT("購買伝票" + s.getPO_NO() + "が登録されていません。チェックしてください。");
        }

    }

    public void checkD_NO(Pch01 s) {

        int dNO = s.getD_NO();
        PchT02PoD podnData = podndataDao.getByID(s.getPO_NO() ,dNO);
        if (podnData != null) {
            s.setSUCCESS(true);
        }else{
            s.setMSG_TEXT("購買伝票" + s.getPO_NO() + "明細" + s.getD_NO() + "が登録されていません。チェックしてください。");
        }
    
    }
 
    public void check_auth(Pch01 s){

        Pch01Auth1 userData = userdataDao.getUSER_DATA();//取得类型
        SysT09User2Plant plantData = userdataDao.getT09_PLANT();//取得SYS_T09_USER_2_PLANT中的plant id

        if ("1".equals(userData.getUserType())) {

            int dNO = s.getD_NO();
            PchT02PoD podnData = podndataDao.getByID(s.getPO_NO() ,dNO);//取得PCH_T02_PO_D中的plant id

        //两个相等则有权限。
            if (podnData.getPlantId().equals(plantData.getPlantId())) {
                s.setSUCCESS(true);
            }else{
                s.setMSG_TEXT("購買伝票" + s.getPO_NO() + "明細" + s.getD_NO() + "に対して権限がありません。チェックしてください。");
            }  

        }else if("2".equals(userData.getUserType())){
            PchT01PoH poData = podataDao.getByID(s.getPO_NO());

            if (userData.getBpNumber().equals(poData.getSupplier())) {
                s.setSUCCESS(true);   
            }else{
                s.setMSG_TEXT("購買伝票" + s.getPO_NO() + "明細" + s.getD_NO() + "に対して権限がありません。チェックしてください。");
            }  
        }

    }
            
}