package customer.handlers.pch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.reflect.CdsService;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH03SENDEMAILContext;
import cds.gen.tableservice.PCH04SENDEMAILContext;
import cds.gen.tableservice.PchT05AccountDetailExcel;
import cds.gen.tableservice.PchT05AccountDetailExcel_;
import cds.gen.tableservice.TableService_;
import cds.gen.MailBody;
import cds.gen.MailJson;
import customer.service.sys.EmailServiceFun;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch05Handler implements EventHandler {
  /**
   * 
   * 检查抬头 工厂 检查明细
   * 
   * @param context       传入上下文
   * @param d012MoveActHs 传入画面输入值
   */
  @After(entity = PchT05AccountDetailExcel_.CDS_NAME, event = "READ")
  public void beforeReadD012MoveActH(CdsReadEventContext context, Stream<PchT05AccountDetailExcel> pchT05AccountDetailExcel) {
    pchT05AccountDetailExcel.forEach(pchT05AccountDetail -> {
        System.out.println(pchT05AccountDetail.getGrDate());   
        LocalDate grDate = pchT05AccountDetail.getGrDate(); // 获取GR_DATE作为LocalDate对象
        
        if(grDate !=null){
            // 获取年份和月份
            int year = grDate.getYear();
            int month = grDate.getMonthValue();

            // 计算当月最后一天
            LocalDate lastDate;
            if (month == 2) {
                lastDate = LocalDate.of(year, month, grDate.isLeapYear() ? 29 : 28);
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                lastDate = LocalDate.of(year, month, 31);
            } else {
                lastDate = LocalDate.of(year, month, 30);
            }

                // 设置计算出的LASTDATE
                pchT05AccountDetail.setLastdate(lastDate);
        }
       

        // pchT05AccountDetail.setLastdate(LocalDate.now());
      });
    
  }
   
}
