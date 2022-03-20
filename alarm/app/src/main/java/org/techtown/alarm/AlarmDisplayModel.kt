package org.techtown.alarm

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
){
    val timeText:String
        get(){
            val h ="%02d".format(if (hour <12) hour else hour -12)
            val m ="%02d".format(minute)

            return "$h:$m"
        }

    val amapText: String
        get(){
            return if (hour<12) "AM" else "PM"
        }

    val onOffText: String
    get(){
        return  if (onOff) "알람끄기" else "알람켜기"
    }

    fun makeDataForDB():String {
        return "$hour:$minute"      // :로 구분
    }
}
