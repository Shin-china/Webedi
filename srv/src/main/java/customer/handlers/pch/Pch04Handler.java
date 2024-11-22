package customer.handlers.pch;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH04SENDEMAILContext;
import cds.gen.tableservice.PCH04EXCELDOWNLOADContext;
import cds.gen.tableservice.PchT04PaymentSumHj6;
import cds.gen.tableservice.PchT04PaymentSumHj6_;
import cds.gen.tableservice.PchT04PaymentUnit;
import cds.gen.tableservice.PchT04PaymentUnit_;
import cds.gen.tableservice.TableService_;
import cds.gen.MailBody;
import cds.gen.MailJson;

import customer.bean.tmpl.test;
import customer.service.sys.EmailServiceFun;
import customer.tool.UWebConstants;
import customer.bean.tmpl.Pch04;
import customer.bean.tmpl.Pch04List;
import customer.bean.tmpl.Pch05;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch04Handler implements EventHandler {

    @Autowired
    private EmailServiceFun emailServiceFun;

    @On(event = "PCH04_SENDEMAIL")
    public void sendEmail(PCH04SENDEMAILContext context) {
        // 直接从上下文中获取参数
        String emailJsonParam = (String) context.get("parms"); // 根据上下文对象获取数据

        // 解析 JSON 参数为 List<MailParam> 对象
        List<MailParam> emailParams = parseParams(emailJsonParam);

        // 创建 MailJson 的集合
        Collection<MailJson> mailJsonList = new ArrayList<>();

        // 遍历每个邮件参数，并创建 MailJson 对象
        for (MailParam param : emailParams) {
            MailJson mailJson = MailJson.create(); // 使用静态方法创建 MailJson 对象

            // 设置邮件参数
            mailJson.setMailTo(param.getMailTo()); // 设置收件人邮箱
            mailJson.setTemplateId(param.getTemplateId()); // 设置模板ID
            mailJson.setMailBody(createMailBody(param)); // 设置邮件内容（MailBody）

            // 添加到邮件列表
            mailJsonList.add(mailJson);
        }

         // 调用邮件发送服务
         try {
            emailServiceFun.sendEmailFun(mailJsonList);
            // 设置操作结果
            context.setResult(JSON.toJSONString("メールは送付されました"));
        } catch (Exception e) {
            // 处理发送邮件的异常

        }
    }

    // 创建 MailBody 的集合
    private Collection<MailBody> createMailBody(MailParam param) {
        Collection<MailBody> bodies = new ArrayList<>();
        MailBody body = MailBody.create();
        body.setObject("content"); // 根据具体需求设置
        body.setValue(param.getEmailContent()); // 使用参数内容
        bodies.add(body);
        return bodies;
    }

    // 解析传入的 JSON 参数为 List<MailParam>
    private List<MailParam> parseParams(String parms) {
        Gson gson = new Gson();
        Type mailParamListType = new TypeToken<List<MailParam>>() {
        }.getType();
        return gson.fromJson(parms, mailParamListType);
    }

    // 定义一个内部类，用于接收 JSON 数据
    private static class MailParam {
        private String INV_NO;
        private String SUPPLIER_DESCRIPTION;
        private String EMAIL_CONTENT;
        private String TEMPLATE_ID; // 添加模板ID字段
        private String MAIL_TO; // 添加收件人邮箱字段

        public String getInvNo() {
            return INV_NO;
        }

        public String getSupplierDescription() {
            return SUPPLIER_DESCRIPTION;
        }

        public String getEmailContent() {
            return EMAIL_CONTENT;
        }

        public String getTemplateId() {
            return TEMPLATE_ID;
        }

        public String getMailTo() {
            return MAIL_TO;
        }
    }

    /**
     * 
     * 检查抬头 工厂 检查明细
     * 
     * @param context       传入上下文
     * @param d012MoveActHs 传入画面输入值
     */
    @After(entity = PchT04PaymentSumHj6_.CDS_NAME, event = "READ")
    public void afterReadPchT04PaymentSumHj6(CdsReadEventContext context, Stream<PchT04PaymentSumHj6> datas1) {

        datas1.forEach(data1 -> {

            data1.setTotalPriceAmount8(stripTrailingZeros(data1.getTotalPriceAmount8()));
            data1.setConsumptionTax8(stripTrailingZeros(data1.getConsumptionTax8()));
            data1.setTotalPaymentAmount8End(stripTrailingZeros(data1.getTotalPaymentAmount8End()));
            data1.setTotalPriceAmount10(stripTrailingZeros(data1.getTotalPriceAmount10()));
            data1.setConsumptionTax10(stripTrailingZeros(data1.getConsumptionTax10()));
            data1.setTotalPaymentAmount10End(stripTrailingZeros(data1.getTotalPaymentAmount10End()));
            data1.setTotalPriceAmountNot(stripTrailingZeros(data1.getTotalPriceAmountNot()));
            data1.setTotalPaymentAmountFinal(stripTrailingZeros(data1.getTotalPaymentAmountFinal()));
            data1.setQuantity(stripTrailingZeros(data1.getQuantity()));
            data1.setUnitPriceInYen(stripTrailingZeros(data1.getUnitPriceInYen()));
            data1.setBaseAmountExcludingTax(stripTrailingZeros(data1.getBaseAmountExcludingTax()));
            data1.setTaxRate(stripTrailingZeros(data1.getTaxRate()));

             // 获取 NoDetails 字段并补充前导零
             String noDetails = data1.getNoDetails(); // 使用实例调用非静态方法
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
     * 
     * 检查抬头 工厂 检查明细
     * 
     * @param context       传入上下文
     * @param d012MoveActHs 传入画面输入值
     */
    @After(entity = PchT04PaymentUnit_.CDS_NAME, event = "READ")
    public void afterReadPchT04PaymentUnit(CdsReadEventContext context, Stream<PchT04PaymentUnit> datas2) {

        datas2.forEach(data2 -> {

            data2.setTaxRate(stripTrailingZeros(data2.getTaxRate()));
            data2.setUnitPrice(stripTrailingZeros(data2.getUnitPrice()));
            data2.setExchange(stripTrailingZeros(data2.getExchange()));
            data2.setQuantity(stripTrailingZeros(data2.getQuantity()));
            data2.setPriceAmount(stripTrailingZeros(data2.getPriceAmount()));
            data2.setTotalAmount(stripTrailingZeros(data2.getTotalAmount()));
            data2.setUnitPriceInYen(stripTrailingZeros(data2.getUnitPriceInYen()));
            data2.setTotalAmountInYen(stripTrailingZeros(data2.getTotalAmountInYen()));
            data2.setPriceAmount10(stripTrailingZeros(data2.getPriceAmount10()));
            data2.setPriceAmount8(stripTrailingZeros(data2.getPriceAmount8()));

            // 获取 NoDetails 字段并补充前导零
            String noDetails = data2.getNoDetails(); // 使用实例调用非静态方法
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
                data2.setNoDetails(paddedNoDetails);
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
    @After(entity = PchT04PaymentSumHj6_.CDS_NAME, event = "READ")
    public void afterReadPchT04PaymentSumHj6(CdsReadEventContext context, Stream<PchT04PaymentSumHj6> datas3) {

        datas3.forEach(data3 -> {

            // 获取 NoDetails 字段并补充前导零
            String noDetails = data3.getNoDetails(); // 使用实例调用非静态方法
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
                data3.setNoDetails(paddedNoDetails);
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

    // Excel 导出测试
    @On(event = "PCH04_EXCELDOWNLOAD")
    public void exportExcel(PCH04EXCELDOWNLOADContext context) throws IOException {
        // String content = context.getContent();
        byte[] bytes = null;
        Pch04List dataList = JSON.parseObject(context.getParms(), Pch04List.class);

        // 检查 dataList 是否为空，并且确保它的 list 字段存在
        if (dataList != null && dataList.getList() != null) {
            // 遍历 Pch04List 中的每一项（每个 item 为 Pch04List 的一个记录）
            for (Pch04 item : dataList.getList()) {

                String grDateString = item.getGR_DATE();
                String invBaseDateString = item.getINV_BASE_DATE();

                // 获取 NoDetails 字段并补充前导零
                String noDetails = item.getNO_DETAILS(); // 使用实例调用非静态方法
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
                    item.setNO_DETAILS(paddedNoDetails);
                }

                // 如果 GR_DATE 存在，解析并格式化它
                if (grDateString != null && !grDateString.isEmpty()) {
                    try {
                        // 使用 OffsetDateTime 解析带时区的日期字符串
                        OffsetDateTime grDate = OffsetDateTime.parse(grDateString); // 解析为 OffsetDateTime
                        String formattedGrDate = grDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); // 格式化为
                                                                                                           // yyyy/MM/dd
                        item.setGR_DATE(formattedGrDate); // 设置格式化后的日期
                    } catch (Exception e) {
                        e.printStackTrace(); // 如果解析失败，打印错误信息
                    }
                }

                // 如果 INV_BASE_DATE 存在，解析并格式化它
                if (invBaseDateString != null && !invBaseDateString.isEmpty()) {
                    try {
                        // 使用 OffsetDateTime 解析带时区的日期字符串
                        OffsetDateTime invBaseDate = OffsetDateTime.parse(invBaseDateString); // 解析为 OffsetDateTime
                        String formattedInvBaseDate = invBaseDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); // 格式化为
                                                                                                                     // yyyy/MM/dd
                        item.setINV_BASE_DATE(formattedInvBaseDate); // 设置格式化后的日期
                    } catch (Exception e) {
                        e.printStackTrace(); // 如果解析失败，打印错误信息
                    }
                }
            }

        }
        ArrayList<Pch04> list = dataList.getList();
        list.add(new Pch04());
        list.add(new Pch04());
        list.add(new Pch04());
        dataList.setList(list);

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(UWebConstants.PCH04_TEP_PATH);

        // Excel写入数据
        ExcelWriter excelWriter = null;
        try {

            // 数据总行数变量
            int dataRowCount = dataList.getList().size() - 3;
            // Add by stanley
            CellWriteHandler cellWriteHandler = new CellWriteHandler() {
                @Override
                public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                        List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex,
                        Boolean isHead) {
                    if (!isHead) {

                        int targetRowIndex = dataRowCount + 2; // 数据行数 + 2

                        if (cell.getColumnIndex() == 3 && relativeRowIndex == targetRowIndex) {
                            cell.setCellValue(UWebConstants.PCH04_TEP_EXCEL_MSG);
                        }

                    }
                }
            };
            // End Add
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            excelWriter = EasyExcel.write(os).withTemplate(inputStream).inMemory(true).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(cellWriteHandler).build();
            // map组合数据
            HashMap<String, String> otherData = new HashMap<>();
            otherData.put("TOTAL_PRICE_AMOUNT_8", dataList.getList().get(0).getTOTAL_PRICE_AMOUNT_8());
            otherData.put("CONSUMPTION_TAX_8", dataList.getList().get(0).getCONSUMPTION_TAX_8());
            otherData.put("TOTAL_PAYMENT_AMOUNT_8_END", dataList.getList().get(0).getTOTAL_PAYMENT_AMOUNT_8_END());
            otherData.put("TOTAL_PRICE_AMOUNT_10", dataList.getList().get(0).getTOTAL_PRICE_AMOUNT_10());
            otherData.put("CONSUMPTION_TAX_10", dataList.getList().get(0).getCONSUMPTION_TAX_10());
            otherData.put("TOTAL_PAYMENT_AMOUNT_10_END", dataList.getList().get(0).getTOTAL_PAYMENT_AMOUNT_10_END());
            otherData.put("NON_APPLICABLE_AMOUNT", dataList.getList().get(0).getNON_APPLICABLE_AMOUNT());
            otherData.put("TOTAL_PAYMENT_AMOUNT_FINAL", dataList.getList().get(0).getTOTAL_PAYMENT_AMOUNT_FINAL());
            otherData.put("INV_MONTH_FORMATTED", dataList.getList().get(0).getINV_MONTH_FORMATTED());
            otherData.put("SUPPLIER_DESCRIPTION", dataList.getList().get(0).getSUPPLIER_DESCRIPTION());
            otherData.put("LOG_NO", dataList.getList().get(0).getLOG_NO());
            otherData.put("Company_Name", dataList.getList().get(0).getCompany_Name());
            otherData.put("CURRENT_DAY", dataList.getList().get(0).getCURRENT_DAY());

            // 填充完后需要换行
            FillConfig fileConfig = FillConfig.builder().forceNewRow(true).build();
            // 写入数据

            // excelWriter.write(os, writeSheet)
            excelWriter.fill(dataList.getList(), fileConfig, writeSheet);

            // 重新计算公式
            Workbook workbook = excelWriter.writeContext().writeWorkbookHolder().getWorkbook();

            // 调用
            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
            // 填充
            excelWriter.fill(otherData, writeSheet);
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

    private void _relModel(String pch04TepPath, int dataSize) {

        List<String> data = new ArrayList<>();
        data.add("支払通知エクセルファイル");

        EasyExcel.write(pch04TepPath).sheet("支払通知エクセルファイル");
        // .doWrite(data,writeSheet->{

        // writeSheet.setFixedIndexes(0,0);//将A1作为
        // return writeSheet;
        // });
    }

    private void _setModel(String pch04TepPath, int dataSize) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method '_setModel'");
    }
}
