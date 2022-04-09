data class Region(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {
    val x2 = x + width
    val y2 = y + width
}
