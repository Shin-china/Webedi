package customer.dao.pch;

import cds.gen.pch.Pch_;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.tableservice.PchT07QuotationD;
import cds.gen.tableservice.PchT07QuotationD_;
import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import customer.bean.pch.Pch08DetailParam;
import customer.bean.pch.Pch08QueryResult;
import customer.bean.pch.Pch08Template;
import customer.dao.common.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class Pch08Dao extends Dao {

    // 修改
    private static final Logger logger = LoggerFactory.getLogger(Pch08Dao.class);

    // 修改t06, t07
    public void updatePch08(T06QuotationH h, T07QuotationD d) {
        d.setUpTime(getNow());
        d.setUpBy(this.getUserId());
        logger.info("修改T07QuotationD" + d.getQuoNumber() + "行号：" + d.getQuoItem());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).data(d));

        h.setUpTime(getNow());
        h.setUpBy(this.getUserId());

        logger.info("修改T06QuotationH" + h.getQuoNumber());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).data(d));
        db.run(Update.entity(Pch_.T06_QUOTATION_H).data(h));

    }

    public T06QuotationH getT06ByQuoNumber(String quoNumber) {
        Optional<T06QuotationH> result = db.run(
                Select.from(Pch_.T06_QUOTATION_H)
                       .where(o -> o.QUO_NUMBER().eq(quoNumber)))
               .first(T06QuotationH.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public List<T07QuotationD> getT07ByQuoNumber(String quoNumber) {
        List<T07QuotationD> listOf = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                     .where(o -> o.QUO_NUMBER().eq(quoNumber).and(o.DEL_FLAG().eq("N"))
                     ))
             .listOf(T07QuotationD.class);
        return listOf;
    }

    public List<T07QuotationD> getT07ByQuoNumberItem(String quoNumber, Integer quoItem) {
        List<T07QuotationD> listOf = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                   .where(o -> o.QUO_NUMBER().eq(quoNumber).and(o.DEL_FLAG().eq("N")).and(o.QUO_ITEM().eq(quoItem))
                  ))
           .listOf(T07QuotationD.class);
        return listOf;
    }

    // 修改t06, t07
    public void updatePch08(List<T07QuotationD> oldItems, List<T07QuotationD> newItems) {
        //删除旧数据
        if(oldItems != null) {
            oldItems.forEach(d -> {
                d.setUpTime(getNow());
                d.setUpBy(this.getUserId());
                d.setDelFlag("Y");
                logger.info("修改T07QuotationD" + d.getQuoNumber() + "行号：" + d.getQuoItem());
                db.run(Update.entity(Pch_.T07_QUOTATION_D).data(d));
            });
        }

        // 插入新数据
        if(newItems!= null) {
            newItems.forEach(d -> {
                d.setUpTime(getNow());
                d.setUpBy(this.getUserId());
                logger.info("新建T07QuotationD" + d.getQuoNumber() + "行号：" + d.getQuoItem());
                db.run(Insert.into(Pch_.T07_QUOTATION_D).entry(d));
            });
        }


//        if(h != null) {
//            db.run(Update.entity(Pch_.T06_QUOTATION_H).data(h));
//        }

    }

    public Pch08QueryResult queryTemplateData(List<Pch08DetailParam> params ){
        List<String> quoNumberList = params.stream().map(Pch08DetailParam::getQUO_NUMBER).toList();
        List<Integer> quoItemList = params.stream().map(Pch08DetailParam::getQUO_ITEM).toList();
        Result itemResult = db.run(Select.from(PchT07QuotationD_.class).where(e->e.QUO_NUMBER().in(quoNumberList)
                .and(e.QUO_ITEM().in(quoItemList))
                .and(e.DEL_FLAG().eq("N"))
        ));
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
                                ))
                        .columns(o -> o.ID())
        ).first(T07QuotationD.class);

        if (result.isPresent()){
            return result.get().getId();
        }else {
            return "";
        }
    }
}
