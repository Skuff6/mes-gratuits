package fr.hugo.mesgratuits;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0xFF07070F);
        getWindow().setNavigationBarColor(0xFF07070F);

        web = new WebView(this);
        web.setBackgroundColor(0xFF07070F);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        web.addJavascriptInterface(new Bridge(this), "Android");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String u = r.getUrl().toString();
                if (u.startsWith("http")) {
                    ouvrirLien(u);
                    return true;
                }
                return false;
            }
        });
        setContentView(web);

        String onglet = getIntent() != null ? getIntent().getStringExtra("onglet") : null;
        web.loadUrl("file:///android_asset/index.html" + (onglet != null ? "#" + onglet : ""));

        Notif.creerCanal(this);
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        Planif.programmer(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String onglet = intent.getStringExtra("onglet");
        if (onglet != null && web != null) {
            web.evaluateJavascript("window.ouvrirOnglet && window.ouvrirOnglet('" + onglet.replaceAll("[^a-z]", "") + "')", null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) web.evaluateJavascript("window.actualiser && window.actualiser(false)", null);
    }

    @Override
    public void onBackPressed() {
        web.evaluateJavascript("window.retour ? window.retour() : false", v -> {
            if (!"true".equals(v)) MainActivity.super.onBackPressed();
        });
    }

    void ouvrirLien(String u) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(u));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception ignored) {
        }
    }
}
