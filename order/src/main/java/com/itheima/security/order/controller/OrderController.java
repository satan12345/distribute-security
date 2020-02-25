package com.itheima.security.order.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @param
 * @author jipeng
 * @date 2020-02-21 13:55
 */
@RestController

public class OrderController {

    /**
     * 测试资源1
     * @return
     */
    @GetMapping(value = "/r/r1")
    @PreAuthorize("hasAuthority('p1')")//拥有p1权限才可以访问
    public String r1(){
        return " 访问资源1";
    }
}

