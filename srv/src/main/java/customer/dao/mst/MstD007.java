package customer.dao.mst;

import java.util.List;
import java.util.Optional;

import customer.comm.tool.DateTools;
import customer.dao.common.Dao;
import io.vavr.collection.Seq;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T03PoC_;
import cds.gen.mst.Mst_;
import cds.gen.mst.T01SapMat;
import cds.gen.mst.T03SapBp;
import cds.gen.pch.Pch_;

import java.time.Instant;

@Repository
public class MstD007 extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(MstD007.class);

    /**
     * 根据 materialNumber 查找 SAP 品目代码，并检查 DEL_FLAG 是否为 null。
     * 
     * @param materialNumber 上传文件中的 MATERIAL_NUMBER
     * @return 如果找到符合条件的记录则返回 true，否则返回 false
     */
    public boolean isSapMaterialValid(String materialNumber) {
        try {
            Optional<T01SapMat> result = db.run(
                Select.from(Mst_.T01_SAP_MAT)
                      .where(o -> o.MAT_ID().eq(materialNumber)
                              .and(o.DEL_FLAG().isNull()))
            ).first(T01SapMat.class);

            return result.isPresent();
        } catch (Exception e) {
            logger.error("Error checking material validity for: {}", materialNumber, e);
            return false;
        }
    }

    /**
     * 根据 bpNumber 查找 SAP 供应商代码，并检查 DEL_FLAG 是否为 null。
     * 
     * @param bpNumber 上传文件中的 BP_NUMBER
     * @return 如果找到符合条件的记录则返回 true，否则返回 false
     */
    public boolean isSapBpValid(String bpNumber) {
        try {
            Optional<T03SapBp> result = db.run(
                Select.from(Mst_.T03_SAP_BP)
                      .where(o -> o.BP_ID().eq(bpNumber)
                              .and(o.DEL_FLAG().isNull()))
            ).first(T03SapBp.class);

            return result.isPresent();
        } catch (Exception e) {
            logger.error("Error checking BP validity for: {}", bpNumber, e);
            return false;
        }
    }

    // 其他方法可以在此处继续定义...
}
