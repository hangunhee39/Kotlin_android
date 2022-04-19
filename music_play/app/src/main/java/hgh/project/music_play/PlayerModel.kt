package hgh.project.music_play

data class PlayerModel(
    private val playMusicList: List<MusicModel> = emptyList(),
    var currentPosition: Int = -1,
    var isWatchingPlayListView: Boolean = true
) {
    //지금 생행되는 MusicModel 의 isPlaying true 로 바꾸는 함수
    fun getAdapterModels(): List<MusicModel> {
        return playMusicList.mapIndexed { index, musicModel ->
            val newItem = musicModel.copy(
                isPlaying = index == currentPosition
            )
            newItem
        }
    }

    //재생되고있는 Music 으로 currentPosition 바꾸기
    fun updateCurrentPosition(musicModel: MusicModel) {
        currentPosition = playMusicList.indexOf(musicModel)
    }

    //다음 음악으로 바꾸기
    fun nextMusic(): MusicModel? {
        if (playMusicList.isEmpty()) return null

        currentPosition = if ((currentPosition + 1) == playMusicList.size) {
            0
        } else {
            currentPosition + 1
        }
        return playMusicList[currentPosition]
    }

    //이전 음악으로 바꾸기
    fun prevMusic(): MusicModel? {
        if (playMusicList.isEmpty()) return null

        currentPosition = if ((currentPosition - 1) < 0) {
            playMusicList.lastIndex
        } else {
            currentPosition - 1
        }
        return playMusicList[currentPosition]
    }

    //재생되는 musicModel 받기
    fun currentMusicModel(): MusicModel? {
        if (playMusicList.isEmpty()) return null

        return playMusicList[currentPosition]
    }
}
