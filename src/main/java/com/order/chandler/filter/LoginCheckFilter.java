package com.order.chandler.filter;

import com.alibaba.fastjson.JSON;
import com.order.chandler.common.BaseContext;
import com.order.chandler.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义过滤器，检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路劲匹配器，支持通配符
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();  // /backend/index.html
        log.info("拦截到的请求：{}",requestURI);

        //2.定义不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg"
        };

        //3.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //4. 如果不需要处理，直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //5.1 判断登录状态，如果已经登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已经登录，用户id为:{}",requestURI);

            //在某个操作线程中，保存id值（主要用于自动填充字段）
            BaseContext.set((Long) request.getSession().getAttribute("employee"));

            filterChain.doFilter(request,response);
            return;
        }

        //5.2 判断登录状态，如果已经登录，则直接放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已经登录，用户id为:{}",requestURI);

            //在某个操作线程中，保存id值（主要用于自动填充字段）
            BaseContext.set((Long) request.getSession().getAttribute("user"));

            filterChain.doFilter(request,response);
            return;
        }

        //6.如果未登录则返回登录结果，通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for(String url : urls){
            if(PATH_MATCHER.match(url, requestURI)){
                return true;
            }
        }
        return false;
    }
}
