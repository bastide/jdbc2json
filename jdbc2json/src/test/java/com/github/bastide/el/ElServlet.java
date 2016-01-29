package com.github.bastide.el;

import java.io.IOException;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;
import javax.el.StandardELContext;
import javax.el.ValueExpression;
import javax.servlet.ServletContext;
/**
 *
 * @author rbastide
 */
public class ElServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain;charset=UTF-8");
	ServletContext svc = getServletContext();
	JspFactory jspf = JspFactory.getDefaultFactory();
	JspApplicationContext jspContext = jspf.getJspApplicationContext(svc);
	ExpressionFactory expressionFactory = jspContext.getExpressionFactory();
	StandardELContext context = new StandardELContext(expressionFactory);
	context.addELResolver(new javax.servlet.jsp.el.ImplicitObjectELResolver());
	ValueExpression expression = expressionFactory.createValueExpression(context, "hello  ${param.x}", Object.class);
        resp.getWriter().println(expression.getValue(context));
    }    
}
