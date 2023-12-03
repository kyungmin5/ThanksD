package com.example.thanksd.editor.camera

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thanksd.retrofit.DiaryPostReqBody
import com.example.thanksd.retrofit.RetrofitManager
import com.example.thanksd.dashboard.ui.theme.DarkBrown
import com.example.thanksd.dashboard.ui.theme.PrimaryYellow
import com.example.thanksd.editor.camera.data.TextElementState
import com.example.thanksd.editor.camera.data.TextElementsViewModel
import com.example.thanksd.utils.Constants.TAG
import com.example.thanksd.utils.RESPONSE_STATE
import com.google.gson.JsonParser
import com.smarttoolfactory.screenshot.ImageResult
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.ScreenshotState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID


@Composable
fun CanvasView(
    screenshotState: ScreenshotState,
    viewModel: TextElementsViewModel = koinViewModel(),
    resetCallback: () -> Unit,
    background: @Composable () -> Unit,
){
    val textElemListState by viewModel.textElemListState.collectAsState()
    val imageResult: ImageResult = screenshotState.imageState.value
    val context = LocalContext.current

    var saveToGalleryBtnClicked by remember { mutableStateOf(false) }

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()
    val activity = (LocalContext.current as? Activity)


    LaunchedEffect(imageResult){
        if(saveToGalleryBtnClicked){
            when (imageResult) {
                is ImageResult.Success -> {
                    screenshotState.imageBitmap?.let{
                        val onSaveToGallery =  viewModel::storePhotoInGallery
                        onSaveToGallery(screenshotState.imageBitmap!!.asAndroidBitmap())
                    }
                    val toast =  Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT)
                    toast.show()

                    // Image(bitmap = imageResult.data.asImageBitmap(), contentDescription = null)
                }
                is ImageResult.Error -> {
                    val toast = Toast.makeText(context,"Error: ${imageResult.exception.message}", Toast.LENGTH_SHORT)
                    toast.show()
                }
                else -> {}
            }
            saveToGalleryBtnClicked = false
        }else{
            when (imageResult) {
                is ImageResult.Success -> {
                    screenshotState.imageBitmap?.let {imageBitmap->
                        diarySaveHandler(context, coroutineScope, activity, imageBitmap)
                    }
                }
                is ImageResult.Error -> {
                    val toast = Toast.makeText(context,"Error: ${imageResult.exception.message}", Toast.LENGTH_SHORT)
                    toast.show()
                }
                else -> {}
            }
        }
    }
    Column{
        Box(
            modifier = Modifier
                .padding(vertical = 15.dp)
                .fillMaxWidth()
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.DarkGray, CircleShape)
                    .size(45.dp),
                onClick = {
                    resetCallback()
                    viewModel.clearTextElementState()
                },
            ){
                Icon(modifier = Modifier.size(34.dp), imageVector = Icons.Rounded.ArrowBack, contentDescription = "Editor Back icon", tint=Color.White)
            }

            Row(
                modifier= Modifier
                    .align(Alignment.TopEnd)
            ){
                IconButton(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .background(Color.DarkGray, CircleShape)
                        .size(45.dp),
                    onClick = {
                        val newTextElementState = TextElementState(text="New Text", position=Offset(0f, 0f))
                        viewModel.addTextElementState(newTextElementState)
                    },
                ){
                    Icon(modifier = Modifier.size(32.dp), imageVector = Icons.Rounded.TextFields, contentDescription = "Text Add icon", tint=Color.White)
                }

                IconButton(
                    modifier = Modifier
                        .background(Color.DarkGray, CircleShape)
                        .size(45.dp),
                    onClick = {
                        screenshotState.capture()
                    },
                ){
                    Icon(modifier = Modifier.size(32.dp), imageVector = Icons.Rounded.Download, contentDescription = "Download Picture icon", tint=Color.White)
                }
            }


        }
        ScreenshotBox(screenshotState = screenshotState) {
            Canvas(textElemListState = textElemListState, background = { background() })

        }
    }




}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Canvas(
    textElemListState: List<TextElementState>,
    background: @Composable () -> Unit,
){
    val kc = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(25.dp))
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    focusManager.clearFocus()
                    kc?.hide()
                }
            )
        }){
        background()
        textElemListState.forEach { textElementState ->
            TextElement(
                initialText = textElementState.text,
                positionOffset = textElementState.position
            )
        }
    }

}


@Composable
fun CanvasRowController(screenshotState: ScreenshotState, imageBitmap: ImageBitmap?){

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            modifier= Modifier
                .width(110.dp)
                .height(40.dp)
                .align(Alignment.CenterVertically),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellow),
            onClick = {
                    screenshotState.capture()
            }) {
            Text(modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(align = Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                color = DarkBrown,
                fontSize = 18.sp, text = "Save")
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Rounded.ChevronRight,
                tint = DarkBrown,
                contentDescription = "Save icon"
            )
        }
    }
}


@Composable
fun TextElement(
    initialText: String = "",
    positionOffset: Offset,
) {

    // TODO 글자 박스 크기 제한 두기
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(positionOffset) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange * scale
    }




    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y,
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(Color.Transparent)
            .padding(30.dp)
    ) {
        TextFieldElement(initialText)
    }
}

@Composable
fun TextFieldElement(
    initialText: String = "",
) {

    var text by remember { mutableStateOf(initialText) }

    BasicTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .drawBehind {
                drawRoundRect(
                    Color.White,
                    cornerRadius = CornerRadius(10.dp.toPx())
                )
            }
            .padding(15.dp, 7.dp)
    )

}



fun diarySaveHandler(context: Context, coroutineScope: CoroutineScope, activity: Activity?, imageBitmap: ImageBitmap){

    val imageId = UUID.randomUUID().toString();
    val file = File(context.cacheDir, "$imageId.png")

    try {
        val outputStream = FileOutputStream(file)
        imageBitmap.asAndroidBitmap()
            .compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        val imagePart =
            RequestBody.create("image/png".toMediaTypeOrNull(),file)

        coroutineScope.launch {
            var retrofitManager = RetrofitManager.instance
            val preSignedUrlResult = retrofitManager.getPresignedUrl(image = "$imageId.png")
            val preSignedUrl = JsonParser.parseString(preSignedUrlResult).asJsonObject.getAsJsonObject("data").get("url").asString
            if(preSignedUrl.startsWith("https://thanksd-image-bucket.s3.ap-northeast-2.amazonaws.com/")){
                Log.d("presignedUrl" , preSignedUrl)
                val result = retrofitManager.uploadImagetoS3(preSignedUrl=preSignedUrl, file=imagePart)
                if(result === RESPONSE_STATE.OKAY){
                    retrofitManager.postDiary(
                        DiaryPostReqBody(image=preSignedUrl),
                        completion = { responseState->
                            when (responseState) {
                                RESPONSE_STATE.OKAY -> {
                                    Log.d(TAG, "저장 성공")
                                    activity?.finish()
                                }
                                RESPONSE_STATE.FAIL -> {
                                    Log.d(TAG, "저장 실패")
                                }
                            }})
                }

            }else{
                throw error("presignedUrl 실패")
            }

        }

    } catch (e: IOException) {
        e.printStackTrace()
    }

}



