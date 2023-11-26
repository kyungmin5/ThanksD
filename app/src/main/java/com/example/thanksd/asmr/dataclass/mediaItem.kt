package com.example.thanksd.asmr.dataclass

import android.net.Uri

data class mediaItem (
    val contentUri : Uri,
    val mediaItem: mediaItem,
    val name:String
)

