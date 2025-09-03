package br.edu.ifsp.arq.camerapoc

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import br.edu.ifsp.arq.camerapoc.ui.theme.CameraPoCTheme
import coil.compose.AsyncImage
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CameraPoCTheme {

                Surface(
                    modifier = Modifier.fillMaxSize().padding(45.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraApp()
                }
            }
        }
    }
}


@Composable
fun CameraApp() {
    val context = LocalContext.current
    val outputDirectory = context.filesDir
    var photos by remember { mutableStateOf(listOf<Uri>()) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // carregar fotos já tiradas
    photos = remember {
        outputDirectory.listFiles()?.map {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", it)
        } ?: listOf()
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photos = photos + listOf(currentPhotoUri!!)
        }
    }


    Column (modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                val photoFile = File(outputDirectory, "photo_${System.currentTimeMillis()}.jpg")
                currentPhotoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
                takePictureLauncher.launch(currentPhotoUri!!)
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Tirar Foto")
        }

        // fotos tiradas
        LazyColumn (modifier = Modifier.fillMaxSize()) {
            items(photos) { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(200.dp)
                )
            }
        }
    }




}