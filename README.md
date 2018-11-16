### 前言
大家好,手写springmvc源码，希望和大家分享一下，希望大家能从中收益，如果有意见和好的想法请加我！
 ＱＱ:3341386488
 邮箱：QiuRunZe_key@163.com

我会不断完善，希望大家有好的想法拉一个分支提高，一起合作！


    觉得不错对您有帮助，麻烦右上角点下star以示鼓励！长期维护不易 多次想放弃 坚持是一种信仰 专注是一种态度！


## springmvc源码

![初始化整体流程](https://raw.githubusercontent.com/qiurunze123/imageall/master/springmvc.png)

     pom -- 引入了javax.servlet这个jar，说到底mvc的底层也就是一个servlet

![初始化整体流程](https://raw.githubusercontent.com/qiurunze123/imageall/master/springmvc2.png)

    这个是tomcat初始化的时候,初始流程， 整体来说 mvc的实现也是极大的利用了反射原理，将初始化的bean放在
    map中，在取一对一的映射拼装起来
    
    load-on-startup标记容器是否在启动的时候实例化并调用其init()方法的优先级。
    它的值表示servlet应该被载入的顺序
    当值为0或者大于0时，表示容器在应用启动时就加载并初始化这个servlet
    这一些值都是注册一个servlet的必要阶段
    
    注解，控制层，service等等 