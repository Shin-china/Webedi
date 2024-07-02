package customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.sys.T01User;
import customer.bean.sys.Sys001User;
import customer.dao.sys.UserDao;

@Component
public class SysUserService {
    @Autowired
    private UserDao userDao;

    /*
     * Insert User
     */
    public void insertUser(Sys001User user) {
        T01User o = T01User.create();
        o.setId(user.getUserId());
        o.setUserType(user.getUserType());
        o.setValidDateFrom(user.getValidDateFrom());
        o.setValidDateTo(user.getValidDateTo());

        userDao.insert(o);

    }
}
