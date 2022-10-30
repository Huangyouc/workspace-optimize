package com.example.module_base.request;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.noahwm.crm.BuildConfig;
import com.noahwm.crm.Constant;
import com.noahwm.crm.IFAApplication;
import com.noahwm.crm.Ifa;
import com.noahwm.crm.model.responseBodyModel;
import com.noahwm.king.framework.cache.ACache;
import com.noahwm.king.framework.config.Assembly;
import com.noahwm.king.framework.log.LogDebugger;
import com.noahwm.king.framework.network.SSLSocketClient;
import com.noahwm.king.framework.network.okhttp.helper.ApiCall;
import com.noahwm.king.framework.network.okhttp.helper.ReqConfig;
import com.noahwm.king.framework.util.CommonUtil;
import com.noahwm.king.framework.util.EnvUtil;
import com.noahwm.king.framework.util.GsonUtil;
import com.noahwm.king.framework.util.JsonUtils;
import com.noahwm.king.framework.util.MD5Util;
import com.noahwm.king.framework.util.SharedPreferencesUtil;
import com.noahwm.king.framework.util.SignUtil;
import com.noahwm.king.framework.util.StringUtils;
import com.noahwm.king.framework.util.UserAgentUtil;
import com.noahwm.kotlin.util.httpdns.HttpDns;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Bing
 * 网络模块 为对接中台用
 */
public class OkHttpUtil {

    public static final String CHANNEL_ZX = "zx";
    public static final String CHANNEL_GOPHER = "gopher";

    private static LoggingInterceptor logInterceptor = new LoggingInterceptor();
    private static DynamicConnectTimeoutInterceptor timeoutInterceptor = new DynamicConnectTimeoutInterceptor();
    private static OkHttpClient client = null;

    private synchronized static OkHttpClient getClient() {
        if (client == null) {
            synchronized (OkHttpUtil.class){
                client = new OkHttpUtil();
            }

        }
        return client;
    }

    private OkHttpUtil() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.dns(new HttpDns());
        clientBuilder.connectTimeout(30000, TimeUnit.MILLISECONDS);
        if (Assembly.debugMode) {
            clientBuilder.readTimeout(17000, TimeUnit.MILLISECONDS);
        } else {
            clientBuilder.readTimeout(16000, TimeUnit.MILLISECONDS);
        }

