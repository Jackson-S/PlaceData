data class Position(
    val x: Int,
    val y: Int
) {
    fun inside(region: Region) =
        (region.x..(region.x2)).contains(x) && (region.y..region.y2).contains(y)
}
