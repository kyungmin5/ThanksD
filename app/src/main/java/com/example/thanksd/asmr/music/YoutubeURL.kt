package com.example.thanksd.asmr.music

import android.content.Context
import android.util.Log
import android.util.SparseArray
import com.example.thanksd.asmr.dataclass.mediaItem
import com.example.thanksd.asmr.dataclass.mediaViewModel
import com.maxrave.kotlinyoutubeextractor.State
import com.maxrave.kotlinyoutubeextractor.VideoMeta
import com.maxrave.kotlinyoutubeextractor.YTExtractor
import com.maxrave.kotlinyoutubeextractor.YtFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class YoutubeURL(context: Context) {
    var context = context
    private val scope = CoroutineScope(Dispatchers.Main)
    fun generateURL(videdID:String,viewModel:mediaViewModel){
        var videoId = videdID
        val yt = YTExtractor(con = context, CACHING = false, LOGGING = true, retryCount = 3)
        var streamurl:String? = ""
        var ytFiles: SparseArray<YtFile>? = null
        var videoMeta: VideoMeta? = null
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                yt.extract(videoId)
                //Before get YtFile or VideoMeta, you need to check state of yt object
                if (yt.state == State.SUCCESS) {
                    ytFiles = yt.getYTFiles()
                    videoMeta = yt.getVideoMeta()
                    var ytFile = ytFiles?.get(251)
                    streamurl = ytFile?.url

                }
            }
            result?.let {
                if (streamurl != "") {
                    //Log.d("YoutubeURL", streamurl!!)
                    val img = videoMeta?.thumbUrl
                    val title = videoMeta?.title
                    synchronized(viewModel){
                        viewModel.url.value = mediaItem(streamurl!!,title!!,img!!)
                    }
                }
            }

        }
    }

}