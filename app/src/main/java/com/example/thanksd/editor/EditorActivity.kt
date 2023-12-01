package com.example.thanksd.editor

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.thanksd.editor.camera.CameraPreview
import com.example.thanksd.editor.camera.CameraRowController
import com.example.thanksd.editor.camera.CameraViewModel
import com.example.thanksd.editor.camera.CanvasRowController
import com.example.thanksd.editor.camera.CanvasView
import com.example.thanksd.editor.camera.NoPermissionScreen
import com.example.thanksd.editor.camera.utils.rotateBitmap
import com.example.thanksd.ui.theme.ThanksDTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.smarttoolfactory.screenshot.rememberScreenshotState
import org.koin.androidx.compose.koinViewModel


class EditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ThanksDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainView() {

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    MainContent(
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )

}
@Composable
private fun MainContent(hasPermission:Boolean, onRequestPermission: () -> Unit){

    if(hasPermission){
        EditorScreen()
    }
    else{
        NoPermissionScreen(onRequestPermission)
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorScreen(
    viewModel: CameraViewModel = koinViewModel()
) {
    val cameraState by viewModel.state.collectAsStateWithLifecycle()
    val lastCapturedPhoto = cameraState.capturedImage
    val context: Context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context).apply {
        setEnabledUseCases(
            CameraController.IMAGE_CAPTURE
        )
    } }

    // 이미지 picker
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    fun resetSelectedImages() {
        if(selectedImageUri !== null){
            selectedImageUri = null
        }
        if(lastCapturedPhoto !== null){
            val updateCameraState = viewModel::updateCapturedPhotoState
            updateCameraState(null)
        }

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.Bottom
    ){
        val screenshotState = rememberScreenshotState()
        Box(modifier = Modifier.fillMaxWidth()
            .background(color = Color.Black)
            .clip(shape = RoundedCornerShape(25.dp))
            .weight(5.0f)
        ) {
            if(selectedImageUri !== null){
                CanvasView(screenshotState=screenshotState, resetCallback = { resetSelectedImages() }, background = {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                })
            }else if(lastCapturedPhoto !== null){
                CanvasView(screenshotState=screenshotState, resetCallback = { resetSelectedImages() },
                    background = {
                        LastPhotoPreview(modifier = Modifier.fillMaxWidth(), lastCapturedPhoto=lastCapturedPhoto)
                    })
            }
            else{
                CameraPreview(modifier = Modifier.fillMaxSize(), controller = cameraController)
            }
        }

        Box(modifier = Modifier.fillMaxWidth()
            .background(color = Color.Black)
            .clip(shape = RoundedCornerShape(25.dp))
            .weight(1.0f)){
            if(selectedImageUri !== null || lastCapturedPhoto !== null){
                // editor
                CanvasRowController(imageBitmap=screenshotState?.imageBitmap)
            }else{
                // camera
                CameraRowController(viewModel = viewModel, controller = cameraController, singlePhotoPickerLauncher = singlePhotoPickerLauncher)
            }
        }
    }
}



@Composable
private fun LastPhotoPreview(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap
) {

    val capturedPhoto: ImageBitmap =
        remember(lastCapturedPhoto.hashCode()) { lastCapturedPhoto.asImageBitmap() }

    Surface(modifier = modifier.fillMaxSize()) {
        Image(
            bitmap = capturedPhoto,
            contentDescription = "Last captured photo",
            contentScale = ContentScale.Crop
        )
    }

}








