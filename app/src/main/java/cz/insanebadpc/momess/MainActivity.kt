package cz.insanebadpc.momess

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        webView = createWebView().apply {
            setBackgroundColor(Color.parseColor("#0b0c0e"))
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                useWideViewPort = true
                loadWithOverviewMode = true
                mediaPlaybackRequiresUserGesture = false
            }
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#0b0c0e"))
            addView(
                webView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }
        setContentView(root)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime()
            )
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(root)

        // Dark theme status & navigation bar (after decor view exists)
        window.statusBarColor = Color.parseColor("#0b0c0e")
        window.navigationBarColor = Color.parseColor("#0b0c0e")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
        }

        webView.loadUrl("file:///android_asset/www/index.html")
    }

    private fun createWebView(): WebView {
        return try {
            WebView(this)
        } catch (t: Throwable) {
            if (!isWebViewResourceRedirectFailure(t)) throw t
            WebView(this)
        }
    }

    private fun isWebViewResourceRedirectFailure(t: Throwable): Boolean {
        var current: Throwable? = t
        var depth = 0
        while (current != null && depth < 10) {
            if (current is Resources.NotFoundException &&
                current.message?.contains("failed to redirect ResourcesImpl") == true
            ) {
                return true
            }
            current = current.cause
            depth++
        }
        return false
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
