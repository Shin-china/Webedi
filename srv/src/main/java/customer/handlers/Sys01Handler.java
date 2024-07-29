package customer.handlers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSON;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.tableservice.SYS01UserAddUserContext;
import cds.gen.tableservice.SYS01UserDeleteUserContext;
import cds.gen.tableservice.SYS01UserEditUserContext;
import cds.gen.tableservice.TableService_;
import customer.bean.sys.Sys001User;
import customer.service.SysUserService;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Sys01Handler implements EventHandler {
    @Autowired
    SysUserService sysUserService;

    // 新增用户
    @On(event = "SYS01_USER_addUser")
    public void createUser(@RequestBody SYS01UserAddUserContext context) {
        String content = context.getUserJson();
        Sys001User user = JSON.parseObject(content, Sys001User.class);
        // 检查用户是否重复
        sysUserService.checkUserExist(user);
        String userId = sysUserService.insertUser(user);
        System.out.println(context);
        context.setResult("success");
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
}
