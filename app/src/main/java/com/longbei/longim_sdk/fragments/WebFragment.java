package com.longbei.longim_sdk.fragments;


import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.longbei.im_push_service_sdk.app.intercepter.CacheType;
import com.longbei.im_push_service_sdk.app.intercepter.HttpCacheInterceptor;
import com.longbei.im_push_service_sdk.common.app.Fragment;
import com.longbei.im_push_service_sdk.common.utils.MimeTypeMapUtils;
import com.longbei.im_push_service_sdk.common.utils.NetUtils;
import com.longbei.longim_sdk.R;
import com.longbei.longim_sdk.widgets.WebProgress;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.longbei.im_push_service_sdk.common.Common.KEY_CACHE;

public class WebFragment extends Fragment {

    private WebView mWV;
    private TabLayout myTab;
    private OkHttpClient mHttpClient = null;
    private File mCacheFile;
    private long mCacheSize = 100 * 1024 * 1024;
    private long mConnectTimeout = 20;
    private long mReadTimeout = 20;
    private String[] urls;
    private String mOrigin = "";
    private String mReferer = "";
    private String mUserAgent = "";
    private static HashSet STATIC = new HashSet() {
        {
            add("html");
            add("htm");
            add("js");
            add("ico");
            add("css");
            add("png");
            add("jpg");
            add("jpeg");
            add("gif");
            add("bmp");
            add("ttf");
            add("woff");
            add("woff2");
            add("otf");
            add("eot");
            add("svg");
            add("xml");
            add("swf");
            add("txt");
            add("text");
            add("conf");
            add("webp");
        }
    };
    private static HashSet NO_CACH = new HashSet() {
        {
            add("mp4");
            add("mp3");
            add("ogg");
            add("avi");
            add("wmv");
            add("flv");
            add("rmvb");
            add("3gp");
        }
    };
    //??????webview?????????
    private HashSet statics = new HashSet(STATIC);
    private HashSet no_cache = new HashSet(NO_CACH);
    private CacheType mCacheType;
    private WebProgress mProgress;

    public WebFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_web_index;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mWV = (WebView) root.findViewById(R.id.ad_wv);
        myTab = (TabLayout) root.findViewById(R.id.my_tab);
        mProgress = (WebProgress) root.findViewById(R.id.progress);
        mProgress.setColor("#D81B60");             // ????????????
        mProgress.setColor("#00D81B60","#D81B60"); // ???????????????
        urls = getActivity().getResources().getStringArray(R.array.urls);
        mCacheType = CacheType.FORCE;

