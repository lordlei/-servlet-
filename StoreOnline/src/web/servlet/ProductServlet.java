package web.servlet;

import com.google.gson.Gson;
import domain.*;
import service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;


@SuppressWarnings("all")
@WebServlet("/product")
public class ProductServlet extends BaseServlet {
    //清空购物车
    public void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("cart");
        request.getRequestDispatcher("cart.jsp").forward(request,response);
    }


    //删除购物车中的商品
    public void delItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            Map<String, CartItem> cartItems = cart.getCartItems();
            //减去删除的价格
            CartItem cartItem = cartItems.get(pid);
            cart.setTotal(cart.getTotal()-cartItem.getSubtotal());
            //删除商品
            cartItems.remove(pid);
            //************************由于获取session中的内容 属于引用 所以不需要再次放入到域中去了

        }
        request.getRequestDispatcher("cart.jsp").forward(request,response);

    }


    //添加到购物车
    public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String pid = request.getParameter("pid");

        ProductService service = new ProductService();
        Product product = service.findProductByPid(pid);
        int buyNum = Integer.parseInt(request.getParameter("buyNum"));

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }
        CartItem cartItem = new CartItem();
        double subtotal = buyNum * product.getShop_price();

        cartItem.setProduct(product);
        cartItem.setBuyNum(buyNum);
        cartItem.setSubtotal(subtotal);

        Map<String, CartItem> cartItems = cart.getCartItems();
        double total = 0.0;
        if (cartItems.containsKey(pid)) {
            CartItem oldItem = cartItems.get(pid);
            //修改购买数量
            int oldBuyNum = oldItem.getBuyNum();
            oldBuyNum += buyNum;
            oldItem.setBuyNum(oldBuyNum);
            //修改小计
            double oldsubtotal1 = oldItem.getSubtotal();
            oldsubtotal1 += subtotal;
            oldItem.setSubtotal(oldsubtotal1);
            //重新添加到订单项中
            cartItems.put(pid, oldItem);
        } else {
            cartItems.put(pid, cartItem);
        }
        //总计 加的都是新的 所以不需要改变
        total = cart.getTotal() + subtotal;
        cart.setTotal(total);

        session.setAttribute("cart", cart);

        response.sendRedirect("cart.jsp");

    }


    //显示商品的类别的的功能
    public void categoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductService service = new ProductService();
        List<category> categoryList = service.findAllCategoryList();

        Gson gson = new Gson();
        String json = gson.toJson(categoryList);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(json);

    }


    //显示首页的功能
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductService service = new ProductService();

        //寻找最热商品
        List<Product> hotProductList = service.findHotProductList();

        //寻找最新商品
        List<Product> newProductList = service.findNewProductList();

        request.setAttribute("hotProductList", hotProductList);
        request.setAttribute("newProductList", newProductList);

        request.getRequestDispatcher("/index.jsp").forward(request, response);

    }

    //根据商品的类别获得商品的列表
    public void ProductListByCid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获得cid
        String cid = request.getParameter("cid");

        String currentPageStr = request.getParameter("currentPage");
        if (currentPageStr == null) {
            currentPageStr = "1";
        }
        int currentPage = Integer.parseInt(currentPageStr);
        int currentCount = 12;

        ProductService service = new ProductService();
        PageBean pageBean = service.findProductListByCid(cid, currentPage, currentCount);

        request.setAttribute("pageBean", pageBean);
        request.setAttribute("cid", cid);

        //定义一个记录历史商品信息的集合
        List<Product> historyProductList = new ArrayList<Product>();

        //获得客户端携带名字叫pids的cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    String pids = cookie.getValue();//3-2-1
                    String[] split = pids.split("-");
                    for (String pid : split) {
                        Product pro = service.findProductByPid(pid);
                        historyProductList.add(pro);
                    }
                }
            }
        }

        //将历史记录的集合放到域中
        request.setAttribute("historyProductList", historyProductList);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);

    }


    //显示商品的详细信息功能
    public void ProductByPid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");
        String cid = request.getParameter("cid");
        String currentPage = request.getParameter("currentPage");

        ProductService service = new ProductService();
        Product product = service.findProductByPid(pid);

        request.setAttribute("product", product);
        request.setAttribute("cid", cid);
        request.setAttribute("currentPage", currentPage);


        //获得客户端携带cookie---获得名字是pids的cookie
        String pids = pid;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    pids = cookie.getValue();
                    //1-3-2 本次访问商品pid是8----->8-1-3-2
                    //1-3-2 本次访问商品pid是3----->3-1-2
                    //1-3-2 本次访问商品pid是2----->2-1-3
                    //将pids拆成一个数组
                    String[] split = pids.split("-");
                    List<String> asList = Arrays.asList(split);
                    LinkedList<String> list = new LinkedList<>(asList);
                    if (list.contains(pid)) {
                        list.remove(pid);
                        list.addFirst(pid);
                    } else {
                        list.addFirst(pid);
                    }
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < list.size() && i < 7; i++) {
                        if (i == list.size() - 1) {
                            buffer.append(list.get(i));
                        } else {
                            buffer.append(list.get(i));
                            buffer.append("-");
                        }
                    }
                    pids = String.valueOf(buffer);
                }
            }
        }
        Cookie cookie = new Cookie("pids", pids);
        response.addCookie(cookie);

        request.getRequestDispatcher("product_info.jsp").forward(request, response);

////        //获得客户端携带cookie---获得名字是pids的cookie
//        String pids = pid;
//        Cookie[] cookies = request.getCookies();
//        if(cookies!=null){
//            for(Cookie cookie : cookies){
//                if("pids".equals(cookie.getName())){
//                    pids = cookie.getValue();
//                    //1-3-2 本次访问商品pid是8----->8-1-3-2
//                    //1-3-2 本次访问商品pid是3----->3-1-2
//                    //1-3-2 本次访问商品pid是2----->2-1-3
//                    //将pids拆成一个数组
//                    String[] split = pids.split("-");//{3,1,2}
//                    List<String> asList = Arrays.asList(split);//[3,1,2]
//                    LinkedList<String> list = new LinkedList<String>(asList);//[3,1,2]
//                    //判断集合中是否存在当前pid
//                    if(list.contains(pid)){
//                        //包含当前查看商品的pid
//                        list.remove(pid);
//                        list.addFirst(pid);
//                    }else{
//                        //不包含当前查看商品的pid 直接将该pid放到头上
//                        list.addFirst(pid);
//                    }
//                    //将[3,1,2]转成3-1-2字符串
//                    StringBuffer sb = new StringBuffer();
//                    for(int i=0;i<list.size()&&i<7;i++){
//                        sb.append(list.get(i));
//                        sb.append("-");//3-1-2-
//                    }
//                    //去掉3-1-2-后的-
//                    pids = sb.substring(0, sb.length()-1);
//                }

//        Cookie cookie_pids = new Cookie("pids",pids);
//        response.addCookie(cookie_pids);

    }


}
