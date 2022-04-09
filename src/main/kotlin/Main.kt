import java.time.Duration
import java.time.Instant

object Settings {
    // The 2022_place_canvas_history.csv file must be sorted alphabetically line by line before use in this program
    const val PLACE_DATA_PATH = "/Users/jackson/Desktop/2022_place_canvas_history_sorted.csv"

    const val OUTPUT_DIRECTORY = "./frames"

    const val SECONDS_PER_FRAME = 60

    // Frames to keep in memory before flushing to disk
    const val FRAMEBUFFER_SIZE = 100

    val ZOOM_REGION: Region = Place2022Regions.FuckCars
}

fun main() {
    val reader = PlaceDataReader()
    val writer = ImageWriter()

    var frameNumber = 0
    var frameStartTime = Instant.MIN

    // Output the initial canvas as a blank white page
    writer.outputFrame()

    while (true) {
        val pixel = try {
            reader.nextPixel()
        } catch (e: EndOfFileException) {
            break
        }

        writer.drawPixel(pixel)

        if (frameStartTime.deltaSeconds(pixel.placementTime) >= Settings.SECONDS_PER_FRAME) {
            print("\rCreated frame $frameNumber to time ${pixel.placementTime}")

            frameStartTime = pixel.placementTime
            frameNumber += 1

            writer.outputFrame()
        }
    }

    writer.flushBuffer()

    println("done")
}

fun Instant.deltaSeconds(other: Instant) = Duration.between(this, other).seconds
