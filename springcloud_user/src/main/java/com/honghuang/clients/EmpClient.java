package com.honghuang.clients;

import com.honghuang.entity.EmpDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

// 调用员工服务的feign
@FeignClient("emps")
public interface EmpClient {

    @GetMapping("/emp/find") // 注意：使用openFeign的get方式传递参数，参数变量必须通过@RequestParam注解进行修饰
    Map<String,Object> find(@RequestParam("id") String id);

    @PostMapping("/emp/findByName") // 注意：此处同样需要，参数变量必须通过@RequestParam注解进行修饰
    Map<String,Object> findByName(@RequestParam("name")String name);

    @PostMapping("/emp/saveEmp")
    Map<String,Object> saveEmp(@RequestBody EmpDTO empDTO);
}
