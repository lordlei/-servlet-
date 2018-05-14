package web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet("/base")
public class BaseServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String methodName = req.getParameter("method");
            Class clazz = this.getClass();
            Method method = clazz.getMethod(methodName,HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this,req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
