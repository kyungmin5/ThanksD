package com.example.thanksd.editor.camera.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TextElementsViewModel(
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase
) : ViewModel() {

    // MutableStateFlow를 사용하여 UI에 상태를 알리는 private 변수
    private val _textElemListState = MutableStateFlow<List<TextElementState>>(emptyList())

    // Public으로 노출되는 StateFlow (불변)
    val textElemListState = _textElemListState.asStateFlow()

    // 리스트에 하나의 TextElementState를 추가하여 업데이트하는 함수
    fun addTextElementState(textElementState: TextElementState) {
        // 현재 리스트를 가져온 후 새로운 요소를 추가
        val currentList = _textElemListState.value.toMutableList()
        currentList.add(textElementState)

        // 새로운 리스트를 UI에 전달하여 변경 사항 알림
        _textElemListState.value = currentList
    }

    // 리스트에서 TextElementState를 삭제하여 업데이트하는 함수
    fun removeTextElementState(textElementState: TextElementState) {
        val currentList = _textElemListState.value.toMutableList()
        currentList.remove(textElementState)
        _textElemListState.value = currentList
    }

    // 텍스트 리스트 전체 삭제
    fun clearTextElementState() {
        _textElemListState.value = emptyList()
    }

    // 글자를 포함해 이미지화해서 갤러리에 저장
    fun storePhotoInGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            savePhotoToGalleryUseCase.call(bitmap)
        }
    }

    // 서버에 이미지 저장
    fun storeDiaryOnServerSide(imageBitmap: ImageBitmap){


    }
}