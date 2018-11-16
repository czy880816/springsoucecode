package com.enjoy.geekq.servlet;

import com.enjoy.geekq.annotation.*;
import com.enjoy.geekq.controller.GeekqController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet  {


    List<String> classNames = new ArrayList<String>();

    Map<String,Object>  beans = new HashMap<String, Object>();
    Map<String,Object>  handerMap = new HashMap<String, Object>();

    //tomcat 实例化bean
    public void init(ServletConfig config) {
        //扫描 不断递归
        basePackageScan("com.enjoy");

        //实例化
        doInstance();

        //注入
        doAutowired();

        //路径转换器 -- 添加映射关系
        doUrlMapping();

    }

    private void doUrlMapping() {
        for (Map.Entry<String,Object> entry: beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();

            if (clazz.isAnnotationPresent(EnjoyController.class)) {
                EnjoyRequestMapping mapping1 = clazz.getAnnotation(EnjoyRequestMapping.class);
                String classPath = mapping1.value();

                Method[] methods = clazz.getMethods();
                for (Method method:methods){
                    if(method.isAnnotationPresent(EnjoyRequestMapping.class)){
                        EnjoyRequestMapping mapping2 = method.getAnnotation(EnjoyRequestMapping.class);

                        String methodPath = mapping2.value();
                        String requestPath = classPath+methodPath;
                        handerMap.put(requestPath,method);
                    }else{
                        continue;
                    }
                }
        }else{
                continue;
            }

    }
    }

    //  TODO
    public void doAutowired(){
        for (Map.Entry<String,Object> entry: beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();

            if (clazz.isAnnotationPresent(EnjoyController.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field:fields){
                    if(field.isAnnotationPresent(EnjoyAutowired.class)){
                        EnjoyAutowired auto = field.getAnnotation(EnjoyAutowired.class);
                        String key = auto.value();
                        Object bean = beans.get(key);
                        field.setAccessible(true);
                        try {
                            field.set(instance,bean);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else{
                        continue;
                    }
                }
            }else {
                continue;
            }
            }
        }

    public void doInstance(){
        for (String className:classNames){
            String cn = className.replace(".class","");
            try {
                Class<?> clazz = Class.forName(cn);
                if(clazz.isAnnotationPresent(EnjoyController.class)){
                    //控制类
                    Object instance = clazz.newInstance();
                    EnjoyRequestMapping mapping = clazz.getAnnotation(EnjoyRequestMapping.class);
                    String key = mapping.value();
                    //创建map
                    beans.put(key,instance);

                }else if (clazz.isAnnotationPresent(EnjoyService.class)){
                    //服务类
                    Object instance = clazz.newInstance();
                    EnjoyService service = clazz.getAnnotation(EnjoyService.class);
                    String key = service.value();
                    //创建map
                    beans.put(key,instance);
                }else
                {
                    continue;
                }




            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void basePackageScan(String basePackage){

        //扫描编译好的类路径  .class结尾的类
        // url w://work/emjoy/balabal
        URL url = this.getClass().getClassLoader().getResource("/"+basePackage.replaceAll("\\.","/"));

        String fileStr = url.getFile();

        File file = new File(fileStr);
        String[] filesStr = file.list();

        for (String path:filesStr ){
            File filePath = new File(fileStr+path);
            if(filePath.isDirectory()){
                basePackageScan(basePackage+"."+path);
            }else{
                //
                classNames.add(basePackage+"."+filePath.getName());
            }

        }


    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String context = req.getContextPath();
        String path = uri.replace(context,"");

        Method method = (Method) handerMap.get(path);
        GeekqController instance = (GeekqController) beans.get("/"+path.split("/")[1]);
       Object[] arges =  hand(req,resp,method);
        try {
            method.invoke(instance,arges);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Object[] hand(HttpServletRequest request , HttpServletResponse response ,Method method) {

        Class<?>[] paramClazzs = method.getParameterTypes();
        Object[] args = new Object[paramClazzs.length];
        int args_i = 0;
        int index = 0;
        for (Class<?> paramcClazz : paramClazzs) {
            if (ServletRequest.class.isAssignableFrom(paramcClazz)) {
                args[args_i++] = request;
            }

            if (ServletResponse.class.isAssignableFrom(paramcClazz)) {
                args[args_i++] = response;
            }

            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if (paramAns.length > 0) {
                for (Annotation paramAnn : paramAns) {
                    if (EnjoyRequestParam.class.isAssignableFrom(paramAnn.getClass())) {
                        EnjoyRequestParam rp = (EnjoyRequestParam) paramAnn;
                        args[args_i++] = request.getParameter(rp.value());
                    }
                }
            }
            index++;
        }
        return args;
    }
}
