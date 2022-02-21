package com.guoyx.service;

import com.guoyx.entity.FileInfoEntity;
import com.guoyx.util.HttpUtil;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

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

        if (StringUtils.isBlank(taskURL)) {
            throw new IllegalArgumentException("下载链接不能为空");
        }

        if (!StringUtils.startsWithAny(taskURL, "http", "https")) {
            throw new IllegalArgumentException("下载链接必须是以http或者https开头");
        }

        //添加下载任务
        taskPool.execute(() -> downloadFile(taskURL, remark));
    }

    public List<FileInfoEntity> listFiles() {
        File filePath = new File(downloadPath);
        Collection<File> files = FileUtils.listFiles(filePath, null, true);

        List<FileInfoEntity> fileList = new ArrayList<>();
        for (File file : files) {
            FileInfoEntity fileInfo = new FileInfoEntity();
            fileInfo.setFileName(file.getName());
            fileInfo.setSize(file.length());

            fileList.add(fileInfo);
        }

        return fileList;
    }

    private void downloadFile(String taskURL, String remark) {
        try {
            logger.info("start download file. url={} remark={}", taskURL, remark);
            Request request = new Request.Builder()
                    .url(taskURL)
                    .build();

            Response response = HttpUtil.getHttpClient().newCall(request).execute();
            if (response == null || response.body() == null) {
                throw new RuntimeException("server response body is empty");
            }
            byte[] bytes = response.body().bytes();

            //切割出图片名称
            String filename = StringUtils.substringAfterLast(taskURL, "/");
            Path folderPath = Paths.get(downloadPath);
            boolean desk = Files.exists(folderPath);
            if (!desk) {
                //不存在文件夹
                Files.createDirectories(folderPath);
            }

            String suffix = StringUtils.isBlank(remark) ? StringUtils.EMPTY : "[" + remark + "]";
            Path filePath = Paths.get(downloadPath + File.separator + suffix + filename);
            boolean exists = Files.exists(filePath, LinkOption.NOFOLLOW_LINKS);
            if (!exists) {
                //不存在文件
                Files.write(filePath, bytes, StandardOpenOption.CREATE);
            }
            logger.info("finish download file task. url={} remark={}", taskURL, remark);

        } catch (Exception e) {
            logger.error("addDownloadTask error. url={} remark={}", taskURL, remark, e);
        }
    }

    public File getFilePath(String fileName) {
        String filePath = downloadPath + File.separator + fileName;
        return new File(filePath);
    }

}
