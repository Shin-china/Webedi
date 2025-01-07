package customer.dao.common;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

public class SqlDao {

    @Autowired
    protected JdbcTemplate db;

    public void testSql() {

        List<Map<String, Object>> aa = db.queryForList("SELECT from sqlite_schema where name like ?", "xxxxx");

    }

    public void setStockForBook(int id, int stock) {
        db.update("call setStockForBook(?,?)", id, stock); // Run the stored procedure `setStockForBook(id in number,
                                                           // stock in number)`
    }

    public int countStock(int id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        Object aa = db.queryForObject("SELECT stock FROM Books WHERE id ='' ",Integer.class); // Run native SQL
        return id;

    }

}
