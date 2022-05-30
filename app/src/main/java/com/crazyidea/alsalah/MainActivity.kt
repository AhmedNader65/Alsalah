package com.crazyidea.alsalah

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.crazyidea.alsalah.data.model.PrimaryColor
import com.crazyidea.alsalah.databinding.ActivityMainBinding
import com.crazyidea.alsalah.receiver.AlarmReceiver
import com.crazyidea.alsalah.utils.CommonUtils.Companion.setLocale
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(this, "ar")
        setTheme(globalPreferences.primaryColor)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id != R.id.navigation_home) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.setStatusBarColor(resources.getColor(R.color.header_color))
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navView.setupWithNavController(navController)
//        val bottomBarBackground = binding.navView.background as MaterialShapeDrawable
//        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
//            .toBuilder()
//            .setTopLeftCorner(RoundedCornerTreatment())
//            .setTopLeftCornerSize(RelativeCornerSize(.8f))
//            .setTopRightCorner(RoundedCornerTreatment())
//            .setTopRightCornerSize(RelativeCornerSize(.8f))
//            .build()
//        setAlarm("asr", System.currentTimeMillis())
    }

    private fun setTheme(primaryColor: PrimaryColor) {
        when (primaryColor) {
            PrimaryColor.ORANGE -> setTheme(R.style.OverlayPrimaryColorOrange)
            PrimaryColor.BLUE -> setTheme(R.style.OverlayPrimaryColorBlue)
            PrimaryColor.PINK -> setTheme(R.style.OverlayPrimaryColorPink)
        }
    }

    private fun setAlarm(title: String, timeInMillis: Long) {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = title
        intent.putExtra("salah", title)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
        }

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        } else {
        }
    }

    fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}