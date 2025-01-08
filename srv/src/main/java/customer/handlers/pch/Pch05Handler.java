package customer.handlers.pch;

import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Workbook;
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

import cds.gen.tableservice.PCH05EXCELDOWNLOADContext;
import cds.gen.tableservice.PchT05AccountDetailDisplay3;
import cds.gen.tableservice.PchT05AccountDetailDisplay3_;
import cds.gen.tableservice.PchT05Forexcel;
import cds.gen.tableservice.PchT05Forexcel_;
import cds.gen.tableservice.PchT05AccountDetailExcel1;
import cds.gen.tableservice.PchT05AccountDetailExcel1_;
import cds.gen.tableservice.PchT05AccountDetail1;
import cds.gen.tableservice.PchT05AccountDetail1_;
import cds.gen.tableservice.TableService_;
import cds.gen.MailBody;
import cds.gen.MailJson;
import customer.bean.com.UmcConstants;

import customer.bean.tmpl.test;
import customer.service.pch.PchService;
import customer.service.sys.EmailServiceFun;
import cds.gen.tableservice.PCH05SENDEMAILContext;
import cds.gen.tableservice.PCH05CANCELContext;
import cds.gen.tableservice.PCH05CONFIRMContext;
import customer.bean.tmpl.Pch05List;
import customer.bean.tmpl.Pch05;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Date;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
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
  @After(entity = PchT05AccountDetail1_.CDS_NAME, event = "READ")
  public void afterReadPchT05AccountDetail1(CdsReadEventContext context,
      Stream<PchT05AccountDetail1> pchT05AccountDetail1s) {
    pchT05AccountDetail1s.forEach(PchT05AccountDetail1 -> {
      String currency = PchT05AccountDetail1.getCurrency();

      // 检查 currency 并根据需要设置字段精度
      if ("JPY".equals(currency)) {
        PchT05AccountDetail1.setPriceAmount(scaleToInteger(PchT05AccountDetail1.getPriceAmount()));
        PchT05AccountDetail1.setTaxAmount(scaleToInteger(PchT05AccountDetail1.getTaxAmount()));
        PchT05AccountDetail1.setTotalAmount(scaleToInteger(PchT05AccountDetail1.getTotalAmount()));
      } else if ("USD".equals(currency) || "EUR".equals(currency)) {
        PchT05AccountDetail1.setPriceAmount(scaleToTwoDecimal(PchT05AccountDetail1.getPriceAmount()));
        PchT05AccountDetail1.setTaxAmount(scaleToTwoDecimal(PchT05AccountDetail1.getTaxAmount()));
        PchT05AccountDetail1.setTotalAmount(scaleToTwoDecimal(PchT05AccountDetail1.getTotalAmount()));
      }

      PchT05AccountDetail1.setQuantity(stripTrailingZeros(PchT05AccountDetail1.getQuantity()));
      PchT05AccountDetail1.setTaxRate(stripTrailingZeros(PchT05AccountDetail1.getTaxRate()));

      // 获取 NoDetails 字段并补充前导零
      String noDetails = PchT05AccountDetail1.getNoDetails();
      if (noDetails != null && noDetails.length() >= 10) {
        // 获取前10位
        String prefix = noDetails.substring(0, 10);
    
        // 获取第11位及以后的部分
        String suffix = noDetails.length() > 10 ? noDetails.substring(10) : "";
    
        // 补零到5位，从第11位开始补零
        String paddedSuffix = String.format("%05d", Integer.parseInt(suffix.isEmpty() ? "0" : suffix));
    
        // 拼接前10位和补零后的后缀，确保总长度为15
        String paddedNoDetails = prefix + paddedSuffix;
    
        // 设置回 NoDetails
        PchT05AccountDetail1.setNoDetails(paddedNoDetails);
    }

    });
  }

  /**
   * 
   * 检查抬头 工厂 检查明细
   * 
   * @param context       传入上下文
   * @param d012MoveActHs 传入画面输入值
   */
  @After(entity = PchT05AccountDetailExcel1_.CDS_NAME, event = "READ")
  public void afterReadPchT05AccountDetailExcel1(CdsReadEventContext context, Stream<PchT05AccountDetailExcel1> datas1) {

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

      // 获取 NoDetails 字段并补充前导零
      String noDetails = data1.getNoDetails();
      if (noDetails != null && noDetails.length() >= 10) {
        // 获取前10位
        String prefix = noDetails.substring(0, 10);
    
        // 获取第11位及以后的部分
        String suffix = noDetails.length() > 10 ? noDetails.substring(10) : "";
    
        // 补零到5位，从第11位开始补零
        String paddedSuffix = String.format("%05d", Integer.parseInt(suffix.isEmpty() ? "0" : suffix));
    
        // 拼接前10位和补零后的后缀，确保总长度为15
        String paddedNoDetails = prefix + paddedSuffix;
    
        // 设置回 NoDetails
        data1.setNoDetails(paddedNoDetails);
    }

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
   * 
   * 检查抬头 工厂 检查明细
   * 
   * @param context       传入上下文
   * @param d012MoveActHs 传入画面输入值
   */
  @After(entity = PchT05AccountDetailDisplay3_.CDS_NAME, event = "READ")
  public void afterReadPchT05AccountDetailDisplay3(CdsReadEventContext context,
      Stream<PchT05AccountDetailDisplay3> datas2) {

    datas2.forEach(data2 -> {

      data2.setCalc10PriceAmount(stripTrailingZeros(data2.getCalc10PriceAmount()));
      data2.setCalc8PriceAmount(stripTrailingZeros(data2.getCalc8PriceAmount()));
      data2.setSapTaxAmount10(stripTrailingZeros(data2.getSapTaxAmount10()));
      data2.setSapTaxAmount8(stripTrailingZeros(data2.getSapTaxAmount8()));
      data2.setRecalcPriceAmount10(stripTrailingZeros(data2.getRecalcPriceAmount10()));
      data2.setRecalcPriceAmount8(stripTrailingZeros(data2.getRecalcPriceAmount8()));
      data2.setDiffTaxAmount10(stripTrailingZeros(data2.getDiffTaxAmount10()));
      data2.setDiffTaxAmount8(stripTrailingZeros(data2.getDiffTaxAmount8()));
      data2.setTotal10TaxIncludedAmount(stripTrailingZeros(data2.getTotal10TaxIncludedAmount()));
      data2.setTotal8TaxIncludedAmount(stripTrailingZeros(data2.getTotal8TaxIncludedAmount()));

    //   // 对每个字段进行处理
    // data2.setCalc10PriceAmount(handleZeroValue(data2.getCalc10PriceAmount()));
    // data2.setCalc8PriceAmount(handleZeroValue(data2.getCalc8PriceAmount()));
    // data2.setSapTaxAmount10(handleZeroValue(data2.getSapTaxAmount10()));
    // data2.setSapTaxAmount8(handleZeroValue(data2.getSapTaxAmount8()));
    // data2.setRecalcPriceAmount10(handleZeroValue(data2.getRecalcPriceAmount10()));
    // data2.setRecalcPriceAmount8(handleZeroValue(data2.getRecalcPriceAmount8()));
    // data2.setDiffTaxAmount10(handleZeroValue(data2.getDiffTaxAmount10()));
    // data2.setDiffTaxAmount8(handleZeroValue(data2.getDiffTaxAmount8()));
    // data2.setTotal10TaxIncludedAmount(handleZeroValue(data2.getTotal10TaxIncludedAmount()));
    // data2.setTotal8TaxIncludedAmount(handleZeroValue(data2.getTotal8TaxIncludedAmount()));

    });
  }

//   /**
//  * 处理字段值，若为 0 则返回 null，否则返回原值
//  *
//  * @param value 输入值
//  * @return 处理后的值
//  */
// private BigDecimal handleZeroValue(BigDecimal value) {
//   return (value != null && value.compareTo(BigDecimal.ZERO) == 0) ? null : value;
// }


  /**
   * PCH_T05_ACCOUNT_DETAIL_DISPLAY3
   * 检查抬头 工厂 检查明细
   * 
   * @param context       传入上下文
   * @param d012MoveActHs 传入画面输入值
   */
  @After(entity = PchT05Forexcel_.CDS_NAME, event = "READ")
  public void afterReadPchT05Forexcel(CdsReadEventContext context,
      Stream<PchT05Forexcel> datas) {

    int[] a = new int[1];
    a[0] = 0;
    datas.forEach(data -> {
      a[0] = a[0] + 1;
      data.setInvoiceid(a[0]);
      // 检查 currency 并根据需要设置字段精度

      data.setDiffTaxAmount(stripTrailingZeros(data.getDiffTaxAmount()));
      data.setTaxBaseAmount(stripTrailingZeros(data.getTaxBaseAmount()));
      // data.setLastdate(data.getLastdate());

      // 获取当前日期
      LocalDate currentDate = LocalDate.now();
      // 计算当月的最后一天
      LocalDate lastDayOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
      // 将格式化的日期存入变量
      data.setLastdate(lastDayOfMonth);

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

  /**
   * 
   * @param context
   */
  @On(event = "PCH05_CONFIRM")
  public void confirm(PCH05CONFIRMContext context) {

    String supp = context.getParms();
    JSONArray jsonArray = JSONArray.parseArray(supp);
    for (int i = 0; i < jsonArray.size(); i++) {
      String object = (String) jsonArray.get(i);

      System.out.println(object);
      pchService.setinvdateconfirm(object);
    }

    context.setResult("success");

  }

  /**
   * 
   * @param context
   */
  @On(event = "PCH05_CANCEL")
  public void confirm(PCH05CANCELContext context) {

    String supp = context.getParms();
    JSONArray jsonArray = JSONArray.parseArray(supp);
    for (int i = 0; i < jsonArray.size(); i++) {
      String object = (String) jsonArray.get(i);

      System.out.println(object);
      pchService.setinvdatecancel(object);
    }

    context.setResult("success");

  }

  // Excel 导出测试
  @On(event = "PCH05_EXCELDOWNLOAD")
  public void exportExcel(PCH05EXCELDOWNLOADContext context) throws IOException {
    // String content = context.getContent();
    byte[] bytes = null;
    Pch05List dataList = JSON.parseObject(context.getParms(), Pch05List.class);

    // 检查 dataList 是否为空，并且确保它的 list 字段存在
    if (dataList != null && dataList.getList() != null) {
      // 遍历 Pch05List 中的每一项（每个 item 为 Pch05List 的一个记录）
      for (Pch05 item : dataList.getList()) {
        //Add by stanley 20250103
        BigDecimal bigDiffTax = new BigDecimal(item.getDIFF_TAX_AMOUNT());
        String absDIFF_TAX_AMOUNT1 = bigDiffTax.abs().toString();
        BigDecimal bigTax_Base_Amount = new BigDecimal(item.getTAX_BASE_AMOUNT());
        String absTAX_BASE_AMOUNT = bigTax_Base_Amount.abs().toString();
        //End
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        item.setCOMPANY_CODE1(item.getCOMPANY_CODE()); // 赋值 Company_Code
        item.setLASTDATE1(item.getLASTDATE()); // 将 LASTDATE 的值赋给 LASTDATE1
        item.setLASTDATE2(item.getLASTDATE()); // 将 LASTDATE 的值赋给 LASTDATE2
        item.setDIFF_TAX_AMOUNT1(absDIFF_TAX_AMOUNT1); // 赋值 DIFF_TAX_AMOUNT1
        item.setTAX_BASE_AMOUNT(absTAX_BASE_AMOUNT);//ADD BY STANLEY 20250103

      }
    }

    // 获取模板文件
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/消費税差額差処理.xlsx");

    // Excel写入数据
    ExcelWriter excelWriter = null;
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      excelWriter = EasyExcel.write(os).withTemplate(inputStream).inMemory(true).build();
      WriteSheet writeSheet = EasyExcel.writerSheet().build();

      // 填充完后需要换行
      FillConfig fileConfig = FillConfig.builder().forceNewRow(true).build();
      // 写入数据
      // excelWriter.write(os, writeSheet)
      excelWriter.fill(dataList.getList(), fileConfig, writeSheet);
      // 重新计算公式
      Workbook workbook = excelWriter.writeContext().writeWorkbookHolder().getWorkbook();
      // 调用
      workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

      excelWriter.finish();

      // 获取byte字节、
      bytes = os.toByteArray();
    } catch (Exception e) {

    } finally {
      if (excelWriter != null) {
        excelWriter.finish();
      }
    }

    context.setResult(bytes);
  }

}