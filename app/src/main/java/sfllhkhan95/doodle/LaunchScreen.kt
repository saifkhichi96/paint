package sfllhkhan95.doodle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import pk.aspirasoft.core.db.PersistentStorage
import sfllhkhan95.doodle.projects.HomeActivity
import java.util.*

/**
 * @author saifkhichi96
 * @version 1.2.0
 * @since 1.0.0
 * created on 23/10/2017 2:26 AM
 */
class LaunchScreen : AppCompatActivity() {

    companion object {
        private const val delay = 1500L
    }

    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as DoodleApplication).setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        var introSeen: Boolean? = PersistentStorage.get("INTRO_SEEN", Boolean::class.java)
        if (introSeen == null) introSeen = false
        val finalIntroSeen = introSeen
        timer.schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(applicationContext, if (!finalIntroSeen || permissionCheck != PackageManager.PERMISSION_GRANTED)
                    IntroActivity::class.java
                else
                    HomeActivity::class.java))
                finish()
            }
        }, delay)
    }

    override fun onStart() {
        super.onStart()
        try {
            val mAuth = FirebaseAuth.getInstance()
            if (mAuth.currentUser == null) {
                mAuth.signInAnonymously()
            }
        } catch (ex: Exception) {
            Crashlytics.logException(ex)
        }

    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        try {
            timer.cancel()
        } finally {
            super.onDestroy()
        }
    }

}