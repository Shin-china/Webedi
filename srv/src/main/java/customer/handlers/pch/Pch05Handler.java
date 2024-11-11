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

import cds.gen.tableservice.PchT05AccountDetailExcel;
import cds.gen.tableservice.PchT05AccountDetailExcel_;
import cds.gen.tableservice.PchT05AccountDetail;
import cds.gen.tableservice.PchT05AccountDetail_;
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
import java.math.BigDecimal;

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
  @After(entity = PchT05AccountDetail_.CDS_NAME, event = "READ")
  public void afterReadPchT05AccountDetail(CdsReadEventContext context,
      Stream<PchT05AccountDetail> pchT05AccountDetails) {
    pchT05AccountDetails.forEach(pchT05AccountDetail -> {
      String currency = pchT05AccountDetail.getCurrency();

      // 检查 currency 并根据需要设置字段精度
      if ("JPY".equals(currency)) {
        pchT05AccountDetail.setPriceAmount(scaleToInteger(pchT05AccountDetail.getPriceAmount()));
        pchT05AccountDetail.setTaxAmount(scaleToInteger(pchT05AccountDetail.getTaxAmount()));
        pchT05AccountDetail.setTotalAmount(scaleToInteger(pchT05AccountDetail.getTotalAmount()));
      } else if ("USD".equals(currency) || "EUR".equals(currency)) {
        pchT05AccountDetail.setPriceAmount(scaleToTwoDecimal(pchT05AccountDetail.getUnitPrice()));
        pchT05AccountDetail.setTaxAmount(scaleToTwoDecimal(pchT05AccountDetail.getTaxAmount()));
        pchT05AccountDetail.setTotalAmount(scaleToTwoDecimal(pchT05AccountDetail.getTotalAmount()));
      }
    });
  }
  /**
   * PCH_T05_ACCOUNT_DETAIL_EXCEL
   * 检查抬头 工厂 检查明细
   * 
   * @param context       传入上下文
   * @param d012MoveActHs 传入画面输入值
   */
  @After(entity = PchT05AccountDetailExcel_.CDS_NAME, event = "READ")
  public void afterReadPchT05AccountDetailExcel(CdsReadEventContext context,
      Stream<PchT05AccountDetailExcel> datas) {


        int[] a = new int[1];
        a[0]= 0; 
        datas.forEach(data -> {
          a[0] = a[0]+1;
          data.setInvoiceid(a[0]);
      // 检查 currency 并根据需要设置字段精度
     
    });
  }
  // 保留整数
  private BigDecimal scaleToInteger(BigDecimal value) {
    return (value != null) ? value.setScale(0, BigDecimal.ROUND_DOWN) : null;
  }

  // 保留两位小数
  private BigDecimal scaleToTwoDecimal(BigDecimal value) {
    return (value != null) ? value.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
  }

}