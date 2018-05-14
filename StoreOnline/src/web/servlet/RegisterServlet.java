package web.servlet;

import domain.User;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import service.UserService;
import util.CommonsUtils;
import util.MailUtils;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        //接受表单数据
        User user = new User();
        Map<String, String[]> parameterMap = request.getParameterMap();
        try {
            //自己定义一个类型转换器(将String转成Date)
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class aClass, Object o) {
                    //将String转成Date
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date parse = null;
                    try {
                        parse = format.parse(o.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return parse;
                }
            }, Date.class);
            //映射封装
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
//        private String uid;
        user.setUid(CommonsUtils.getUUID());
//        private String telephone;
        user.setTelephone(null);
//        private int state;//是否激活
        user.setState(0);
//        private String code;//激活码
        String activeCode=CommonsUtils.getUUID();
        user.setCode(activeCode);


        String check = request.getParameter("check");
        HttpSession session = request.getSession();
        String checkWord = (String) session.getAttribute("randomString");
        //验证验证码是否正确
        if (check.equals(checkWord)) {
            UserService service = new UserService();
            boolean isRegistSuccess = service.regist(user);
            //判断是否注册成功
            if (isRegistSuccess) {
                //成功后发送邮件
                String emailMsg="恭喜你注册成功,请点击下面的连接进行激活账户"+
                        "<a href='http://localhost:9696/StoreOnline/active?activeCode="+activeCode+"'>"+
                        "http://localhost:9696/StoreOnline/active?activeCode="+activeCode+"</a>";
                try {
                    MailUtils.sendMail(user.getEmail(),emailMsg);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
        }
    }
}
