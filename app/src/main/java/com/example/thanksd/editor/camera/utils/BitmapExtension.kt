package com.example.thanksd.editor.camera.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * The rotationDegrees parameter is the rotation in degrees clockwise from the original orientation.
 */
fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(-rotationDegrees.toFloat())
        postScale(-1f, -1f)
    }


    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
