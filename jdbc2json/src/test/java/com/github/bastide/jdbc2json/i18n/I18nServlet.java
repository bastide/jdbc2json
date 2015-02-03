package com.github.bastide.jdbc2json.i18n;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class I18nServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Iñtërnâtiônàlizætiøn");
    }    
}
