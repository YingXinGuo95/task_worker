package com.guoyx.controller;

import com.guoyx.util.HttpUtil;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文件传输controller
 */
@Controller
@RequestMapping("/download")
public class DownLoadTaskController extends BaseController{
    @Value("${download.path}")
    private String downloadPath;

    public void addTask(String taskURL, String remark) {

    }

    public static void main(String[] args) {
        new DownLoadTaskController().addTask("","");
    }

}
