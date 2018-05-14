package service;

import dao.Userdao;
import domain.User;

import java.sql.SQLException;

public class UserService {
    public boolean regist(User user) {
        Userdao dao = new Userdao();
        int row = 0;
        try {
            row = dao.regist(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (row > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkUser(String username) {
        Userdao dao = new Userdao();
        long row = 0;
        try {
            row = dao.checkUserdao(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row>0?true:false;

    }

    public void opeanActive(String activeCode) {
        Userdao dao=new Userdao();
        try {
            dao.opeanActivedao(activeCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
