package com.example.caloriengine

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.caloriengine.widget.MetabolicHudWidgetProvider
import com.example.caloriengine.widget.QuickAddWidgetProvider

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    inner class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun saveTheme(theme: String) {
            val prefs = context.getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
            prefs.edit().putString("cyber_theme", theme).apply()
            QuickAddWidgetProvider.triggerWidgetUpdate(context)
            MetabolicHudWidgetProvider.triggerWidgetUpdate(context)
        }

        @JavascriptInterface
        fun saveGoal(goal: String) {
            val prefs = context.getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
            prefs.edit().putString("cyber_goal", goal).apply()
            QuickAddWidgetProvider.triggerWidgetUpdate(context)
            MetabolicHudWidgetProvider.triggerWidgetUpdate(context)
        }

        @JavascriptInterface
        fun syncTarget(target: Int) {
            val prefs = context.getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
            prefs.edit().putInt("cyber_target", target).apply()
            QuickAddWidgetProvider.triggerWidgetUpdate(context)
            MetabolicHudWidgetProvider.triggerWidgetUpdate(context)
        }

        @JavascriptInterface
        fun syncStorage(consumed: Int, unitsJson: String) {
            val prefs = context.getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
            prefs.edit()
                .putInt("cyber_consumed", consumed)
                .putString("cyber_units", unitsJson)
                .apply()
            QuickAddWidgetProvider.triggerWidgetUpdate(context)
            MetabolicHudWidgetProvider.triggerWidgetUpdate(context)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide ActionBar for immersive Cyberpunk feel
        supportActionBar?.hide()

        // Enable WebView debugging via Chrome DevTools
        WebView.setWebContentsDebuggingEnabled(true)

        webView = WebView(this)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.addJavascriptInterface(WebAppInterface(this), "AndroidHost")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Page finished loading: $url")
                syncQuickLogFromPrefs()
                checkQuickLogIntent(intent)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.d("WebViewConsole", "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                return true
            }
        }

        webView.loadUrl("file:///android_asset/index.html")
        setContentView(webView)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        syncQuickLogFromPrefs()
        checkQuickLogIntent(intent)
    }

    private fun syncQuickLogFromPrefs() {
        val prefs = getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
        val consumed = prefs.getInt("cyber_consumed", -1)
        val target = prefs.getInt("cyber_target", -1)
        val units = prefs.getString("cyber_units", null)
        val theme = prefs.getString("cyber_theme", "GREEN") ?: "GREEN"
        val goal = prefs.getString("cyber_goal", "MAINTAIN") ?: "MAINTAIN"

        if (consumed >= 0) {
            webView.evaluateJavascript("localStorage.setItem('cyber_consumed', '$consumed');", null)
        }
        if (target >= 0) {
            webView.evaluateJavascript("localStorage.setItem('cyber_target', '$target');", null)
        }
        if (!units.isNullOrEmpty()) {
            val escapedUnits = units.replace("'", "\\'")
            webView.evaluateJavascript("localStorage.setItem('cyber_units', '$escapedUnits');", null)
        }
        webView.evaluateJavascript("localStorage.setItem('cyber_theme', '$theme');", null)
        webView.evaluateJavascript("localStorage.setItem('cyber_goal', '$goal');", null)
    }

    private fun checkQuickLogIntent(intent: Intent?) {
        val mealExtra = intent?.getStringExtra("QUICK_LOG_MEAL")
        if (!mealExtra.isNullOrEmpty()) {
            Log.d("QuickAddWidget", "Quick log triggered for: $mealExtra")
            webView.postDelayed({
                webView.evaluateJavascript("if(window.triggerQuickLog){ window.triggerQuickLog('$mealExtra'); }", null)
            }, 300)
            intent.removeExtra("QUICK_LOG_MEAL")
        }
    }
}
