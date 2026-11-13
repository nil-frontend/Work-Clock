package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ui.theme.MyApplicationTheme
import com.example.workclock.controller.WindowController
import com.example.workclock.ui.WorkClockApp
import com.example.workclock.viewmodel.ClockViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var windowController: WindowController
    private val clockViewModel: ClockViewModel by viewModels {
        ClockViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        windowController = WindowController(this)

        // Dynamically apply keep-screen-awake, window brightness, and immersive mode
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                clockViewModel.settings.collect { settings ->
                    windowController.applySettings(settings)
                }
            }
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    WorkClockApp(viewModel = clockViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure screen-awake and brightness are re-applied immediately upon foregrounding
        windowController.applySettings(clockViewModel.settings.value)
    }
}
