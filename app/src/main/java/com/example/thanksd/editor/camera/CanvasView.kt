package com.example.thanksd.editor.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thanksd.editor.camera.data.TextElementState
import com.example.thanksd.editor.camera.ui.theme.ThanksDTheme
import com.google.android.material.color.utilities.MaterialDynamicColors.background
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import kotlin.math.roundToInt

@Composable
fun CanvasView(
    resetCallback: () -> Unit,
    background: @Composable () -> Unit,
){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(background = {background()})
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(12.dp, 12.dp)
                .background(Color.DarkGray, CircleShape)
                .size(45.dp),
            onClick = {
                resetCallback()
            },
        ){
            Icon(modifier = Modifier.size(34.dp), imageVector = Icons.Rounded.ArrowBack, contentDescription = "Editor Back icon", tint=Color.White)
        }


        Row(
            modifier= Modifier
                .align(Alignment.TopEnd)
                .offset((-12).dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
            IconButton(
                modifier = Modifier
                    .background(Color.DarkGray, CircleShape)
                    .size(45.dp),
                onClick = {
                },
            ){
                Icon(modifier = Modifier.size(32.dp), imageVector = Icons.Rounded.TextFields, contentDescription = "Text Add icon", tint=Color.White)
            }

            IconButton(
                modifier = Modifier
                    .background(Color.DarkGray, CircleShape)
                    .size(45.dp),
                onClick = {
                },
            ){
                Icon(modifier = Modifier.size(32.dp), imageVector = Icons.Rounded.Download, contentDescription = "Download Picture icon", tint=Color.White)
            }
        }


    }

}

@Composable
fun CanvasRowController(){
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            contentPadding = PaddingValues(20.dp, 8.dp, 12.dp, 8.dp),
            onClick = {

            }) {
            Text(modifier = Modifier.height(32.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp, text = "Save")
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "Save icon"
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Canvas(
    background: @Composable () -> Unit,
){
    val screenshotState = rememberScreenshotState()
    val textElements = remember { mutableStateListOf<TextElementState>() }
    val kc = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    ScreenshotBox(screenshotState = screenshotState) {
        Box(modifier = Modifier.fillMaxSize().pointerInput(Unit){
            detectTapGestures(
                onTap = {
                    focusManager.clearFocus()
                    kc?.hide()
                }
            )
        }){
            background()

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
        modifier = Modifier.width(IntrinsicSize.Min)
            .drawBehind {
                drawRoundRect(
                Color.White,
                cornerRadius = CornerRadius(10.dp.toPx())
                )
            }.padding(15.dp, 7.dp)
    )

}



//    var scale by remember { mutableStateOf(1f) }
//    var rotation by remember { mutableStateOf(0f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//    val transformState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
//        scale *= zoomChange
//        rotation += rotationChange
//        offset += offsetChange
//    }
//        Text(
//        color = Color.Black,
//        text = "hello",
//        modifier = Modifier
//            .graphicsLayer(
//                scaleX = scale,
//                scaleY = scale,
//                rotationZ = rotation,
//                translationX = offset.x,
//                translationY = offset.y
//            )
//            // add transformable to listen to multitouch transformation events
//            // after offset
//            .transformable(state = transformState)
//            .background(Color.White)
//        )