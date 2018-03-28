package com.chatcamp.plugin;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.content.res.Resources;

public class WebViewActivity extends AppCompatActivity {
    public static final String URL = "url_link";

    WebView webView;

    private Resources resources;
    private String package_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        package_name = getApplication().getPackageName();
        resources = getApplication().getResources();
        setContentView(resources.getIdentifier("activity_web_view", "layout", package_name));
        webView = findViewById(resources.getIdentifier("webview", "id", package_name));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);
        openWebView(url);
    }

    private void openWebView(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        String data = String.format("<img src='%s'>", url);
        webView.loadDataWithBaseURL("file:///android_asset/", getHtmlData(data), "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
}
