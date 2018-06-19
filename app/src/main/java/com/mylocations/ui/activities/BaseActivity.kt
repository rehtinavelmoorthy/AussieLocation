package com.mylocations.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import com.mylocations.utils.LocationUtils
import java.util.logging.Logger

/**
 * Abstract class to display progress dialog and for toolbar home button functionality.
 * All activities extends this class.
 */
abstract class BaseActivity : AppCompatActivity() {
    companion object {
        val LOG = Logger.getLogger(this::class.java.simpleName)
    }

    internal var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        catchAll(LOG, "Set Progress view") {
            super.onCreate(savedInstanceState)
            progressDialog = createProgressDialog()
        }
    }

    protected fun closeProgress() {
        catchAll(LOG, "action1") {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        }
    }

    protected fun showProgress() {
        catchAll(LOG, "action1") {
            if (progressDialog != null) {
                progressDialog!!.show()
            }
        }

    }

    protected fun createProgressDialog(): ProgressDialog? {
        var progressDialog: ProgressDialog? = null
        catchAll(LOG, "Progress Bar not initiated") {
            progressDialog = ProgressDialog(this)
            progressDialog!!.isIndeterminate = false
            progressDialog!!.setMessage("Loading...")
            progressDialog!!.setCancelable(false)
        }

        return progressDialog
    }

    override fun onDestroy() {
        catchAll(LOG, "Activity destroyed") {
            super.onDestroy()
            closeProgress()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun startLocationDetailActivity(id: Int) {
        val intent = Intent(this, LocationDetailActivity::class.java)
        intent.putExtra(LocationUtils.EXTRA_LOCATION_ID, id)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun showToast(message: String, toastDuration: Int = Toast.LENGTH_SHORT) {
        val toast = Toast.makeText(this, message, toastDuration)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.show();
    }

    inline fun catchAll(LOG: Logger, message: String, action: () -> Unit) {
        try {
            action()
        } catch (t: Throwable) {
//            LOG.warn("Failed to $message. ${t.message}", t)
        }
    }
}
