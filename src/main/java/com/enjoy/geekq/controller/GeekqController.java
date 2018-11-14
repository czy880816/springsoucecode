package com.enjoy.geekq.controller;

import com.enjoy.geekq.annotation.EnjoyAutowired;
import com.enjoy.geekq.annotation.EnjoyController;
import com.enjoy.geekq.annotation.EnjoyRequestMapping;
import com.enjoy.geekq.annotation.EnjoyRequestParam;
import com.enjoy.geekq.service.GeekqService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@EnjoyController
@EnjoyRequestMapping("/james")
public class GeekqController {

    @EnjoyAutowired("GeekqServiceImpl")
    private GeekqService geekqService;

    @EnjoyRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @EnjoyRequestParam("name") String name, @EnjoyRequestParam("age") String age) {

        try {
            PrintWriter pw = response.getWriter();
            String result = geekqService.query(name,age);
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
