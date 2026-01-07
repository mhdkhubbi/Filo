package io.mhdkhubbi.filo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import io.mhdkhubbi.filo.ui.theme.FiloTheme
import io.mhdkhubbi.filo.ui.theme.screens.AppRoot

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = "package:${applicationContext.packageName}".toUri()
            }
            startActivity(intent)
        }

        setContent {
            FiloTheme {

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppRoot(
                            Modifier.padding(innerPadding),

                        )
                    }

            }
        }
    }
}








@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FiloTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppRoot(Modifier.padding(innerPadding))
        }
    }
}