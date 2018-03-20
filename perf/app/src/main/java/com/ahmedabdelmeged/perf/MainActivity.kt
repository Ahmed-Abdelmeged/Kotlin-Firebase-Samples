package com.ahmedabdelmeged.perf

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*
import java.util.concurrent.CountDownLatch

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var mHeader: ImageView
    private lateinit var mContent: TextView

    private val mTrace: Trace by lazy { FirebasePerformance.getInstance().newTrace(STARTUP_TRACE_NAME) }

    private val STARTUP_TRACE_NAME = "startup_trace"
    private val REQUESTS_COUNTER_NAME = "requests sent"
    private val FILE_SIZE_COUNTER_NAME = "file size"
    private val mNumStartupTasks = CountDownLatch(2)
    private val DEFAULT_CONTENT_FILE = "default_content.txt"
    private val CONTENT_FILE = "content.txt"
    private val IMAGE_URL = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mHeader = imageView
        mContent = textView
        button.setOnClickListener {
            // write 40 chars of random text to file
            val contentFile = File(this@MainActivity.filesDir, CONTENT_FILE)
            WriteToFileTask(contentFile.absolutePath).execute(getRandomString(40) + "\n")
        }

        Log.d(TAG, "Starting trace")
        mTrace.start()
        loadImageFromWeb()
        // Increment the counter of number of requests sent in the trace.
        Log.d(TAG, "Incrementing number of requests counter in trace")
        mTrace.incrementCounter(REQUESTS_COUNTER_NAME)
        loadFileFromDisk()
        // Wait for app startup tasks to complete asynchronously and stop the trace.
        Thread(Runnable {
            try {
                mNumStartupTasks.await()
            } catch (e: InterruptedException) {
                Log.e(TAG, "Unable to wait for startup task completion.")
            } finally {
                Log.d(TAG, "Stopping trace")
                mTrace.stop()
                this@MainActivity.runOnUiThread {
                    Toast.makeText(this@MainActivity, "Trace completed",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }).start()
    }

    private fun loadImageFromWeb() {
        GlideApp.with(this).load(IMAGE_URL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                                              isFirstResource: Boolean): Boolean {
                        mNumStartupTasks.countDown() // Signal end of image load task.
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                                 dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        mNumStartupTasks.countDown() // Signal end of image load task.
                        return false
                    }
                }).into(mHeader)
    }

    @SuppressLint("StaticFieldLeak")
    private fun loadFileFromDisk() {
        object : AsyncTask<Void, Void, Boolean>() {
            private var fileContent: String? = null

            override fun doInBackground(vararg params: Void): Boolean? {
                val contentFile = File(this@MainActivity.filesDir, CONTENT_FILE)
                try {
                    if (contentFile.createNewFile()) {
                        // Content file exist did not exist in internal storage and new file was created.
                        // Copy in the default content.
                        val `is`: InputStream = assets.open(DEFAULT_CONTENT_FILE)
                        val size = `is`.available()
                        val buffer = ByteArray(size)
                        `is`.read(buffer)
                        `is`.close()
                        val fos = FileOutputStream(contentFile)
                        fos.write(buffer)
                        fos.close()
                    }
                    val fis = FileInputStream(contentFile)
                    val content = ByteArray(contentFile.length().toInt())
                    fis.read(content)
                    fileContent = String(content)
                    return true

                } catch (e: IOException) {
                    return false
                }

            }

            override fun onPostExecute(result: Boolean?) {
                super.onPostExecute(result)
                if ((!result!!)) {
                    Log.e(TAG, "Couldn't read text file.")
                    Toast.makeText(this@MainActivity, getString(R.string.text_read_error),
                            Toast.LENGTH_LONG).show()
                    return
                }
                mContent.text = fileContent
                // Increment a counter with the file size that was read.
                Log.d(TAG, "Incrementing file size counter in trace")
                mTrace.incrementCounter(FILE_SIZE_COUNTER_NAME, fileContent!!.toByteArray().size.toLong())
                mNumStartupTasks.countDown()
            }
        }.execute()
    }

    private fun getRandomString(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyz".toCharArray()
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until length) {
            val c = chars[random.nextInt(chars.size)]
            sb.append(c)
        }
        return sb.toString()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class WriteToFileTask(private val filename: String) : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String): Void? {
            val content = params[0]
            try {
                FileOutputStream(filename, true).use { fos -> fos.write(content.toByteArray()) }
            } catch (e: IOException) {
                Log.e(TAG, "Unable to write to file: $filename")
                Log.e(TAG, Log.getStackTraceString(e))
            }
            return null
        }

        override fun onPostExecute(aVoid: Void) {
            super.onPostExecute(aVoid)
            loadFileFromDisk()
        }
    }
}
