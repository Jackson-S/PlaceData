import java.awt.Color
import java.time.Instant

data class Pixel(
    val placementTime: Instant,
    val userId: String,
    val color: Color,
    val position: Position
)
