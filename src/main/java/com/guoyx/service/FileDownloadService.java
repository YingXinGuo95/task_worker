package com.guoyx.service;

import com.guoyx.util.HttpUtil;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

@Service
public class FileDownloadService extends BaseService{
    @Value("${download.path}")
    private String downloadPath;

    private final BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(5);

    private final ExecutorService taskPool = new ThreadPoolExecutor(1,1, 60, TimeUnit.MILLISECONDS, taskQueue);

    /**
     * 添加文件下载任务
     * @param taskURL 下载链接
     * @param remark 文件备注
     */
    public void addTask(String taskURL, String remark) {
        if (StringUtils.length(remark) > 10) {
            throw new IllegalArgumentException("备注信息长度过长，需小于10个字符");
        }

        String pattern = "/(http|https):\\/\\/([\\w.]+\\/?)\\S*/";
        if (!Pattern.matches(pattern, taskURL)) {
            throw new IllegalArgumentException("下载链接不正确，需要是http或者https开头的资源");
        }

        //添加下载任务
        taskPool.execute(() -> downloadFile(taskURL, remark));
    }

    private void downloadFile(String taskURL, String remark) {
        try {
            Request request = new Request.Builder()
                    .url(taskURL)
                    .build();

            Response response = HttpUtil.getHttpClient().newCall(request).execute();
            if (response == null || response.body() == null) {
                throw new RuntimeException("server response body is empty");
            }
            byte[] bytes = response.body().bytes();

            //切割出图片名称
            String filename = org.apache.commons.lang3.StringUtils.substringAfterLast(taskURL, "/");
            Path folderPath = Paths.get(downloadPath);
            boolean desk = Files.exists(folderPath);
            if (!desk) {
                //不存在文件夹
                Files.createDirectories(folderPath);
            }

            String suffix = org.apache.commons.lang3.StringUtils.isBlank(remark) ? org.apache.commons.lang3.StringUtils.EMPTY : "[" + remark + "]";
            Path filePath = Paths.get(downloadPath + suffix + filename);
            boolean exists = Files.exists(filePath, LinkOption.NOFOLLOW_LINKS);
            if (!exists) {
                //不存在文件
                Files.write(filePath, bytes, StandardOpenOption.CREATE);
            }

        } catch (Exception e) {
            logger.error("addDownloadTask error. url={} remark={}", taskURL, remark, e);
        }
    }

}
