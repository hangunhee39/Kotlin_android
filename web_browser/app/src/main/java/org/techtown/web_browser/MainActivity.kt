package org.techtown.web_browser

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val goHomeButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goHomeButton)
    }

    private val goBackButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goBackButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goForwardButton)
    }

    private val addresBar: EditText by lazy {
        findViewById<EditText>(R.id.addressBar)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.refreshlayout)
    }

    private val webView: WebView by lazy {
        findViewById<WebView>(R.id.webView)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById<ContentLoadingProgressBar>(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        bindViews()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()   //앱 종료
        }
    }

    private fun initView() {
        webView.apply {
            webChromeClient =WebChromeClinet()
            webViewClient = WebViewClinet() //크롬 실행 막기
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }

    }

    private fun bindViews() {
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        addresBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl =v.text.toString()
                if (URLUtil.isNetworkUrl(loadingUrl)){  //http 안붙였을때 자동 붙이기
                    webView.loadUrl(loadingUrl)
                }else{
                    webView.loadUrl("http://$loadingUrl")
                }
            }

            return@setOnEditorActionListener false      //키페드 닫기
        }

        goBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    inner class WebViewClinet: WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false  //새로고침 끝내기기
            progressBar.hide()
            goBackButton.isEnabled =webView.canGoBack()
            goForwardButton.isEnabled =webView.canGoForward()
            addresBar.setText(url)
        }
    }

    inner class WebChromeClinet: WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress=newProgress
        }
    }

    companion object{
        private const val DEFAULT_URL ="http://www.google.com"
    }
}