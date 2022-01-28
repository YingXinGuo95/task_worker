package com.guoyx.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

@Service
public class FileDownloadService extends BaseService{

    private final BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(5);

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

        taskPool.execute(() -> {

        });
    }


}
