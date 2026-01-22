package com.example.springmvc.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    // Nạp các cấu hình tầng Root (Database, Security, Service...)
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{ AppConfig.class, SecurityConfig.class };
    }

    // Nạp cấu hình tầng Web (Controller, MVC...)
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{ WebMvcConfig.class };
    }

    // Map tất cả request vào DispatcherServlet của Spring
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        servletContext.addListener(new DatabaseCleanupListener());
    }


}