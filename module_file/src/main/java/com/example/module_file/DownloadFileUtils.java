package com.example.module_file;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.module_base.fileutil.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class DownloadFileUtils {
  public static void download(final File file, String uri, final OnDownloadListener downloadStatusListener) {
    download(file, uri, downloadStatusListener, false);
  }

  public static void download(final File file, String uri, final OnDownloadListener downloadStatusListener, boolean contentIsStream) {
        download(file, uri, 0, 0, downloadStatusListener, contentIsStream);

    }

  public static void download(final File file, final String url, int timeout, int retryTimes, final OnDownloadListener listener) {
        download(file, url, timeout, retryTimes, listener, false);
    }  /**
   * @param url      下载连接
   * @param file     储存下载文件的SDCard目录
   * @param listener 下载监听
   *@param contentIsStream 是否是以流的形式下载
     */
  public static void download(final File file, final String url, int timeout, int retryTimes,
      final OnDownloadListener listener, boolean contentIsStream) {
    if (retryTimes == 0)
      retryTimes = 1;
    if (timeout == 0)
      timeout = 10 * 1000;
    if (file.exists() && file.isDirectory()) {
      FileUtil.deleteFileSafely(file);
      Log.d("HttpConnecter", "DOWNLOAD FAILURE: file exist");
    }

    OkHttpClient okHttpClient = null;
    try {
      file.getParentFile().mkdirs();
      file.createNewFile();

//      HashMap<String, String> mapDown = SignUtil.sign(url);
      String token = "";

      Request.Builder requestBuilder = new Request.Builder().url(url);
//            requestBuilder = requestBuilder.addHeader("X-KGW-SID", mapDown.get("sid"))
//          .addHeader("X-KGW-T", mapDown.get("t")).addHeader("X-KGW-SIGN", mapDown.get("sign"))
//          .addHeader("X-KGW-AGENT", mapDown.get("agent")).addHeader("X-KGW-UID", Ifa.X_KGW_UID)
//          .addHeader("X-KGW-IVER", BuildConfig.VERSION_INTERFACE)
//          .addHeader("User-Agent", UserAgentUtil.getUserAgentInfo()).addHeader("X-KGW-GROUP-NO", Ifa.X_KGW_GROUP_NO)
//          .addHeader("Accept-Encoding", "identity");
//      if (!StringUtils.isEmpty(token)) {
//        requestBuilder.addHeader("nfp-token", token);
//      }
      if (contentIsStream) {
        requestBuilder.addHeader("Content-Type", "application/octet-stream");
      }

      HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
      logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

      Request request = requestBuilder.build();
      Headers requestHeaders = request.headers();
      int requestHeadersLength = requestHeaders.size();
      // for (int i = 0; i < requestHeadersLength; i++) {
      // String headerName = requestHeaders.name(i);
      // String headerValue = requestHeaders.get(headerName);
      // System.out.print("下载header----------->Name:" + headerName +
      // "------------>Value:" + headerValue + "\n");
      // }

      //放开证书校验，生产https证书部分设备不信任
            final X509TrustManager trustManager = new X509TrustManager() {
                @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            okHttpClient = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.MILLISECONDS)
          .readTimeout(timeout * 2, TimeUnit.MILLISECONDS)
          // .addNetworkInterceptor(logInterceptor)
          .addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
              // 拦截
              Response originalResponse = chain.proceed(chain.request());

              return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), listener))
                  .build();
            }
          }).addInterceptor(new RetryIntercepter(retryTimes))// 重试
          .sslSocketFactory(sslSocketFactory, trustManager)
                    .hostnameVerifier(hostnameVerifier)
//        .dns(new HttpDns())
                    .build();

      okHttpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          // 下载失败
          listener.onDownloadFailed();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          InputStream is = null;
          byte[] buf = new byte[2048];
          int len = 0;
          FileOutputStream fos = null;
          // 储存下载文件的目录
          try {
            is = response.body().byteStream();
            // long total = response.body().contentLength();
            fos = new FileOutputStream(file);
            // long sum = 0;
            while ((len = is.read(buf)) != -1) {
              fos.write(buf, 0, len);
              // sum += len;
              // int progress = (int) (sum * 1.0f / total * 100);
              // LogDebugger.error("下载进度",""+progress);
              // 下载中
            }
            fos.flush();
            // 下载完成
            listener.onDownloadSuccess();
          } catch (Exception e) {
            listener.onDownloadFailed();
          } finally {
            try {
              if (is != null)
                is.close();
            } catch (IOException e) {
            }
            try {
              if (fos != null)
                fos.close();
            } catch (IOException e) {
            }
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      listener.onDownloadFailed();
    }

  }

  /**
   * @param saveDir
   * @return
   * @throws IOException 判断下载目录是否存在
   */
  private String isExistDir(String saveDir) throws IOException {
    // 下载位置
    File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
    if (!downloadFile.mkdirs()) {
      downloadFile.createNewFile();
    }
    String savePath = downloadFile.getAbsolutePath();
    return savePath;
  }

  /**
   * @param url
   * @return 从下载连接中解析出文件名
   */
  @NonNull
  private String getNameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }

}
