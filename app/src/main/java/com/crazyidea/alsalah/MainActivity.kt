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
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.crazyidea.alsalah.data.model.PrimaryColor
import com.crazyidea.alsalah.databinding.ActivityMainBinding
import com.crazyidea.alsalah.receiver.AlarmReceiver
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.setLocale
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 35

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val runningQOrLater = Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.Q

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(this, globalPreferences.getLocale())
        setTheme(globalPreferences.getPrimaryColor())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id != R.id.navigation_home) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.header_color)
            }
        }
        if (savedInstanceState == null) {
            navController.navigate(R.id.navigation_home)
        }

        navView.setupWithNavController(navController)
        // Add your own reselected listener
        binding.navView.apply {
            NavigationUI.setupWithNavController(
                this,
                navController
            )
            setOnItemSelectedListener { item ->
                NavigationUI.onNavDestinationSelected(item, navController)
                true
            }
            setOnItemReselectedListener {
                navController.popBackStack(destinationId = it.itemId, inclusive = false)
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//        navController.addOnDestinationChangedListener { item ->
//            // Pop everything up to the reselected item
//            val reselectedDestinationId = item.itemId
//        }
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

    fun showLoading(show: Boolean) {
        binding.progress.isVisible = show
    }

}

