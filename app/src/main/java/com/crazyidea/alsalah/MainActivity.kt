package com.crazyidea.alsalah

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.crazyidea.alsalah.databinding.ActivityMainBinding
import com.crazyidea.alsalah.receiver.AlarmReceiver
import com.crazyidea.alsalah.utils.CommonUtils.Companion.setLocale
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(this, "ar")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navView.setupWithNavController(navController)
        val bottomBarBackground = binding.navView.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setTopLeftCorner(RoundedCornerTreatment())
            .setTopLeftCornerSize(RelativeCornerSize(.8f))
            .setTopRightCorner(RoundedCornerTreatment())
            .setTopRightCornerSize(RelativeCornerSize(.8f))
            .build()
        setAlarm("asr", System.currentTimeMillis())
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
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        }

    }

}