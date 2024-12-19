package customer.dao.common;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DarftDao extends SqlDao {

    @Autowired
    JdbcTemplate db;
    private static final Logger logger = LoggerFactory.getLogger(DarftDao.class);

    /** 编辑数据时 根据id获取当前其它人是否正在编辑 直接读取草稿表 */
    public List<Map<String, Object>> getDarftList(String infoId, String darftTable) {
        String sqlCommand = "SELECT * FROM " + darftTable + " as darftTable  " +
                "join  draft_draftadministrativedata draft on darftTable.draftadministrativedata_draftuuid= draft.draftuuid "
                + "where darftTable.id = ? ";
        return db.queryForList(sqlCommand, infoId);

    }

    /** 删除草稿 */
    public void deleteDarft(String darftTable, String where) {
        String sqlCommand = "DELETE FROM " + darftTable + " WHERE " + where;
        db.execute(sqlCommand);
    }

    public void updateSysT17Draft(String fileId, String fileName, BigDecimal fileSize, String fileContentType,
            String id) {
        String sql = "UPDATE tableservice_sys_t17_doc_drafts SET FILE_ID ='" + fileId + "',FILE_NAME ='"
                + fileName + "',FILE_SIZE=" + fileSize + ",FILE_CONTENT_TYPE='" + fileContentType + "' WHERE ID ='" + id
                + "'";
                System.out.println(sql);
        db.execute(sql);

    }

}
