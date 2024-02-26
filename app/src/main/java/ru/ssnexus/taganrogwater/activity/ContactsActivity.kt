/*
 * Copyright (c) Vasyutchenko Alexey  2023. Last modified 19.11.2023, 10:02
 * ss.plexus@gmail.com
 */

package ru.ssnexus.taganrogwater.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityContactsBinding
import ru.ssnexus.taganrogwater.utils.Utils

class ContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactsBinding

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressDialog: ProgressDialog

    private val contactsUrl = App.instance.interactor.getSiteContactsUrlPref()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.contacts)

        webView = binding.webView
        progressBar = binding.progressBar
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(resources.getString(R.string.loading_please_wait))
        val webSettings: WebSettings = webView.settings
        webSettings.builtInZoomControls = true
        webSettings.javaScriptEnabled = true
        webView.webViewClient = MyWebViewClient()

        if(Utils.checkConnection(this)) loadPage() else
            Toast.makeText(this, resources.getString(R.string.no_connection), Toast.LENGTH_SHORT).show()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {

                progressBar.setProgress(newProgress)
                progressDialog.show()
                if (newProgress == 100){
                    progressDialog.dismiss()
                }
                super.onProgressChanged(view, newProgress)
            }
        }
        webView.setDownloadListener { s, s2, s3, s4, l ->
            if(s != null){
                val intent = Intent (Intent.ACTION_VIEW)
                intent.setData(Uri.parse(s))
                startActivity(intent)
            }
        }
    }

    private fun loadPage(){
        webView.loadUrl(contactsUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            webView.loadUrl("javascript:(function() { " +
                    "document.getElementsByTagName('div')[0].style.display='none'; " +
                    "})()")
            progressBar.visibility = View.GONE
            webView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
//        Timber.d("Activity Destroyed")
        super.onDestroy()
    }
}