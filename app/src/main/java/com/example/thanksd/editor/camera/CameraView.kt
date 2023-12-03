
package com.example.thanksd.editor.camera

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlipCameraIos
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.thanksd.editor.camera.utils.rotateBitmap
import java.nio.ByteBuffer
import java.util.concurrent.Executor

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    // 카메라 보여 주는 뷰
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = (LocalContext.current as? Activity)
    Column{
        Box(
            modifier= Modifier.padding(horizontal = 10.dp, vertical = 15.dp)
                .fillMaxWidth()
        ){
            IconButton(
                modifier = Modifier
                    .size(45.dp),
                onClick = {
                    activity?.finish()
                },
            ){
                Icon(modifier = Modifier.size(34.dp), imageVector = Icons.Rounded.Close, contentDescription = "Text Add icon", tint=Color.White)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(25.dp))){
            AndroidView(
                modifier = modifier,
                factory = {
                    PreviewView(it).apply {
                        this.controller = controller
                        controller.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
        }
    }

}


@Composable
fun CameraRowController(
    viewModel: CameraViewModel,
    controller: LifecycleCameraController,
    singlePhotoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    val context: Context = LocalContext.current
    val onPhotoCaptured = viewModel::updateCapturedPhotoState
    Row(
        modifier = Modifier
            .fillMaxSize().padding(horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .size(38.dp) // Set the size of the IconButton
                .background(Color.White, CircleShape),
            onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            ){
            Icon( modifier = Modifier.size(33.dp), imageVector = Icons.Rounded.Image, contentDescription = "Gallery icon", tint=Color.DarkGray)
        }

        IconButton(
            modifier = Modifier
                .size(57.dp) // Set the size of the IconButton
                .background(Color.White, CircleShape),
            onClick = {
                capturePhoto(context, controller, onPhotoCaptured)
            },
            ){
            Icon( modifier = Modifier.size(50.dp), imageVector = Icons.Rounded.Camera, contentDescription = "Camera capture icon", tint=Color.DarkGray)
        }

        IconButton(
            modifier = Modifier
                .size(38.dp) // Set the size of the IconButton
                .background(Color.White, CircleShape),
            onClick = {
                controller.cameraSelector =
                    if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else CameraSelector.DEFAULT_BACK_CAMERA
            },
            ){
            Icon( modifier = Modifier.size(33.dp), imageVector = Icons.Rounded.FlipCameraIos, contentDescription = "Camera Converter icon", tint=Color.DarkGray)
        }
    }
}


private fun capturePhoto(
    context: Context,
    cameraController: LifecycleCameraController,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)


    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)

            // imageProxy에서 비트맵 변환 함수 새로 만들어서 작업
            val bitmap = imageProxyToBitmap(image).rotateBitmap(image.imageInfo.rotationDegrees)
            onPhotoCaptured(bitmap)
            image.close()
        }

        override fun onError(exception: ImageCaptureException) {
            super.onError(exception)
            Log.e("CameraContent", "Error capturing image", exception)
        }
    })
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer: ByteBuffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}