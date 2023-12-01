package com.example.thanksd.editor.camera.data

import androidx.compose.ui.geometry.Offset
import java.util.UUID

data class TextElementState(
    val id: UUID = UUID.randomUUID(),
    var text: String,
    var position: Offset,
)
