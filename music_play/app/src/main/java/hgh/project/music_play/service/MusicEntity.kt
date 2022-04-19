package hgh.project.music_play.service

import com.google.gson.annotations.SerializedName
import hgh.project.music_play.MusicModel

data class MusicEntity(
    @SerializedName("track") val track: String,
    @SerializedName("streamUrl") val streamUrl: String,
    @SerializedName("artist")  val artist: String,
    @SerializedName("cover")  val coverUrl:String,
)


