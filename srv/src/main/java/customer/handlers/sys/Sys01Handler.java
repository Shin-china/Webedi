package customer.handlers.sys;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.fastjson.JSON;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.EXCELTESTContext;
import cds.gen.tableservice.SYS01UserAddUserContext;
import cds.gen.tableservice.SYS01UserDeleteUserContext;
import cds.gen.tableservice.SYS01UserEditUserContext;
import cds.gen.tableservice.TableService_;
import customer.bean.sys.Sys001User;
import customer.bean.tmpl.test;
import customer.service.sys.SysUserService;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Sys01Handler implements EventHandler {
    @Autowired
    SysUserService sysUserService;

    // 新增用户
    @On(event = "SYS01_USER_addUser")
    public void createUser(SYS01UserAddUserContext context) {
        String content = context.getUserJson();
        Sys001User user = JSON.parseObject(content, Sys001User.class);
        // 检查用户是否重复
        sysUserService.checkUserExist(user);
        String userId = sysUserService.insertUser(user);
        context.setResult(userId);
    }

    // 编辑用户
    @On(event = "SYS01_USER_editUser")
    public void editUser(SYS01UserEditUserContext context) {
        String jsonStr = context.getUserJson();
        Sys001User user = JSON.parseObject(jsonStr, Sys001User.class);
        sysUserService.updateUser(user);
        context.setResult("success");

    }

    // 删除用户
    @On(event = "SYS01_USER_deleteUser")
    public void deleteUser(SYS01UserDeleteUserContext context) {
        String jsonStr = context.getUserJson();
        Sys001User user = JSON.parseObject(jsonStr, Sys001User.class);
        sysUserService.deleteUser(user);
        context.setResult("success");
    }

    // Excel 导出测试
    @On(event = "EXCEL_TEST")
    public void exportExcel(EXCELTESTContext context) throws IOException {
        String content = context.getContent();
        byte[] bytes = null;
        List<test> dataList = new ArrayList<>();
        test exl = JSON.parseObject(content, test.class);
        test temp = new test();
        temp.setId(exl.getId());
        temp.setMat(exl.getMat());
        temp.setQty(exl.getQty());
        temp.setUnp(exl.getUnp());
        dataList.add(temp);

        // 获取模板文件
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/test.xlsx");

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
            excelWriter.fill(dataList, fileConfig, writeSheet);
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
