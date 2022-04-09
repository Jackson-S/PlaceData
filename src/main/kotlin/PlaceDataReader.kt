import java.awt.Color
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

class PlaceDataReader {
    private data class Line(
        var rawTime: String,
        var userId: String,
        var rawColor: String,
        var rawX: String,
        var rawY: String
    ) {
        constructor(line: List<String>) : this(line[0], line[1], line[2], line[3], line[4])
    }

    private val file = File(Settings.PLACE_DATA_PATH)
    private val bufferedReader = file.bufferedReader()
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
    private val replacementRegex = "\\.[0-9]{1,3}\\s".toRegex()

    init {
        // Skip first line since it's a header
        bufferedReader.readLine()
    }

    private fun getPosition(rawX: String, rawY: String) =
        Position(
            x = rawX.substringAfter("\"").toInt(),
            y = rawY.substringBefore("\"").toInt()
        )

    private fun getColor(rawColor: String) =
        Color(
            rawColor.substring(1, 3).toInt(16),
            rawColor.substring(3, 5).toInt(16),
            rawColor.substring(5, 7).toInt(16)
        )

    private fun getLine() = try {
        bufferedReader.readLine()
    } catch (e: IOException) {
        throw EndOfFileException()
    }.split(",")

    private fun getNextPixel(region: Region): Pixel {
        var line: Line
        do {
            line = convertLine(getLine())
        } while (!getPosition(line.rawX, line.rawY).inside(region))

        return Pixel(
            placementTime = dateTimeFormat.parse(line.rawTime.replace(replacementRegex, " ")).toInstant(),
            userId = line.userId,
            color = getColor(line.rawColor),
            position = getPosition(line.rawX, line.rawY)
        )
    }

    fun nextPixel() = getNextPixel(Settings.ZOOM_REGION)

    private fun convertLine(line: List<String>) = when (line.size) {
        // For some reason some lines have extra x and y coordinates
        5, 7 -> Line(line)
        else -> throw EndOfFileException()
    }
}
