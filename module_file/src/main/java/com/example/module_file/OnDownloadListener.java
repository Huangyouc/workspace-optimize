package com.example.module_file;

public interface OnDownloadListener {
  /**
   * 下载成功
   */
  void onDownloadSuccess();

  /**
   * @param totalBytes 总大小
   * @param downloadedBytes 已经下载
   * @param progress 下载进度
   */
  void onDownloading(long totalBytes, long downloadedBytes, int progress);

  /**
   * 下载失败
   */
  void onDownloadFailed();
}
