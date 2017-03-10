package com.earphone.clone.module.common.controller;

import com.earphone.aop.annotation.LogPoint;
import com.earphone.clone.module.constant.service.ConstantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by YaoJiamin on 2016/12/9.
 */
@RestController
@RequestMapping
public class CommonController implements ErrorController {
    private static final int NOT_FOUND = 404;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PATH = "/error";

    @Resource
    private ConstantService constantService;

    @Override
    public String getErrorPath() {
        return PATH;
    }

    @RequestMapping(PATH)
    public void error(HttpServletRequest request, HttpServletResponse response) throws Exception {
        switch (response.getStatus()) {
            case NOT_FOUND:
                request.getRequestDispatcher("/404.html").forward(request, response);
                break;
            case INTERNAL_SERVER_ERROR:
                request.getRequestDispatcher("/500.html").forward(request, response);
                break;
        }
    }

    @RequestMapping("/test")
    @LogPoint("test")
    public Object test() throws Exception {
        constantService.findByKey("666");
        return "test";
    }
}
