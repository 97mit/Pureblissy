package com.pureblissy.android

import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var content: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        installSplashScreen().apply {
            setKeepOnScreenCondition{
                mainViewModel.isLoading.value
            }
        }
        /*content = findViewById(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("MainActivity", "onCreate: I AM RUNNING ON API 12 or higher")
            content.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean =
                    when {
                        mainViewModel.mockDataLoading() -> {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        }
                        else -> false
                    }
            })

            // custom exit on splashScreen
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                // custom animation.
                ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_X,
                    0f,
                    splashScreenView.width.toFloat()
                ).apply {
                    duration = 1000
                    // Call SplashScreenView.remove at the end of your custom animation.
                    doOnEnd {
                        splashScreenView.remove()
                    }
                }.also {
                    // Run your animation.
                    it.start()
                }
            }
        }
*/
    }

}