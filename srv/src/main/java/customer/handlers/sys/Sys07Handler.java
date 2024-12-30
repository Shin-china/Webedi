package customer.handlers.sys;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.fastjson.JSON;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.SYS07CheckDATAContext;
import cds.gen.tableservice.SYS05MailtempAddContext;
import cds.gen.tableservice.SYS05MailtempDelContext;
import cds.gen.tableservice.TableService_;
import customer.bean.sys.Sys005Mail;
import customer.bean.sys.Sys07;
import customer.bean.sys.Sys07List;
import customer.service.sys.Sys07Service;
import customer.service.sys.SysMailService;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Sys07Handler implements EventHandler {
    @Autowired
    private Sys07Service shelfService;

    // @Autowired
    // ResourceBundleMessageSource rbms;

    // check数据
    @On(event = "SYS07_CHECK_DATA")
    public void checkData(SYS07CheckDATAContext context) {
        Sys07List list = JSON.parseObject(context.getJson(), Sys07List.class);
        shelfService.sys07DetailsCheck(list);
        context.setResult(JSON.toJSONString(list));

    }

    // // 保存数据
    // @On(event = "SYS07_SAVE_DATA")
    // public void saveData(SYS07SaveDATAContext context) {
    // Sd036List list = JSON.parseObject(context.getSd036Json(), Sd036List.class);
    // shelfService.detailsSave(list);
    // context.setResult(JSON.toJSONString(list));
    // }

    // // 保存明细数据
    // @On(event = "SYS07_SAVE_DATA_L")
    // public void saveDataL(SYS07SaveDataLContext context) {
    // // 直接从上下文中获取参数
    // JSONArray jsonArray = JSONArray.parseArray(context.getSd036Json());
    // // 根据传入的po和po明细修改po明细状态
    // for (int i = 0; i < jsonArray.size(); i++) {
    // JSONObject jsonObject = jsonArray.getJSONObject(i);

    // shelfService.upDateD04(jsonObject);

    // // pchService.setPoStu(po, dNo);
    // }

    // context.setResult("success");
    // }

    // // 删除明细数据
    // @On(event = "SYS07_DELETE_DATA_L")
    // public void deleteDataL(SYS07DeleteDataLContext context) {
    // // 直接从上下文中获取参数
    // JSONArray jsonArray = JSONArray.parseArray(context.getSd036Json());
    // // 根据传入的po和po明细修改po明细状态
    // for (int i = 0; i < jsonArray.size(); i++) {
    // JSONObject jsonObject = jsonArray.getJSONObject(i);
    // String po = jsonObject.getString("po");
    // String ver = jsonObject.getString("ver");
    // String dno = jsonObject.getString("dno");
    // shelfService.deleteD04(jsonObject);
    // // pchService.setPoStu(po, dNo);
    // }

    // context.setResult("success");
    // }

    // // Excel 导出测试
    // @On(event = "SYS07_EXCEL")
    // public void exportExcel(SYS07EXCELContext context) throws IOException {
    // // String content = context.getContent();
    // byte[] bytes = null;
    // Sd036List dataList = JSON.parseObject(context.getSd036Json(),
    // Sd036List.class);

    // // 获取模板文件
    // InputStream inputStream =
    // this.getClass().getClassLoader().getResourceAsStream(UmcConstants.SYS07_TEMPLATE);

    // // Excel写入数据
    // ExcelWriter excelWriter = null;
    // try {
    // ByteArrayOutputStream os = new ByteArrayOutputStream();
    // excelWriter =
    // EasyExcel.write(os).withTemplate(inputStream).inMemory(true).build();
    // WriteSheet writeSheet = EasyExcel.writerSheet().build();

    // // 填充完后需要换行
    // FillConfig fileConfig = FillConfig.builder().forceNewRow(true).build();
    // // 写入数据
    // // excelWriter.write(os, writeSheet)
    // excelWriter.fill(dataList.getList(), fileConfig, writeSheet);
    // // 重新计算公式
    // Workbook workbook =
    // excelWriter.writeContext().writeWorkbookHolder().getWorkbook();
    // // 调用
    // workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

    // excelWriter.finish();

    // // 获取byte字节、
    // bytes = os.toByteArray();
    // } catch (Exception e) {

    // } finally {
    // if (excelWriter != null) {
    // excelWriter.finish();
    // }
    // }

    // context.setResult(bytes);
    // }
}
