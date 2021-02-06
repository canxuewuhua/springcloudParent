package com.honghuang.controller;

import com.honghuang.clients.EmpClient;
import com.honghuang.entity.EmpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class UserController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmpClient empClient;

    @GetMapping("/user/getEmpInfo")
    public Map<String, Object> getEmpInfo(String empId){
        // 1、通过RestTemplate直接调用
        // RestTemplate restTemplate = new RestTemplate();
        // String forObject = restTemplate.getForObject("http://localhost:8988/emp/find?id=" + empId, String.class);

        // 2、通过RestTemplate + Ribbon 负载均衡客户端 DiscoveryClient 这里可以自己写负载均衡的算法
//        List<ServiceInstance> empServiceInstances = discoveryClient.getInstances("emps");
//        for (ServiceInstance empServiceInstance : empServiceInstances) {
//            log.info("服务的地址：{}",empServiceInstance.getUri());
//        }

         // 3、通过loadBalancerClient 实现默认的轮询的负载均衡算法
//        ServiceInstance empserviceInstance = loadBalancerClient.choose("emps");
//        log.info("当前处理服务负载均衡客户端主机为：{}", empserviceInstance.getUri());
//        String forObject1 = restTemplate.getForObject(empserviceInstance.getUri() + "/emp/find?id=" + empId, String.class);

        // 4、注解方式 @LoadBalanced 实现直接通过服务名实现轮询的负载均衡算法
        //String forObject = restTemplate.getForObject("http://emps/emp/find?id=" + empId, String.class);

        // 5、以上方法依然使用了地址去实现调用，如果以后更改了调用地址依然需要修改代码
        //   所以引入了 openfeign
        // 先仿照员工服务查询员工信息的方法写一个引入了@FeignClient的接口

        // Feign实现了插拔式的 负载均衡 只需要引入注解写一个接口，在调用处就可以直接调用另外一个服务的方法 当然feign组件提供了很多传参方式的调用服务以及超时设置
        Map<String, Object> map = empClient.find(empId);


        log.info("返回信息为：{}", map);
        return map;
    }

    @GetMapping("/user/getEmpInfoByName")
    public Map<String, Object> getEmpInfoByName(String empName){
        Map<String, Object> map = empClient.findByName(empName);


        log.info("返回信息为：{}", map);
        return map;
    }

    @PostMapping("/user/save")
    public Map<String, Object> saveEmpInfoByEmp(EmpDTO empDTO){
        log.info("接收到员工信息为：{}", empDTO);
        Map<String, Object> map = empClient.saveEmp(empDTO);


        log.info("保存信息为：{}", map);
        return map;
    }
}
