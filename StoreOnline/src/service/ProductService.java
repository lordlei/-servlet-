package service;

import dao.ProductDao;
import domain.PageBean;
import domain.Product;
import domain.category;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    public List<Product> findHotProductList() {
        ProductDao dao = new ProductDao();
        List<Product> hotProductList = null;
        try {
            hotProductList = dao.findHotProductList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hotProductList;
    }

    public List<Product> findNewProductList() {
        ProductDao dao = new ProductDao();
        List<Product> newProductList = null;
        try {
            newProductList = dao.findNewProductList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newProductList;
    }

    public List<category> findAllCategoryList() {
        ProductDao dao = new ProductDao();
        List<category> categoryList = null;
        try {
            categoryList = dao.findAllCategoryListDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    public PageBean<Product> findProductListByCid(String cid, int currentPage, int currentCount) {
        PageBean<Product> pageBean = new PageBean<Product>();
        pageBean.setCurrentPage(currentPage);
        pageBean.setCurrentCount(currentCount);
        //封装某类商品数
        ProductDao dao = new ProductDao();
        int totalCount = 0;
        try {
            totalCount = dao.findCount(cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setTotalCount(totalCount);
        //封装总页数
        int ceil = (int) Math.ceil(1.0 * totalCount / currentCount);
        pageBean.setTotalPage(ceil);
        //封装当前页的商品列表
        int index = (currentPage - 1) * currentCount;
        List<Product> list = null;
        try {
            list = dao.findProductListByCid(cid, index, currentCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setList(list);

        return pageBean;


    }

    public Product findProductByPid(String pid) {
        ProductDao dao = new ProductDao();
        Product product = null;
        try {
            product = dao.findProductByPidDao(pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }
}
