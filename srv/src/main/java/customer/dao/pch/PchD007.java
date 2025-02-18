package customer.dao.pch;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import customer.dao.common.Dao;
import customer.tool.DateTools;
import customer.tool.UniqueIDTool;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import cds.gen.pch.T07QuotationD;
import cds.gen.pch.T07QuotationD_;
import cds.gen.common.PchT07QuotationD;
import cds.gen.pch.Pch_;
import cds.gen.pch.T06QuotationH;

@Repository
public class PchD007 extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(PchD007.class);

    public T07QuotationD getByID(String MATERIAL_NUMBER) {
        Optional<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.MATERIAL_NUMBER().eq(MATERIAL_NUMBER)))
                .first(T07QuotationD.class);

        return result.orElse(null);
    }

    // dao层获取传入的QUO_NUMBER所有明细以及头表
    public T07QuotationD get(String quoNumber, String salesNumber, String quoVersion, String item) {
        Optional<T07QuotationD> first = db
                .run(Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quoNumber).and(o.SALES_NUMBER().eq(salesNumber))
                                .and(o.QUO_VERSION().eq(quoVersion)).and(o.DEL_FLAG().eq("N"))
                                .and(o.SALES_D_NO().eq(item))))
                .first(T07QuotationD.class);

        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }
    // dao层获取传入的QUO_NUMBER所有明细以及头表
    public T07QuotationD getId(String quoNumber, String salesNumber, String quoVersion, Integer item, String dno) {
        Optional<T07QuotationD> first = db
                .run(Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quoNumber).and(o.SALES_NUMBER().eq(salesNumber))
                                .and(o.QUO_VERSION().eq(quoVersion)).and(o.QUO_ITEM().eq(item))
                                .and(o.SALES_D_NO().eq(dno))))
                .first(T07QuotationD.class);

        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }
    // 追加
    public void insert(T07QuotationD o) {

        logger.info("=================插入pchd07表号码" + "================");
        o.setCdBy(getUserId());
        o.setCdTime(getNow());
        
            o.setId(UniqueIDTool.getUUID());
        
        o.setCdDate(DateTools.getLocalDate(o.getCdTime()));
        o.setCdDateTime(DateTools.getTimeAsString(o.getCdTime()));

        db.run(Insert.into(Pch_.T07_QUOTATION_D).entry(o));
    }

    public List<T07QuotationD> getList(String quoNum) {
        return db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quoNum)
                                .and(o.DEL_FLAG().eq("N")))) // 追加删除标志
                .listOf(T07QuotationD.class);
    }

    public void update(T07QuotationD o) {
        o.setUpTime(getNow());
        o.setUpBy(this.getUserId());

        logger.info("修改PCHD007" + o.getId());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).data(o));
    }
    public void update(PchT07QuotationD o) {
        o.setUpTime(getNow());
        o.setUpBy(this.getUserId());

        logger.info("修改PCHD007" + o.getId());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).data(o));
    }

    public T07QuotationD getByT07Id(String t07Id) {
        Optional<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.ID().eq(t07Id)))
                .first(T07QuotationD.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public void update(Map<String, Object> data, Map<String, Object> keys) {
        data.put("UP_TIME", this.getNow());
        data.put("UP_BY", this.getUserId());
        CqnUpdate update = Update.entity(Pch_.T07_QUOTATION_D, b -> b.matching(keys)).data(data);

        db.run(update);
    }

    public void updateT07(T07QuotationD o) {

        // 获取 key 的值
        String plantId = o.getPlantId();
        String materialNumber = o.getMaterialNumber();
        String bpNumber = o.getBpNumber();

        // 构建更新条件
        Map<String, Object> keys = new HashMap<>();
        keys.put("PLANT_ID", plantId);
        keys.put("MATERIAL_NUMBER", materialNumber);
        keys.put("BP_NUMBER", bpNumber);

        // 创建需要更新的字段
        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("UNIT", o.getUnit());
        updatedFields.put("LEAD_TIME", o.getLeadTime());
        updatedFields.put("ORIGINAL_COU", o.getOriginalCou());
        updatedFields.put("SUPPLIER_MAT", o.getSupplierMat());
        updatedFields.put("PRICE_CONTROL", o.getPriceControl());
        updatedFields.put("MOQ", o.getMoq());
        updatedFields.put("Incoterms", o.getIncoterms());
        updatedFields.put("Incoterms_Text", o.getIncotermsText());
        updatedFields.put("CURRENCY", o.getCurrency());
        
        updatedFields.put("PRICE", o.getPrice());

        // 执行更新
        db.run(Update.entity(Pch_.T07_QUOTATION_D, b -> b.matching(keys)).data(updatedFields));

    }
}
