package com.tiandisifang.config;

import com.tiandisifang.Application;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@ComponentScan(basePackageClasses = Application.class, useDefaultFilters = true)
public class WebMvcConfig extends WebMvcConfigurerAdapter {



	
}