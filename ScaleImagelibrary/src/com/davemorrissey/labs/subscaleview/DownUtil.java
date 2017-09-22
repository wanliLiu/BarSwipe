package com.davemorrissey.labs.subscaleview;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownUtil {
    // 定义下载资源的路径
    private String path;
    // 指定所下载的文件的保存位置
    private String targetFile;
    // 定义需要使用多少线程下载资源
    private int threadNum;
    // 定义下载的文件的总大小
    private int fileSize;
    // 定义下载的线程对象
    private DownloadThread[] threads;

    public DownUtil(String path, String targetFile, int threadNum) {
        this.path = path;
        this.threadNum = threadNum;
        // 初始化threads数组
        threads = new DownloadThread[threadNum];
        this.targetFile = targetFile;
    }

    public void download() throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "Accept",
                "image/gif,image/jpeg,image/pjpeg,application/x-shockwaveflash,application/x-ms-xbap,application/xaml+xml,application/vnd.ms-xpsdocument,application/x-ms-application,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/msword,*/*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty(
                "User-Agent",
                "Mozilla/4.0(compatible;MSIE7.0;Windows NT 5.2;Trident/4.0;.NET CLR 1.1.4322;.NET CLR 2.0.50727;.NET CLR 3.0.04506.30;.NET CLR 3.0.4506.2152;.NET CLR 3.5.30729)");

        conn.setRequestProperty("Connection", "Keep-Alive");
        // 得到文件大小
        fileSize = conn.getContentLength();
        conn.disconnect();
        int currentPartSize = fileSize / threadNum + 1;
        RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
        // 设置本地文件的大小
        file.setLength(fileSize);
        file.close();
        for (int i = 0; i < threadNum; i++) {
            // 计算每条线程的下载的开始位置
            int startPos = i * currentPartSize;
            // 每个线程使用一个RandomAccessFile进行下载
            RandomAccessFile currentPart = new RandomAccessFile(targetFile,
                    "rw");
            // 定位该线程的下载位置
            currentPart.seek(startPos);
            // 创建下载线程
            threads[i] = new DownloadThread(startPos, currentPartSize, currentPart);
            // 启动下载线程
            threads[i].start();
        }
    }

    /**
     * 获取下载完成的百分比
     *
     * @return
     */
    public double getCompleteRate() {
        // 统计多条线程已经下载的总大小
        int sumSize = 0;
        for (int i = 0; i < threadNum; i++) {
            sumSize += threads[i].length;
        }
        // 返回已经完成的百分比
        return sumSize * 1.0 / fileSize;
    }

    private class DownloadThread extends Thread {
        // 当前线程的下载位置
        private int startPos;
        // 定义当前线程负责下载的文件大小
        private int currentPartSize;
        // 当前线程需要下载的文件块
        private RandomAccessFile currentPart;
        // 定义该线程已下载的字节数
        private int length = 0;

        public DownloadThread(int startPos, int currentPartSize,
                              RandomAccessFile currentPart) {
            this.startPos = startPos;
            this.currentPartSize = currentPartSize;
            this.currentPart = currentPart;
        }

        public void run() {
            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty(
                        "Accept",
                        "image/gif,image/jpeg,image/pjpeg,application/x-shockwaveflash,application/x-ms-xbap,application/xaml+xml,application/vnd.ms-xpsdocument,application/x-ms-application,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/msword,*/*");
                conn.setRequestProperty("Accept-Language", "zh-CN");
                conn.setRequestProperty("Charset", "UTF-8");
                InputStream is = conn.getInputStream();
                // 跳过startPos个字符，表明该线程只下载自己负责那部分文件
                is.skip(startPos);
                byte[] by = new byte[1024];
                int hasRead = 0;
                // 读取网络数据，并写入本地文件
                while (length < currentPartSize
                        && (hasRead = is.read(by)) != -1) {
                    currentPart.write(by, 0, hasRead);
                    // 累计该线程下载的总大小
                    length += hasRead;
                }
                currentPart.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
