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
import com.example.ui.CameraScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CameraViewModel

class MainActivity : ComponentActivity() {
  private val cameraViewModel: CameraViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color.Black
        ) {
          CameraScreen(viewModel = cameraViewModel)
        }
      }
    }
  }
}
