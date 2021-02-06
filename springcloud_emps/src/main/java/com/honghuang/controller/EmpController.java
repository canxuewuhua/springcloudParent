package com.honghuang.controller;

import com.honghuang.entity.EmpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RefreshScope  //需要刷新代码的类中加入刷新配置的注解
public class EmpController {

    @Value("${server.port}")
    private String port;

    /**
     * 根据员工id查询员工信息
     */
    @GetMapping("/emp/find")
    public Map<String,Object> find(@RequestParam("id") String id){
        Map<String,Object> map = new HashMap<>();
        log.info("进入员工服务，根据员工编号：[{}]查询员工信息", id);
        map.put("status",true);
        map.put("msg","当前员工服务调用成功，查询的员工编号为["+id+"]，调用服务的端口号为：" + port);
        return map;
    }

    /**
     * 根据员工名称查询员工信息
     */
    @PostMapping("/emp/findByName")
    public Map<String,Object> findByName(String name){
        Map<String,Object> map = new HashMap<>();
        // 默认情况下,openFiegn在进行服务调用时,要求服务提供方处理业务逻辑时间必须在1S内返回,如果超过1S没有返回则OpenFeign会直接报错,不会等待服务执行,
        // 但是往往在处理复杂业务逻辑是可能会超过1S,因此需要修改OpenFeign的默认服务调用超时时间。
        // 这个超时时间是 users服务调用 emps服务 在users服务配置文件中进行设置
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){e.printStackTrace();}
        log.info("进入员工服务，根据员工名称：[{}]查询员工信息", name);
        map.put("status",true);
        map.put("msg","当前员工服务调用成功，查询的员工名称为["+name+"]，调用服务的端口号为：" + port);
        return map;
    }

    @PostMapping("/emp/saveEmp")
    public Map<String,Object> saveEmp(@RequestBody EmpDTO empDTO){
        log.info("员工服务保存员工信息调用成功,当前服务端口:[{}]",port);
        log.info("当前接收员工信息:[{}]",empDTO);
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("msg","员工服务查询员工信息调用成功,当前服务端口: "+port);
        map.put("status",true);
        map.put("emp",empDTO);
        return map;
    }
}
