package com.yang.androidaar.tool;


import com.yang.androidaar.Define;
import com.yang.androidaar.FileTool;
import com.yang.androidaar.LogUtil;
import com.yang.androidaar.MyCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper {
    // 日志登等级
    public @interface EHttpCode {
        int Ok = 200;
        int Fail = -1;
        int Exception = -2;
        int UrlError = -3;
    }

    public static class SHttp {
        public int code;
        public String msg;
        public boolean isRsp;
        public byte[] bytes;

        public String toString() {
            return String.format(LogUtil.PGFmt("--- code: %d, msg: %s"), code, msg);
        }
    }

    public interface HttpRunnable {
        void run(final SHttp rsp);
    }

    public static SHttp okhttpPostSync(final String url, final String jsonMsg) {
        SHttp rsp = new SHttp();
        new Thread(() -> { // 防止主线程调用
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonMsg);
            Request post = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(post);
            try {
                Response response = call.execute();
                rsp.code = response.code();
                rsp.bytes = response.body().bytes();
                rsp.msg = new String(rsp.bytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                rsp.code = -2;
                rsp.msg = e.getMessage();
            } finally {
                rsp.isRsp = true;
            }
        }).start();

        while (!rsp.isRsp) { // 阻塞等待返回
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return rsp;
    }

    public static void okhttpPostAsync(final String url, final String jsonMsg, final HttpRunnable cb) {
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonMsg);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (cb != null) {
                    SHttp rsp = new SHttp();
                    rsp.code = EHttpCode.Fail;
                    rsp.msg = e.getMessage();
                    cb.run(rsp);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb != null) {
                    SHttp rsp = new SHttp();
                    rsp.code = response.code();
                    rsp.bytes = response.body().bytes();
                    rsp.msg = new String(rsp.bytes, StandardCharsets.UTF_8);
                    cb.run(rsp);
                }
            }
        });
    }


    public static SHttp okhttpGetSync(final String url, Map<String, Object> params) {
        SHttp rsp = new SHttp();
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            rsp.code = EHttpCode.UrlError;
            rsp.msg = String.format("parse url error, url: %s", url);
            return rsp;
        }

        HttpUrl.Builder builder = httpUrl.newBuilder();
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addQueryParameter(key, params.get(key).toString());
            }
        }

        new Thread(() -> { // 防止主线程调用
            OkHttpClient okHttpClient = new OkHttpClient();
            Request post = new Request.Builder().url(builder.build()).get().build();
            Call call = okHttpClient.newCall(post);
            try {
                Response response = call.execute();
                rsp.code = response.code();
                rsp.bytes = response.body().bytes();
                rsp.msg = new String(rsp.bytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                rsp.code = EHttpCode.Exception;
                rsp.msg = e.getMessage();
            } finally {
                rsp.isRsp = true;
            }
        }).start();

        while (!rsp.isRsp) { // 阻塞等待返回
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return rsp;
    }

    public static void okhttpGetAsync(final String url, Map<String, Object> params, final HttpRunnable cb) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            if (cb != null) {
                SHttp rsp = new SHttp();
                rsp.code = EHttpCode.UrlError;
                rsp.msg = String.format("parse url error, url: %s", url);
                cb.run(rsp);
            }
            return;
        }

        HttpUrl.Builder builder = httpUrl.newBuilder();
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addQueryParameter(key, params.get(key).toString());
            }
        }

        Request request = new Request.Builder()
                .url(builder.build())
                .get()
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (cb != null) {
                    SHttp rsp = new SHttp();
                    rsp.code = EHttpCode.Fail;
                    rsp.msg = e.getMessage();
                    cb.run(rsp);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb != null) {
                    SHttp rsp = new SHttp();
                    rsp.code = response.code();
                    rsp.bytes = response.body().bytes();
                    rsp.msg = new String(rsp.bytes, StandardCharsets.UTF_8);
                    cb.run(rsp);
                }
            }
        });
    }

    public static void okhttpDownloadAsync(final String url, String savePath, Define.CodeRunnable resultCb, Define.CodeRunnable processCb) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                try {
                    resultCb.run(MyCode.ECode.Unknown, String.format("--- Failure: %s", e.getMessage()));
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                FileOutputStream fos = null;

                byte[] buf = new byte[2048];
                int len = 0;

                try {


                    File dstFile = new File(savePath);
                    File tmpFile = new File(savePath + ".temp");
                    if (tmpFile.exists()) { // 删除临时文件
                        LogUtil.D("--- del temp file: %s", tmpFile.getAbsolutePath());
                        tmpFile.delete();
                    }

                    // 储存下载文件的目录
                    File dir = new File(savePath).getParentFile();
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(tmpFile);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;

                        if (processCb != null) {
                            try {
                                processCb.run((int) (sum * 1.0f / total * 100), null);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    fos.flush();

                    if (tmpFile.exists()) {
                        tmpFile.renameTo(dstFile); // 重命名为正确文件
                    }

                    try {
                        resultCb.run(MyCode.ECode.Ok, savePath);
                    } catch (Exception ignored) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        resultCb.run(MyCode.ECode.Unknown, String.format("--- Unknown: %s", e.getMessage()));
                    } catch (Exception ignored) {
                    }
                } finally {
                    FileTool.close(is);
                    FileTool.close(fos);
                }
            }
        });
    }

}
