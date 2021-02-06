# springcloudParent
``` - nacos 客户端地址
- http://localhost:8848/nacos/

- sentinel 客户端地址
- http://localhost:9091/

- 访问路由管理列表地址
- http://localhost:8091/actuator/gateway/routes
```

 openFeign 实现服务间通信 负载均衡
 
 使用nacos 配置的时候，如果本地起两台同一个服务会报端口占用，此时将nacos上面的配置中server.port放置到本地的配置文件中，否则就报端口占用，自己使用-Dserver.port=xxx 不起作用
 
 断言 - 网关的拦截
 ```
- After=2020-07-21T11:33:33.993+08:00[Asia/Shanghai]  			`指定日期之后的请求进行路由
- Before=2020-07-21T11:33:33.993+08:00[Asia/Shanghai]       `指定日期之前的请求进行路由
- Between=2017-01-20T17:42:47.789-07:00[America/Denver], 2017-01-21T17:42:47.789-07:00[America/Denver]
- Cookie=username,chenyn																		`基于指定cookie的请求进行路由
- Cookie=username,[A-Za-z0-9]+															`基于指定cookie的请求进行路由	
	`curl http://localhost:8989/user/findAll --cookie "username=zhangsna"
- Header=X-Request-Id, \d+																 ``基于请求头中的指定属性的正则匹配路由(这里全是整数)
	`curl http://localhost:8989/user/findAll -H "X-Request-Id:11"
- Method=GET,POST																						 `基于指定的请求方式请求进行路由
```

  网关配置中的过滤器，自动增加请求参数
          filters:
            - AddRequestParameter=id,34
            
  服务熔断是对调用链路的保护 服务降级是对系统过载的一种保护
  熔断一定触发降级
  服务熔断是解决服务雪崩的重要手段
  如果目标服务情况好转则恢复调用
  通俗: 关闭系统中边缘服务 保证系统核心服务的正常运行  称之为服务降级
  
  openFeign负载均衡策略可参考ribbon，openFeign本身就继承了ribbon的负载均衡策略 使用如下策略
  users服务调用emps，在users服务的配置文件中添加下面的配置
  ``emps.ribbon.NFLoadBalancerRuleClassName=com.netflix.loadbalancer.RandomRule``
  

  现在有一个网关服务，在网关服务的配置中使用了路由规则，经过网关后，直接调用员工服务使用了轮询方式进行调用了服务集群，此处应该也支持配置路由策略，这个以后可以研究lb
  目前是轮询的，怎么设置为随机可以再看下
  注：如果员工服务是集群部署，即两台不同端口的同一服务  用户是单机部署的
  如果经过网关后调用了用户服务，用户服务中配置随机路由规则调用员工服务，这就是openFeign组件的随机路由规则配置
  
  # sentinel 流量控制
  流量控制（flow control），其原理是监控应用流量的 QPS 或并发线程数等指标，当达到指定的阈值时对流量进行控制，以避免被瞬时的流量高峰冲垮，从而保障应用的高可用性。
  注意：sentinel流量规则在服务重启后会消失 要重新设置
  
  阈值类型 QPS 线程数
  
  模式三种 直接 关联 链路
  流控模式中的配置 关联
  1、/user/getEmpInfo
  2、/user/getEmpInfoByName
  1关联2 其实是反向关联  意思就是2如果超过阈值，则1不能访问
  
   流控模式中的配置 链路  基于链路
   比如 order/find  关联 /emp/order/find 即 emps服务下调用订单find方法 如果超出阈值，则调用 /order/find方法的都不能再调用了
   
   三种效果
   快速失败 warm up  排队等待
   快速失败 如果单机QPS超过阈值，直接拒绝
   warm up 预热   20个请求过来 刚开始接收三分之一 慢慢增加 等到达预热时间后，再接收20个请求
   排队等待      单机阈值1个/s  jemeter 1s来10个请求 均速排队  如果配置超时时间为3000ms 则只接收3个  后面的就拒绝
   
   ···降级···
   RT
   1s内5个请求过来，如果每个请求平均响应时间 均 超过4.9s 则自动熔断降级
   
   sentinel控制台上设置降级规则 资源名接口名 降级策略选RT（平均响应时间）RT设置为0.1s，时间窗口设置为20s 代表20s后断路器关闭
   
   比如有这样一个场景 jemeter上0s内10个请求不断的请求 在浏览器地址栏内也进行请求，这个时候超过了0.1s的响应，断路器打开 ，关闭jemeter后，20s过后，断路器关闭
   浏览器内访问正常返回。
   
   异常比例 0-1 如果异常数超过设置的异常比例 则断路器打开 超过设置的时间窗口20s 断路器关闭
   
   异常数    近1分钟内 如果异常数超过设置的异常数 则断路器打开 超过设置的时间窗口20s 断路器关闭
   
   ```sentinel 注解支持``` 用于在熔断后自定义异常处理
   ```@SentinelResource(value = "aa", blockHandler = "testBlockHandler", fallback = "testFallBack")```
   
   testBlockHandler 自定义处理 流控 或者 降级处理 
   testFallBack     自定义处理代码逻辑异常
   
   场景：可以在sentinel控制台上设置一个流控规则 比如说QPS 1s一个 如果超出则流控限流
         在sentinel控制台再设置一个降级规则 比如说 异常数出现1个 则降低 时间窗口设置为20s
         
   ···基于nacos集群 没有讲到··
   ···包括sentinel集群 没有讲到 以后可以补充···
   
   # springcloud alibaba 微服务管理项目启动 需要nacos 和 sentinel 客户端
   sentinel启动命令
   java -Dserver.port=9091 -jar sentinel-dashboard-1.7.0.jar
   
   nacos启动命令
   D:\nacos\bin>startup.cmd -m standalone
   
   另外如果在idea中换一个端口号启动同一个服务的不同实例 可以在VM options中配置上 -Dserver.port=8987 进行启动