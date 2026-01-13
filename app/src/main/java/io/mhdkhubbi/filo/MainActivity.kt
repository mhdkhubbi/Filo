package io.mhdkhubbi.filo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.mhdkhubbi.filo.ui.theme.FiloTheme
import io.mhdkhubbi.filo.ui.theme.screens.MainScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: android.content.Context) {
        val configuration = android.content.res.Configuration(newBase.resources.configuration)
        // Force LTR direction
        configuration.setLayoutDirection(java.util.Locale.US)
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FiloTheme {


                Scaffold(modifier = Modifier.fillMaxSize()

                ) { innerPadding ->

                        MainScreen(Modifier.padding(innerPadding))

                }
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        if (!android.os.Environment.isExternalStorageManager()) {
                            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                data = "package:$packageName".toUri()
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    FiloTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainScreen(Modifier.padding(innerPadding))
        }
    }
}