        initHttpClient();
        initSettings();
        initWebView();
        initTab();

    }

    private void initTab() {
        myTab.addTab(myTab.newTab().setText("25?????????"));
        myTab.addTab(myTab.newTab().setText("??????"));
        myTab.addTab(myTab.newTab().setText("?????????"));
        myTab.addTab(myTab.newTab().setText("?????????"));
        myTab.addTab(myTab.newTab().setText("?????????"));
        myTab.addTab(myTab.newTab().setText("?????????"));

        //myTab.getTabAt(0).select();
        mWV.loadUrl(urls[0]);

        myTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(getActivity(), tab.getText(), Toast.LENGTH_SHORT).show();
                mWV.loadUrl(urls[tab.getPosition()]);
                mProgress.show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initHttpClient() {
        mCacheFile = new File(getActivity().getCacheDir().toString(), "LBCacheWebViewCache");
        final Cache cache = new Cache(mCacheFile, mCacheSize);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(mReadTimeout, TimeUnit.SECONDS)
                .addNetworkInterceptor(new HttpCacheInterceptor());
        mHttpClient = builder.build();
    }


    private void initSettings() {
        WebSettings webSettings = mWV.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setDefaultTextEncodingName("UTF-8");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
    }

    private void initWebView() {
        mWV.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgress.setWebProgress(newProgress);
            }
        });
        mWV.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                //html??????????????????????????????????????????
                if (!NetUtils.isConnected(getActivity())) {
                    mProgress.hide();
                }
                super.onPageFinished(view, url);
            }



            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWV.loadUrl(url);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                int code = error.getErrorCode();
                String resp = error.getDescription().toString();
                String url = request.getUrl().toString();
                super.onReceivedError(view, request, error);
            }


            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (mCacheType == CacheType.NORMAL) {
                    return null;
                }
                if (!checkUrl(url)) {
                    return null;
                }

                mReferer = view.getUrl();
                mOrigin = NetUtils.getOriginUrl(mReferer);
                mUserAgent = view.getSettings().getUserAgentString();

                return getWebResourceResponse(url, buildHeaders());

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (mCacheType == CacheType.NORMAL) {
                    return null;
                }
                if (!checkUrl(url)) {
                    return null;
                }

                return getWebResourceResponse(url,request.getRequestHeaders());

            }


        });
    }

    private Map<String, String> buildHeaders() {

        Map<String, String> headers = new HashMap<String, String>();
        if (!TextUtils.isEmpty(mOrigin)) {
            headers.put("Origin", mOrigin);
        }
        if (!TextUtils.isEmpty(mReferer)) {
            headers.put("Referer", mReferer);
        }
        if (!TextUtils.isEmpty(mUserAgent)) {
            headers.put("User-Agent", mUserAgent);
        }
        return headers;
    }

    public boolean isMedia(String extension) {
        if (TextUtils.isEmpty(extension)) {
            return false;
        }
        if (NO_CACH.contains(extension)) {
            return true;
        }
        return no_cache.contains(extension.toLowerCase().trim());
    }

    public boolean canCache(String extension) {

        if (TextUtils.isEmpty(extension)) {
            return false;
        }
        extension = extension.toLowerCase().trim();
        if (STATIC.contains(extension)) {
            return true;
        }
        return statics.contains(extension);

    }


    private WebResourceResponse getWebResourceResponse(String url, Map<String, String> headers) {
        try {

            Request.Builder reqBuilder = new Request.Builder()
                    .url(url);

            String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);

            if (isHtml(extension)) {
                headers.put(KEY_CACHE, mCacheType.ordinal() + "");
            }
            addHeader(reqBuilder, headers);

            if (!NetUtils.isConnected(getActivity())) {
                reqBuilder.cacheControl(CacheControl.FORCE_CACHE);
            }
            Request request = reqBuilder.build();
            Response response = mHttpClient.newCall(request).execute();
            Response cacheRes = response.cacheResponse();
            System.out.println("--------------------");
            System.out.println("???????????? network response = " + response.networkResponse());
            System.out.println("???????????? cache response = " + cacheRes);
            System.out.println("--------------------");
            String mimeType = MimeTypeMapUtils.getMimeTypeFromUrl(url);
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", response.body().byteStream());
            if (response.code() == 504 && !NetUtils.isConnected(getActivity())){
                return null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String message = response.message();
                if (TextUtils.isEmpty(message)) {
                    message = "OK";
                }
                try {
                    webResourceResponse.setStatusCodeAndReasonPhrase(response.code(), message);
                } catch (Exception e) {
                    return null;
                }
                webResourceResponse.setResponseHeaders(NetUtils.multimapToSingle(response.headers().toMultimap()));
            }
            return webResourceResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        //okhttp only deal with http[s]
        if (!url.startsWith("http")) {
            return false;
        }

        String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);


        if (TextUtils.isEmpty(extension)) {
            return false;
        }
        if (isMedia(extension)) {
            return false;
        }
        if (!canCache(extension)) {
            return false;
        }

        return true;
    }


    public void addHeader(Request.Builder reqBuilder, Map<String, String> headers) {

        if (headers == null) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            reqBuilder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    public boolean isHtml(String extension) {
        if (TextUtils.isEmpty(extension)) {
            return false;
        }
        if (extension.toLowerCase().contains("html") ||
                extension.toLowerCase().contains("htm")) {
            return true;
        }
        return false;
    }

}