        X509TrustManager trustManager = new X509TrustManager(){
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        try {
            SSLContext  sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {trustManager};
            sslContext.init(null,tm, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            clientBuilder.sslSocketFactory(ssf, trustManager);//配置
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        clientBuilder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
//        clientBuilder.retryOnConnectionFailure(false);
        clientBuilder.addInterceptor(logInterceptor);
        clientBuilder.addInterceptor(timeoutInterceptor);

        client = clientBuilder.build();
        client.dispatcher().setMaxRequestsPerHost(10);
    }

    /******************** 多渠道支持 start ********************/

    /**
     * post 请求加渠道
     * @param urlPost
     * @param json
     * @param callback
     * @param channelName
     */
    public static void postChannel(String urlPost, String json, ResponseCallListener callback, String channelName){
        LogDebugger.debug("okhttp postChannel=", json +" channelName=" +channelName);

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);

        //组装请求
        Request request = createRequestBuilder(json, channelName)
                .url(Constant.DOMAIN_MIDDLE + urlPost)
                .post(body)
                .build();

        Call call = getClient().newCall(request);
        if (callback != null) {
            callback.setCall(call);
            callback.start();

            //执行请求
            call.enqueue(callback);
        }

    }

    /**
     * get 请求加渠道
     * @param urlParams
     * @param callback
     * @param channelName
     */
    public static void getChannel(String urlParams, ResponseCallListener callback, String channelName){
        LogDebugger.debug("okhttp getChannel=", urlParams +" channelName="+channelName);
        //组装请求
        Request request = createRequestBuilder(Constant.DOMAIN_MIDDLE +urlParams, channelName)
                .url(Constant.DOMAIN_MIDDLE +urlParams)
                .get()
                .build();

        Call call = getClient().newCall(request);
        if (callback != null) {
            callback.setCall(call);
            callback.start();

            //执行请求
            call.enqueue(callback);
        }
    }

    /**
     * RN 请求用加渠道
     * @param pathUrl
     * @param postJsonParams
     * @param callback
     * @param channelName
     */
    public static void postGetRNChannel(String pathUrl, String postJsonParams,ResponseCallListener callback, String channelName){

        if(StringUtils.isEmptyNull(postJsonParams)){
            getChannel(pathUrl,callback, channelName);
        }else {
            postChannel(pathUrl, postJsonParams, callback, channelName);
        }
    }

    /**
     * flutter 请求用加渠道
     * @param pathUrl
     * @param postJsonParams
     * @param callback
     * @param channelName
     */
    public static void postGetFlutterChannel(String pathUrl, String postJsonParams,ResponseCallListener callback, String channelName){

        if(StringUtils.isEmptyNull(postJsonParams)){
            getChannel(pathUrl,callback, channelName);
        }else {
            postChannel(pathUrl, postJsonParams, callback, channelName);
        }
    }
    /******************** 多渠道支持 end ********************/

    /**
     * post 请求
     * @param json
     * @param callback
     */
    public static void post( String urlPost, String json, ResponseCallListener callback) {
        post(urlPost,json,callback,CHANNEL_ZX);
    }
    public static void post( String urlPost, String json, ResponseCallListener callback,String channelName) {
        LogDebugger.debug("okhttp Post=", json);

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        String url =  urlPost;
        if(StringUtils.isNotEmpty(url) &&  (url.startsWith("http://")|| url.startsWith("https://")) && Assembly.debugMode){
            url = urlPost;
        }else{
            url = Constant.DOMAIN_MIDDLE + urlPost;
        }
        //组装请求
        Request request = createRequestBuilder(json, channelName)
                .url(url)//Constant.DOMAIN
                .post(body)
                .build();

        Call call = getClient().newCall(request);
        if (callback != null) {
            callback.setCall(call);
            callback.start();

            //执行请求
            call.enqueue(callback);
        }
    }
    //同步post请求
    public static String postsync( String urlPost, String json) {
        LogDebugger.debug("okhttp postsync=", json);

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        String url =  urlPost;
        if(StringUtils.isNotEmpty(url) &&  (url.startsWith("http://")|| url.startsWith("https://")) && Assembly.debugMode){
            url = urlPost;
        }else{
            url = Constant.DOMAIN_MIDDLE + urlPost;
        }
        //组装请求
        Request request = createRequestBuilder(json, CHANNEL_ZX)
                .url(url)//Constant.DOMAIN
                .post(body)
                .build();

        Call call = getClient().newCall(request);
        try {
            Response response = call.execute();
            String responseBody = response.body().string();
            LogDebugger.debug("okhttp postsync  responseBody=", responseBody);
            if (responseBody.startsWith("{") && responseBody.endsWith("}")) {
                responseBodyModel mapBody = JsonUtils.bindData(responseBody, responseBodyModel.class);
                String code = mapBody.getCode();
                if ("0".equals(code)) {
                    String responseStr = mapBody.getResponse();
                    return responseStr;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * get 请求
     * @param urlParams
     * @param callback
     */
    public static void get(String urlParams, ResponseCallListener callback){
        get(urlParams,callback,CHANNEL_ZX);
    }
    public static void get(String urlParams, ResponseCallListener callback,String channelName){
        LogDebugger.debug("okhttp urlParams=", urlParams);
        String url =  urlParams;
        if(url.startsWith("http://") && Assembly.debugMode){
            url = urlParams;
        }else{
            url = Constant.DOMAIN_MIDDLE + urlParams;
        }
        //组装请求
        Request request = createRequestBuilder(url, channelName)
                .url(url)
                .get()
                .build();

        Call call = getClient().newCall(request);
        if (callback != null) {
            callback.setCall(call);
            callback.start();

            //执行请求
            call.enqueue(callback);
        }
    }
    /**
     * path, postStr, method, showLoading, isShowNativeToast,
     * postJsonParams为空为get请求
     */
    public static void postGetRN(String pathUrl, String postJsonParams,ResponseCallListener callback){

        if(StringUtils.isEmptyNull(postJsonParams)){
            //postJsonParams为空是get请求
            get(pathUrl,callback);
        }else {
            post(pathUrl, postJsonParams, callback);
        }
    }

    /**
     * 带缓存功能
     * @param json
     * @param callback
     * @param urlPost
     */
    public static void postCache( String urlPost, String json, ResponseCallListener callback) {
        LogDebugger.debug("okhttp postCache=", json);
        final ACache aCache = ACache.get(EnvUtil.getApplicationContext());
        final String cacheIndex = MD5Util.md5(urlPost + json + CommonUtil.getVersionCode(EnvUtil.getApplicationContext()));
        String data = aCache.getAsString(cacheIndex);
        if (!StringUtils.isEmptyNull(data)) {
            try {
                callback.onCache(true);
                callback.hasCacheMiddle(true);//设置有缓存数据
                responseBodyModel mapBody = JsonUtils.bindData(data, responseBodyModel.class);
                callback.success(mapBody.getResponse());
                callback.successRN(data);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            callback.hasCacheMiddle(false);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        //组装请求
        Request request = createRequestBuilder(json, CHANNEL_ZX)
                .url(Constant.DOMAIN_MIDDLE + urlPost)
                .post(body)
                .build();

        Call call = getClient().newCall(request);
        if (callback != null) {
            callback.setCacheKey(cacheIndex,true);
            callback.setACache(aCache);

            callback.setCall(call);
            callback.start();

            //执行请求
            call.enqueue(callback);
        }
    }

    /**
     * 带缓存功能
     * get 请求
     * @param urlParams
     * @param callback
     */
    public static void getCache(String urlParams, ResponseCallListener callback){
        LogDebugger.debug("okhttp  getCache urlParams=", urlParams);

        final ACache aCache = ACache.get(EnvUtil.getApplicationContext());
        final String cacheIndex = MD5Util.md5(urlParams + CommonUtil.getVersionCode(EnvUtil.getApplicationContext()));
        String data = aCache.getAsString(cacheIndex);

        if (!StringUtils.isEmptyNull(data)) {
            try {
                callback.hasCacheMiddle(true);//设置有缓存数据
                responseBodyModel mapBody = JsonUtils.bindData(data, responseBodyModel.class);
                callback.success(mapBody.getResponse());
                callback.successRN(data);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            callback.hasCacheMiddle(false);
        }

        //组装请求
        Request request = createRequestBuilder(Constant.DOMAIN_MIDDLE +urlParams, CHANNEL_ZX)
                .url(Constant.DOMAIN_MIDDLE +urlParams)
                .get()
                .build();

        Call call = getClient().newCall(request);
        if (callback != null) {
            callback.setCacheKey(cacheIndex,true);
            callback.setACache(aCache);

            callback.setCall(call);
            callback.start();

            //执行请求
            call.enqueue(callback);
        }
    }

    /**
     *  返回基础 Request.Builder
     * @param json
     * @return  Request.Builder
     */
    private static Request.Builder createRequestBuilder(String json, String channelName){

        HashMap<String, String> map = SignUtil.sign(json);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("X-KGW-SID", map.get("sid"));
        requestBuilder.addHeader("X-KGW-T", map.get("t"));
        requestBuilder.addHeader("X-KGW-SIGN", map.get("sign"));
        requestBuilder.addHeader("X-KGW-AGENT", map.get("agent"));
        requestBuilder.addHeader("X-KGW-UID", Ifa.X_KGW_UID);
        requestBuilder.addHeader("X-KGW-GROUP-NO", Ifa.X_KGW_GROUP_NO);//增加一个集团号
        requestBuilder.addHeader("X-KGW-IVER", BuildConfig.VERSION_INTERFACE);
        requestBuilder.addHeader("User-Agent", UserAgentUtil.getUserAgentInfo());

        String tokenNBP = SharedPreferencesUtil.getTokenNBP(IFAApplication.getInstance());
        if (!TextUtils.isEmpty(tokenNBP)) {//temp_TOKEN
            requestBuilder.addHeader("access-token", tokenNBP);
        }
        requestBuilder.addHeader("nbp-app-name", "zx_crm_app");//Ifa.APP_NAME
        requestBuilder.addHeader("channel", channelName);//"zx"
        SharedPreferencesUtil.setChannelName(IFAApplication.getInstance(),channelName);

        String token = SharedPreferencesUtil.getToken(IFAApplication.getInstance());
        if (!StringUtils.isEmpty(token)) {
            requestBuilder.addHeader("nfp-token", token);
        }
        requestBuilder.addHeader("app-name", "mnfp");
//        if (Assembly.debugMode) {
//            requestBuilder.addHeader("user-id", "5624");
//        }

        String distinctId = "";
        try {
            distinctId = SensorsDataAPI.sharedInstance().getDistinctId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotEmptyJava(distinctId)) {
            requestBuilder.addHeader("X-Distinct-Id", distinctId);
        }

        return requestBuilder;
    }


    /******************************** 多接口请求 start **********************************/

    @SuppressLint("CheckResult")
    public static void postMultiple(List<ReqConfig> requests, ApiCall apiCall) {
        if (requests == null || requests.size() == 0) {
            return;
        }

        if (apiCall != null) {
            apiCall.start();
        }
        List<Flowable<Map<String, String>>> list = new ArrayList<>();
        for (ReqConfig config : requests) {
            list.add(getFlowable(config, apiCall));
        }


        Flowable.zip(list, new Function<Object[], Map<String, String>>() {
            @Override
            public Map<String, String> apply(Object[] objects) throws Exception {
                Map<String, String> resultMap = new HashMap<>();
                for (Object obj : objects) {
                    Map<String, String> map = (Map<String, String>) obj;
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        resultMap.put(entry.getKey(), entry.getValue());
                    }
                }
                return resultMap;
            }
            //.subscribeOn(Schedulers.io())
            //.observeOn(AndroidSchedulers.mainThread())
        }).subscribeWith(new Subscriber<Map<String, String>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Map<String, String> results) {
                        //LogDebugger.info("okhttp response_multi", GsonUtil.bean2json(results));

                        if (apiCall != null) {
                            try {
                                apiCall.responseResults(results);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (apiCall != null) {
                            apiCall.parserError(t);
                        }
                        onComplete();
                    }


                    @Override
                    public void onComplete() {
                        if (apiCall != null) {
                            try {
                                apiCall.complete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private static Flowable<Map<String, String>> getFlowable(ReqConfig config, ApiCall callListener){
        LogDebugger.info(String.format("okhttp [requestId=%s]", config.getRequestId()), GsonUtil.bean2json(config));

        return Flowable.create(new FlowableOnSubscribe<Map<String, String>>() {
            @Override
            public void subscribe(FlowableEmitter<Map<String, String>> emitter) throws Exception {
                if (ReqConfig.POST.equals(config.getMethod())) {
                    requestPost(config, emitter, callListener);
                }else {
                    requestGet(config, emitter, callListener);
                }
            }
            }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * get 请求
     * @param config
     * @param emitter
     * @param callListener
     */
    private static void requestGet(ReqConfig config, FlowableEmitter<Map<String, String>> emitter, ApiCall callListener){
        String urlStr = String.format("%s%s", Constant.DOMAIN_MIDDLE, config.getPath());
        LogDebugger.debug("okhttp urlParams=", urlStr);
        //组装请求
        Request request = createRequestBuilder(urlStr,CHANNEL_ZX)
                .url(urlStr)
                .get()
                .build();

        Call call = getClient().newCall(request);
        if (callListener != null) {
            callListener.addCall(config.getRequestId(), call);

            //请求入队列
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callListener.onFailure(call, e, emitter);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callListener.onResponse(config, response, emitter);
                }

            });
        }
    }

    /**
     * post 请求
     * @param config
     * @param emitter
     * @param callListener
     */
    private static void requestPost(ReqConfig config, FlowableEmitter<Map<String, String>> emitter, ApiCall callListener){
        String urlStr = String.format("%s%s", Constant.DOMAIN_MIDDLE, config.getPath());

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), config.getParams());
        //组装请求
        Request request = createRequestBuilder(urlStr,CHANNEL_ZX)
                .url(urlStr)
                .post(body)
                .build();

        Call call = getClient().newCall(request);
        if (callListener != null) {
            callListener.addCall(config.getRequestId(), call);

            //请求入队列
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callListener.onFailure(call, e, emitter);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callListener.onResponse(config, response, emitter);
                }
            });

        }
    }

    /******************************** 多接口请求 end **********************************/

}


