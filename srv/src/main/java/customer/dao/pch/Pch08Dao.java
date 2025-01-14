package customer.dao.pch;

import cds.gen.pch.Pch_;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.pch.T07QuotationD_;
import cds.gen.tableservice.PchT07QuotationD_;
import com.sap.cds.Result;
import com.sap.cds.ql.*;
import customer.bean.pch.Pch08DetailParam;
import customer.bean.pch.Pch08QueryResult;
import customer.dao.common.Dao;
import customer.tool.UniqueIDTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class Pch08Dao extends Dao {

    // 修改
    private static final Logger logger = LoggerFactory.getLogger(Pch08Dao.class);

    public List<T07QuotationD> getT07ByQuoNumber(String quoNumber) {
        return db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                     .where(o -> o.QUO_NUMBER().eq(quoNumber).and(o.DEL_FLAG().eq("N"))
                     ))
             .listOf(T07QuotationD.class);
    }

    public List<T07QuotationD> getT07UnReplyItemsByQuoNumber(String quoNumber) {
        return db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                    .where(o -> o.QUO_NUMBER().eq(quoNumber).and(o.DEL_FLAG().eq("N")).and(o.STATUS().ne("3"))
                     ))
            .listOf(T07QuotationD.class);
    }

    public List<T07QuotationD> getT07ByQuoNumberItem(String quoNumber, Integer quoItem) {
        return db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                   .where(o -> o.QUO_NUMBER().eq(quoNumber).and(o.DEL_FLAG().eq("N")).and(o.QUO_ITEM().eq(quoItem))
                  ))
           .listOf(T07QuotationD.class);
    }

    public void updateT06ReplyStatus(String quoNumber){
        Optional<T06QuotationH> headerOpt = db.run(
                Select.from(Pch_.T06_QUOTATION_H)
                       .where(o -> o.QUO_NUMBER().eq(quoNumber).and(o.DEL_FLAG().eq("N")).and(o.STATUS().ne("3")))

        ).first(T06QuotationH.class);

        if (headerOpt.isEmpty()) {
            return;
        }
        T06QuotationH header = headerOpt.get();
        T06QuotationH newHeader = T06QuotationH.create();
        BeanUtils.copyProperties(header, newHeader);
        newHeader.setStatus("3");
        newHeader.setId(UniqueIDTool.getUUID());
        newHeader.setUpTime(getNow());
        newHeader.setUpBy(this.getUserId());


        header.setUpBy(this.getUserId());
        header.setUpTime(getNow());
        header.setDelFlag("Y");
        db.run(Update.entity(Pch_.T06_QUOTATION_H).data(header));


        db.run(Insert.into(Pch_.T06_QUOTATION_H).entry(newHeader));
    }

    // 修改t06, t07
    public void updatePch08(List<T07QuotationD> oldItems, List<T07QuotationD> newItems) {
        //删除旧数据
        if(oldItems != null) {
            oldItems.forEach(d -> {
                d.setUpTime(getNow());
                d.setUpBy(this.getUserId());
                d.setDelFlag("Y");
                logger.info("修改T07QuotationD{}行号：{}", d.getQuoNumber(), d.getQuoItem());
                db.run(Update.entity(Pch_.T07_QUOTATION_D).data(d));
            });
        }

        // 插入新数据
        if(newItems!= null) {
            newItems.forEach(d -> {
                d.setUpTime(getNow());
                d.setUpBy(this.getUserId());
                logger.info("新建T07QuotationD{}行号：{}", d.getQuoNumber(), d.getQuoItem());
                db.run(Insert.into(Pch_.T07_QUOTATION_D).entry(d));
            });
        }

    }

    public Pch08QueryResult queryTemplateData(List<Pch08DetailParam> params ){
        List<String> quoNumberList = params.stream().map(Pch08DetailParam::getQUO_NUMBER).toList();

        List<Predicate> predicateList = params.stream().map(c->CQL.get("QUO_NUMBER").eq(c.getQUO_NUMBER())
                .and(CQL.get("QUO_ITEM").eq(c.getQUO_ITEM()))
                .and(CQL.get("DEL_FLAG").eq("N"))
        ).toList();

        Result itemResult = db.run(Select.from(PchT07QuotationD_.class).where(CQL.or(predicateList)));

        List<T07QuotationD> itemList =  itemResult.listOf(T07QuotationD.class);

        Result headResult = db.run(Select.from(Pch_.T06_QUOTATION_H).where(e->e.QUO_NUMBER().in(quoNumberList)));
        List<T06QuotationH> headerList  = headResult.listOf(T06QuotationH.class);

        return new Pch08QueryResult(headerList, itemList);
    }

    public String getT07QuotationId(String quoNumber, Integer quoItem, String salesNumber, String salesDNo) {
        Optional<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quoNumber).
                                and(o.QUO_ITEM().eq(quoItem)
                                .and(o.SALES_NUMBER().eq(salesNumber))
                                .and(o.SALES_D_NO().eq(salesDNo))
                                .and(o.DEL_FLAG().eq("N"))
                                ))
                        .columns(T07QuotationD_::ID)
        ).first(T07QuotationD.class);

        if (result.isPresent()){
            return result.get().getId();
        }else {
            return "";
        }
    }

    public String getT07QuotationId(String quoNumber, Integer quoItem) {
        Optional<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quoNumber).
                                and(o.QUO_ITEM().eq(quoItem)
                                .and(o.DEL_FLAG().eq("N"))
                                ))
                        .columns(T07QuotationD_::ID)
        ).first(T07QuotationD.class);

        if (result.isPresent()){
            return result.get().getId();
        }else {
            return "";
        }
    }
}
