package jsonClass

data class SkjList(
    val skjList: List<SkjItem>
) {
    fun size(): Int {
        return if(skjList != null) skjList.size else 0
    }

    operator fun get(j: Int): SkjItem {
        return skjList[j]
    }
}

data class SkjItem (
    val user_id: String,
    val year: String,
    val month: String,
    val day: String,
    val from_time: String,
    val skj_info: String
)