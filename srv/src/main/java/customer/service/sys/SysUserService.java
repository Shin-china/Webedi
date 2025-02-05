package customer.service.sys;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ErrorStatus;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.messages.Messages;

import cds.gen.sys.T01User;
import cds.gen.sys.T04User2Role;
import cds.gen.sys.T09User2Plant;
import cds.gen.sys.T14User2Bp;
import cds.gen.tableservice.SysT01User_;
import customer.bean.sys.Sys001User;
import customer.dao.sys.User2BpDao;
import customer.dao.sys.User2PlantDao;
import customer.dao.sys.User2RoleDao;
import customer.dao.sys.UserDao;
import customer.tool.UniqueIDTool;

import com.sap.cds.services.ErrorStatuses;

@Component
public class SysUserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    Messages messages;

    @Autowired
    User2RoleDao user2RoleDao;

    @Autowired
    User2PlantDao user2PlantDao;

    @Autowired
    User2BpDao user2BpDao;

    /*
     * Insert User
     */
    public String insertUser(Sys001User user) {
        T01User o = T01User.create(UniqueIDTool.getUUID());
        o.setUserId(user.getUserId());
        o.setUserType(user.getUserType());
        o.setUserName(user.getUserName());
        o.setBpNumber(user.getBpNumber());
        o.setValidDateFrom(user.getValidDateFrom());
        o.setValidDateTo(user.getValidDateTo());
        o.setCdTime(Instant.now());
        System.out.println(o);

        String userID = userDao.insert(o);

        // 插入用户角色
        user2RoleDao.deleteUser2Role(o.getId());
        for (String roleId : user.getRoles()) {
            T04User2Role role = T04User2Role.create();
            role.setRoleId(roleId);
            role.setUserId(o.getId());
            user2RoleDao.insertUser2Role(role);
        }

        // 插入用户→工厂//删除工厂控制
        // user2PlantDao.deleteByUserId(o.getId());
        // for (String plantId : user.getPlants()) {
        //     T09User2Plant plant = T09User2Plant.create();
        //     plant.setPlantId(plantId);
        //     plant.setUserId(o.getId());
        //     user2PlantDao.insertUser2Plant(plant);
        // }
        // 插入用户->BP
        user2BpDao.deleteByUserId(o.getId());
        for (String bpId : user.getBps()) {
            T14User2Bp bp = T14User2Bp.create();
            bp.setBpId(bpId);
            bp.setUserId(o.getId());
            user2BpDao.insertUser2Bp(bp);
        }

        return userID;

    }

    public void updateUser(Sys001User user) {
        // 查找UUIDD
        T01User o = userDao.getById(user.getUserId());
        o.setUserType(user.getUserType());
        o.setUserName(user.getUserName());
        o.setUserStatus(user.getUserStatus());
        o.setBpNumber(user.getBpNumber());
        o.setValidDateFrom(user.getValidDateFrom());
        o.setValidDateTo(user.getValidDateTo());
        o.setUpTime(Instant.now());
        o.setUpBy(null);

        userDao.update(o);

        // 插入用户角色
        user2RoleDao.deleteUser2Role(o.getId());
        for (String roleId : user.getRoles()) {
            T04User2Role role = T04User2Role.create();
            role.setRoleId(roleId);
            role.setUserId(o.getId());
            user2RoleDao.insertUser2Role(role);
        }

        // 插入用户→工厂//删除工厂控制
        // user2PlantDao.deleteByUserId(o.getId());
        // for (String plantId : user.getPlants()) {
        //     T09User2Plant plant = T09User2Plant.create();
        //     plant.setPlantId(plantId);
        //     plant.setUserId(o.getId());
        //     user2PlantDao.insertUser2Plant(plant);
        // }

        // 插入用户->BP
        user2BpDao.deleteByUserId(o.getId());
        for (String bpId : user.getBps()) {
            T14User2Bp bp = T14User2Bp.create();
            bp.setBpId(bpId);
            bp.setUserId(o.getId());
            user2BpDao.insertUser2Bp(bp);
        }

    }

    // 检查User是否存在
    public void checkUserExist(Sys001User user) {
        T01User userID = userDao.getById(user.getUserId());
        boolean hasError = false;
        if (userID != null) {
            hasError = true;
            messages.error("ERROR_SYS01_001").target(SysT01User_.class, o -> o.USER_ID());
        }

        if (hasError) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "ERROR");
        }
    }

    public void deleteUser(Sys001User user) {
        // 删除用户表
        userDao.deleteUserID(user.getUserId());
        // 删除用户对应的工厂表
        user2PlantDao.deleteByUserId(user.getUserId());
        // 删除用户对应的BP表
        user2BpDao.deleteByUserId(user.getUserId());
    }

}
