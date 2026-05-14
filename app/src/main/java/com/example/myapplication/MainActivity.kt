package com.example.myapplication

import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.SettingsModel
import com.example.myapplication.domain.repository.SettingsRepository
import com.example.myapplication.domain.simulation.GameLoopManager
import com.example.myapplication.ui.component.GlobalOverlay
import com.example.myapplication.ui.debug.DevLabManager
import com.example.myapplication.ui.debug.PerformanceOverlay
import com.example.myapplication.ui.navigation.PetNavHost
import com.example.myapplication.ui.theme.PetSimulationTheme
import com.example.myapplication.util.PerformanceMonitor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var gameLoopManager: GameLoopManager

    @Inject
    lateinit var devLabManager: DevLabManager

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        performanceMonitor.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.preferredDisplayModeId = 0 // Let system choose best mode
        }

        setContent {
            val settings by settingsRepository.getSettings().collectAsState(initial = SettingsModel())
            
            // DYNAMIC REFRESH RATE CONTROL
            LaunchedEffect(settings.graphics.targetFps) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val window = window
                    val params = window.attributes
                    val target = settings.graphics.targetFps.toFloat()
                    
                    // Find a display mode that matches or exceeds our target
                    val modes = display?.supportedModes ?: emptyArray()
                    val bestMode = modes
                        .filter { it.refreshRate >= target - 1f }
                        .minByOrNull { it.refreshRate }
                    
                    if (bestMode != null) {
                        params.preferredDisplayModeId = bestMode.modeId
                        window.attributes = params
                    }
                }
            }

            PetSimulationTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    PetNavHost()
                    
                    // GLOBAL OVERLAYS
                    GlobalOverlay(
                        performanceMonitor = performanceMonitor,
                        devLabManager = devLabManager,
                        showFps = settings.graphics.showFps
                    )

                    if (devLabManager.showPerformanceOverlay) {
                        PerformanceOverlay(
                            monitor = performanceMonitor,
                            manager = devLabManager,
                            modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gameLoopManager.start()
    }

    override fun onPause() {
        super.onPause()
        gameLoopManager.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        performanceMonitor.stop()
    }
}
