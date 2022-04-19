package hgh.project.music_play

import hgh.project.music_play.service.MusicDto
import hgh.project.music_play.service.MusicEntity

fun MusicEntity.mapper(id: Long): MusicModel {
    return MusicModel(
        id = id,
        streamUrl = this.streamUrl,
        coverUrl = this.coverUrl,
        track = this.track,
        artist = this.artist
    )
}

fun MusicDto.mapper(): PlayerModel {
    return PlayerModel(
        playMusicList = this.musics.mapIndexed { index, musicEntity ->
            musicEntity.mapper(index.toLong())
        }
    )
}