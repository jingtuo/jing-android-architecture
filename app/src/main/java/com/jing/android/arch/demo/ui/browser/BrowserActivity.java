package com.jing.android.arch.demo.ui.browser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.jing.android.arch.component.BaseActivity;
import com.jing.android.arch.demo.R;

import java.io.File;

/**
 * 浏览器
 * @author JingTuo
 */
@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        WebView webView = findViewById(R.id.web_view);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        File cacheFolder = getExternalCacheDir();
        if (cacheFolder == null) {
            cacheFolder = getCacheDir();
        }
        cacheFolder = new File(cacheFolder, "browser");
        settings.setAppCachePath(cacheFolder.getAbsolutePath());
        webView.loadUrl("https://www.baidu.com");
    }
}
