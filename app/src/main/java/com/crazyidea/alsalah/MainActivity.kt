package com.crazyidea.alsalah

import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.crazyidea.alsalah.data.model.PrimaryColor
import com.crazyidea.alsalah.databinding.ActivityMainBinding
import com.crazyidea.alsalah.utils.getPrimaryColor
import com.crazyidea.alsalah.utils.setLocale
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.setLocale(App.instance.getAppLocale())))
    }

    private lateinit var binding: ActivityMainBinding

    private val runningQOrLater = Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.Q


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getPrimaryColor(DataStoreCollector.accentColor))
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

    fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    fun showLoading(show: Boolean) {
        binding.progress.isVisible = show
    }

    fun hideKeyboard() {
        val keyboard: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}

