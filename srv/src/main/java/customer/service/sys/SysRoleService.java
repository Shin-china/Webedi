package customer.service.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import cds.gen.sys.T02Role;
import cds.gen.tableservice.SYS02RoleAddRoleContext;
import cds.gen.tableservice.SYS02RoleEditRoleContext;
import customer.comm.tool.DateTools;
import customer.comm.tool.JsonUtils;
import customer.dao.sys.Role2AuthDao;
import customer.dao.sys.RoleDao;

@Component
public class SysRoleService {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private Role2AuthDao role2AuthDao;
    public String addRole(SYS02RoleAddRoleContext context) {
        String jsonString = context.getRoleJson();
        JsonNode roleJson = JsonUtils.strToJson(jsonString);
        // String id = roleJson.findValue("id").asText();
        String roleCode = roleJson.findValue("roleCode").asText();
        String roleName = roleJson.findValue("roleName").asText();
        JsonNode value = roleJson.findValue("authList");
        //判断roleCode不能重复
        T02Role t02Role =  roleDao.getRoleByRodeCode(roleCode);
        String id;
        if (t02Role==null){
            List<String> list = new ArrayList<>();
            for (JsonNode node : value) {
                list.add(node.asText());
            }
            id = UUID.randomUUID().toString();
            // 根据角色id 删除 Role2Auth
            role2AuthDao.deleteByRoleId(id);
            T02Role role = T02Role.create();
            role.setId(id);
            role.setRoleCode(roleCode);
            role.setRoleName(roleName);
            role.setCdBy(roleDao.getUserId());
            role.setCdTime(DateTools.getInstantNow());
            roleDao.insertRole(role);
            role2AuthDao.addRole2Auth(id, list);
        }else{
            id = null;
        }
           
        return id;
    }

    public void editRole(SYS02RoleEditRoleContext context) {
        String jsonString = context.getRoleJson();
        JsonNode roleJson = JsonUtils.strToJson(jsonString);
        String id = roleJson.findValue("id").asText();
        // ROLE CODE 不做更新处理
        // String roleCode = roleJson.findValue("roleCode").asText();
        String roleName = roleJson.findValue("roleName").asText();
        JsonNode value = roleJson.findValue("authList");
        List<String> list = new ArrayList<>();
        for (JsonNode node : value) {
            list.add(node.asText());
        }

        T02Role role = roleDao.getRoleById(id);
        if (role != null) {
            role2AuthDao.deleteByRoleId(id);
            role.setRoleName(roleName);
            role.setUpBy(roleDao.getUserId());
            role.setUpTime(DateTools.getInstantNow());
            roleDao.updateRole(role);
            role2AuthDao.addRole2Auth(id, list);
        }
    }
}
