package com.honghuang.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 熔断降级controller
 */
@RestController
@Slf4j
public class FuseDemoteController {



    @GetMapping("/sentinel/test")
    @SentinelResource(value = "aa", blockHandler = "testBlockHandler", fallback = "testFallBack")
    public String test(int id) {
        log.info("sentinel test");
        if (id < 0)
            throw new RuntimeException("非法参数!!!");
        return "sentinel test :" + id;
    }

    //降级异常处理
    public String testBlockHandler(int id, BlockException e){
        if(e instanceof FlowException){
            return "当前服务已被流控限流！！！ "+e.getClass().getCanonicalName();
        }
        if (e instanceof DegradeException){
            return "当前服务已被降级处理!，请稍后重试！！！ "+e.getClass().getCanonicalName();
        }
        return "当前服务不可用!!!";

    }
    //异常处理
    public String testFallBack(int id){
        return id+"为非法参数，请检查后再重试！！！";
    }
}

