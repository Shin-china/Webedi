package customer.service.sys;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sap.cds.services.messages.Messages;

import customer.comm.constant.ConfigConstants;
import customer.comm.constant.UmcConstants;
import customer.dao.common.DarftDao;


@Component
public class DarftCommService {

    @Autowired
    private DarftDao darftDao;

    @Autowired
    Messages messages;

    /** 检查是否有其他人正在编辑草稿数据 */
    public boolean checkDarftByOhter(String infoId, String darftTable, String nowUser) {
        List<Map<String, Object>> list = darftDao.getDarftList(infoId, darftTable);
        if (null == list || list.size() == 0)
            return true;

        String darftCdUser = (String) list.get(0).get("createdbyuser");
        Timestamp timestamp = (Timestamp) list.get(0).get("creationdatetime");
        //Instant cdTime =timestamp.toInstant();
        LocalDateTime cdTimes = timestamp.toLocalDateTime();
        Instant cdTime = cdTimes.toInstant(ConfigConstants.SERVER_ZONE);
        if (!darftCdUser.equals(nowUser)) {
            if (cdTime.isBefore(Instant.now().plus(UmcConstants.TABLE_DARFTS_DATA_VALID_DATE, ChronoUnit.MINUTES))) {
                //草稿数据超过30分钟，删除后新建
                return true;
            }
            //已有其它用户正在编辑
            this.messages.error("ERROR_COMMAND_DARFT_BY_OTHER_USER", darftCdUser);
            return false;

        }
        return true;

    }

    /**
     * 超时 重新打开编辑
     */
    public boolean checkDarftsDataIsExist(String infoId, String darftTable) {

        List<Map<String, Object>> list = darftDao.getDarftList(infoId, darftTable);
        if (null == list || list.size() == 0) {
            //当前草稿数据不存在 （已超时或者被它人使用清除)。
            this.messages.error("ERROR_COMMAND_DARFT_DATA_IS_NOT_EXIST");
            return true;
        }
        return false;

    }

    /** 删除草稿数据头表 */
    public void deleteDarftHById(String infoId, String darftTable) {
        String where = " ID = '" + infoId + "'";
        darftDao.deleteDarft(darftTable, where);

    }

    /** 删除草稿数据明细表 */
    public void deleteDarftDById(String infoId, String darftTable) {
        String where = " H_ID = '" + infoId + "'";
        darftDao.deleteDarft(darftTable, where);

    }

    public String[] checkDarftParms(String params) {

        if (null == params || "".equals(params) || params.indexOf(",") < 0) {
            return null;
        }
        String[] parArr = params.split(",");
        if (parArr.length < 2) {
            return null;
        }
        return params.split(",");
    }

}
