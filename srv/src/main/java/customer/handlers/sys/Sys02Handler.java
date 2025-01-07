package customer.handlers.sys;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.handler.EventHandler;
import cds.gen.tableservice.SYS02RoleAddRoleContext;
import cds.gen.tableservice.SYS02RoleEditRoleContext;
import cds.gen.tableservice.TableService_;
import customer.comm.tool.JsonUtils;
import customer.service.sys.SysRoleService;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Sys02Handler implements EventHandler{
    @Autowired
    private SysRoleService roleService;

    // 新增角色
    @On(event = "SYS02_ROLE_addRole")
    public void addRole(SYS02RoleAddRoleContext context) {
        String roleId = roleService.addRole(context);
        Map<String, Object> infoMap = new HashMap<String, Object>();
        if (roleId!=null){
            infoMap.put("id", roleId);
            infoMap.put("success", "success");
        }else{
            infoMap.put("id", "error");
            infoMap.put("error", "error");
        }
       
        String jsonStr = JsonUtils.objectToJson(infoMap);
        context.setResult(jsonStr);
    }

    @On(event = "SYS02_ROLE_editRole")
    public void editRole(SYS02RoleEditRoleContext context) {
        roleService.editRole(context);
        context.setResult("success");
    }
}
