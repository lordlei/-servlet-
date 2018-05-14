package dao;

import domain.Product;
import domain.category;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import util.DataSourceUtils;

import java.sql.SQLException;
import java.util.List;

public class ProductDao {
    public List<Product> findHotProductList() throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql="select * from product where is_hot=? limit ?,?";
        List<Product> query = qr.query(sql, new BeanListHandler<Product>(Product.class), 1,0,9);
        return query;
    }

    public List<Product> findNewProductList() throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql="select * from product order by pdate desc limit ?,?";
        List<Product> query = qr.query(sql, new BeanListHandler<Product>(Product.class), 0, 9);
        return query;
    }

    public List<category> findAllCategoryListDao() throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql="select * from category";
        List<category> query = qr.query(sql, new BeanListHandler<category>(category.class));
        return query;
    }

    public int findCount(String cid) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql="select count(*) from product where cid=?";
        Long query =(Long) qr.query(sql, new ScalarHandler(), cid);
        return query.intValue();
    }

    public List<Product> findProductListByCid(String cid, int index, int currentCount) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql="select * from product where cid=? limit ?,?";
        List<Product> query = qr.query(sql, new BeanListHandler<Product>(Product.class), cid, index, currentCount);
        return query;
    }

    public Product findProductByPidDao(String pid) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql="select * from product where pid=?";
        Product query = qr.query(sql, new BeanHandler<Product>(Product.class), pid);
        return query;
    }
}
