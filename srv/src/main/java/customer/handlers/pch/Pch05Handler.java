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

import cds.gen.tableservice.PchT05AccountDetailDisplay3;
import cds.gen.tableservice.PchT05AccountDetailDisplay3_;
import cds.gen.tableservice.PchT05AccountDetailExcel;
import cds.gen.tableservice.PchT05AccountDetailExcel_;
import cds.gen.tableservice.PchT05AccountDetail;
import cds.gen.tableservice.PchT05AccountDetail_;
import cds.gen.tableservice.TableService_;
import cds.gen.MailBody;
import cds.gen.MailJson;
import customer.service.pch.PchService;
import customer.service.sys.EmailServiceFun;
import cds.gen.tableservice.PCH05SENDEMAILContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

  @Autowired
  PchService pchService;

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

      pchT05AccountDetail.setQuantity(stripTrailingZeros(pchT05AccountDetail.getQuantity()));
      pchT05AccountDetail.setTaxRate(stripTrailingZeros(pchT05AccountDetail.getTaxRate()));

    });
  }

  /**
   * 
   * 检查抬头 工厂 检查明细
   * 
   * @param context       传入上下文
   * @param d012MoveActHs 传入画面输入值
   */
  @After(entity = PchT05AccountDetailExcel_.CDS_NAME, event = "READ")
  public void afterReadPchT05AccountDetailExcel(CdsReadEventContext context, Stream<PchT05AccountDetailExcel> datas1) {

    datas1.forEach(data1 -> {

      data1.setQuantity(stripTrailingZeros(data1.getQuantity()));
      data1.setTaxRate(stripTrailingZeros(data1.getTaxRate()));
      data1.setUnitPrice(stripTrailingZeros(data1.getUnitPrice()));
      data1.setPriceAmount(stripTrailingZeros(data1.getPriceAmount()));
      data1.setPriceAmountTotal(stripTrailingZeros(data1.getPriceAmountTotal()));
      data1.setTotalAmount8Total(stripTrailingZeros(data1.getTotalAmount8Total()));
      data1.setTotalAmount10Total(stripTrailingZeros(data1.getTotalAmount10Total()));
      data1.setSapTaxAmount8Total(stripTrailingZeros(data1.getSapTaxAmount8Total()));
      data1.setSapTaxAmount10Total(stripTrailingZeros(data1.getSapTaxAmount10Total()));
      data1.setCalc8PriceAmountTotal(stripTrailingZeros(data1.getCalc8PriceAmountTotal()));
      data1.setCalc10PriceAmountTotal(stripTrailingZeros(data1.getCalc10PriceAmountTotal()));
      data1.setTotalTotalAmount(stripTrailingZeros(data1.getTotalTotalAmount()));
      data1.setTotalTaxAmount(stripTrailingZeros(data1.getTotalTaxAmount()));

    });
  }

  /**
   * 处理金额字段，去除尾随零。
   * 
   * @param amount 金额
   * @return 返回处理后的金额，去除尾随零
   */
  private BigDecimal stripTrailingZeros(BigDecimal amount) {
    if (amount != null) {
      return amount.stripTrailingZeros(); // 去除尾随零
    }
    return BigDecimal.ZERO;
  }

  /**
   * PCH_T05_ACCOUNT_DETAIL_DISPLAY3
   * 检查抬头 工厂 检查明细
   * 
   * @param context       传入上下文
   * @param d012MoveActHs 传入画面输入值
   */
  @After(entity = PchT05AccountDetailDisplay3_.CDS_NAME, event = "READ")
  public void afterReadPchT05AccountDetailDisplay3(CdsReadEventContext context,
      Stream<PchT05AccountDetailDisplay3> datas) {

    int[] a = new int[1];
    a[0] = 0;
    datas.forEach(data -> {
      a[0] = a[0] + 1;
      data.setInvoiceid(a[0]);
      // 检查 currency 并根据需要设置字段精度

      data.setCalc10PriceAmount(stripTrailingZeros(data.getCalc10PriceAmount()));
      data.setCalc8PriceAmount(stripTrailingZeros(data.getCalc8PriceAmount()));
      data.setSapTaxAmount10(stripTrailingZeros(data.getSapTaxAmount10()));
      data.setSapTaxAmount8(stripTrailingZeros(data.getSapTaxAmount8()));
      data.setRecalcPriceAmount10(stripTrailingZeros(data.getRecalcPriceAmount10()));
      data.setRecalcPriceAmount8(stripTrailingZeros(data.getRecalcPriceAmount8()));
      data.setDiffTaxAmount10(stripTrailingZeros(data.getDiffTaxAmount10()));
      data.setDiffTaxAmount8(stripTrailingZeros(data.getDiffTaxAmount8()));
      data.setTotal10TaxIncludedAmount(stripTrailingZeros(data.getTotal10TaxIncludedAmount()));
      data.setTotal8TaxIncludedAmount(stripTrailingZeros(data.getTotal8TaxIncludedAmount()));

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

  /**
   * 
   * @param context
   */
  @On(event = "PCH05_SENDEMAIL")
  public void sendemail(PCH05SENDEMAILContext context) {

    String suppli = context.getParms();
    pchService.setSendflag(suppli);

    context.setResult("success");

  }

}