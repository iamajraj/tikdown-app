package com.rierx.tikdown

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    lateinit var url: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if("text/plain" == intent.type){
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        url = it;
                        if(url.startsWith("https://")){
                            Toast.makeText(this, "service started...", Toast.LENGTH_LONG).show()
                            Perform().execute()
                        }
                    }
                }
            }
        }
        finish()
    }

    private fun startDownload(url: String) {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            downloadFromUrl(url);
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1234
            )
        }
    }

    private fun downloadFromUrl(url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("TikDown- Downloading")
            request.setDescription("Video is on the way...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "tikdown-"+System.currentTimeMillis().toString() + ".mp4"
            )
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        } catch (exception: Exception) {
            showToast(exception.toString())
        }
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    inner class Perform : AsyncTask<Void, Void, Void>() {
        lateinit var newUrl : String;
        override fun doInBackground(vararg p0: Void?): Void? {
            try {
                var document: Document = Jsoup.connect("https://ssstik.io/abc?url=dl").data("id", url.toString()).data("locale", "en").data("tt", "Z3pkTVc2").post();
                newUrl = document.getElementsByClass("without_watermark").attr("href").toString();
            }catch (e: IOException){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            startDownload(newUrl)
        }
    }
}