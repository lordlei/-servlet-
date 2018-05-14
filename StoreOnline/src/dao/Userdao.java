package dao;

import domain.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import util.DataSourceUtils;

import java.sql.SQLException;

public class Userdao {
    public int regist(User user) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql="insert user values(?,?,?,?,?,?,?,?,?,?)";
        int row = qr.update(sql, user.getUid(), user.getUsername(), user.getPassword(), user.getName(), user.getEmail(),
                user.getTelephone(), user.getBirthday(), user.getSex(), user.getState(), user.getCode());
        return row;
    }

    public long checkUserdao(String username) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql="select Count(*) from user where username=?";
        Long query = (long)qr.query(sql, new ScalarHandler(), username);
        return query;

    }

    public void opeanActivedao(String activeCode) throws SQLException {
        QueryRunner qr=new QueryRunner(DataSourceUtils.getDataSource());
        String sql="update user set state=1 where code=?";
        qr.update(sql, activeCode);
    }
}
