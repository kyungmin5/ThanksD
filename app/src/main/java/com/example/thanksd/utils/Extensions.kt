package com.example.thanksd.utils

// 문자열이 제이슨 형태인지
fun String?.isJsonObject():Boolean = this?.startsWith("{") == true && this.endsWith("}")

// 문자열이 제이슨 배열인지
fun String?.isJsonArray() : Boolean = this?.startsWith("[") == true && this.endsWith("]")
