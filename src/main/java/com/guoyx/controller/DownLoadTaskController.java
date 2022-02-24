package com.guoyx.controller;

import com.guoyx.constants.RespCode;
import com.guoyx.entity.FileInfoEntity;
import com.guoyx.entity.Response;
import com.guoyx.service.FileDownloadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 文件传输controller
 */
@RestController
@RequestMapping("/download")
public class DownLoadTaskController extends BaseController{

    @Resource
    private FileDownloadService fileDownloadService;

    @RequestMapping("/addTask")
    public Response addTask(String taskURL, String remark) {
        try{
            fileDownloadService.addTask(taskURL, remark);
            return new Response(RespCode.SUCCESS.getCode(), "任务添加成功，等待后台下载...");
        }catch (Exception e){
            logger.error("addTask error", e);
            return new Response(RespCode.FAIL.getCode(), e.getMessage());
        }
    }

    @RequestMapping("/listFile")
    public Response listFile() {
        try{
            List<FileInfoEntity> files = fileDownloadService.listFiles();
            return new Response(RespCode.SUCCESS, files);
        }catch (Exception e){
            logger.error("listFile error", e);
            return new Response(RespCode.FAIL.getCode(), e.getMessage());
        }
    }

    @RequestMapping("/downloadFile")
    public Response downloadFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = request.getParameter("fileName");
        if(StringUtils.isBlank(fileName)) {
            return new Response(RespCode.FAIL, "未选择下载的文件");
        }

        File filePath = fileDownloadService.getFilePath(fileName);
        try (OutputStream os = response.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))){

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" +
                    java.net.URLEncoder.encode(fileName,"UTF-8"));
            byte[] buffer = new byte[1024];

            int readIdx = bis.read(buffer);
            while(readIdx != -1){
                os.write(buffer);
                readIdx = bis.read(buffer);
            }

        } catch (Exception e) {
            logger.error("downloadFile fail", e);
            return new Response(RespCode.FAIL.getCode(), e.getMessage());
        }

        return new Response(RespCode.SUCCESS);
    }

}
