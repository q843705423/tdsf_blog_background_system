package com.tiandisifang.interceptor;

import com.tiandisifang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


@Configuration
public class MyWebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {//添加拦截器-拦截器注册表
        interceptorRegistry.addInterceptor(new HandlerInterceptor() {

            @Override//拦截前处理
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {


                // System.out.println("URL:"+request.getRequestURL());
                HandlerMethod handlerMethod = (HandlerMethod)handler;
                Method method = handlerMethod.getMethod();
                UnCheck unCheck = method.getAnnotation(UnCheck.class);
                if(unCheck!=null){//有UnCheck,则为不需要检测
                    return true;
                }

//                String token = request.getParameter("token");//读取前端传来的token
//
//                UserInfo u = userService.getUserInfo(token);
//                if(u == null){
//                    try{
//                        PrintWriter pw = response.getWriter();
//                        String origin = request.getHeader("Origin");
//                        response.setHeader("Access-Control-Allow-Origin", origin==null||origin.trim().equals("") ? "*" : origin);
//                        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
//                        response.setHeader("Access-Control-Max-Age", "0");
//                        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");
//                        response.setHeader("Access-Control-Allow-Credentials", "true");
//                        response.setHeader("XDomainRequestAllowed","1");
//                        pw.write("{\"info\":\"not_login\",\"stauts\":0}");
//                        //pw.close();
//                    }catch (Exception e){
//                        System.out.println(e.getMessage());
//                    }
//                    return false;
//                }

                return true;
            }

            @Override//拦截中处理
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

            }

            @Override//拦截后处理
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

            }
        });


        super.addInterceptors(interceptorRegistry);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("打开跨域请求");
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }
}
