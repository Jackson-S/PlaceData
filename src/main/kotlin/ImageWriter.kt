import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class ImageWriter {
    private val bufferedImage = BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB)
    private val graphics = bufferedImage.createGraphics()

    private val frameBuffer = List(Settings.FRAMEBUFFER_SIZE) {
        BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB)
    }

    private var outputFrameNumber = 0
    private var frameBufferPosition = 0

    init {
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, 2000, 2000)
        val path = Path(Settings.OUTPUT_DIRECTORY)
        if (!path.exists()) {
            path.createDirectories()
        }
    }

    fun drawPixel(pixel: Pixel) {
        graphics.color = pixel.color
        graphics.fillRect(pixel.position.x, pixel.position.y, 1, 1)
    }

    fun outputFrame() {
        if (frameBufferPosition == frameBuffer.size) {
            flushBuffer(frameBuffer)
        }

        bufferedImage.copyData(frameBuffer[frameBufferPosition].raster)
        frameBufferPosition += 1
    }

    private fun flushBuffer(buffer: List<BufferedImage>) {
        print("\nDumping framebuffer...")

        for (frame in buffer) {
            ImageIO.write(
                Settings.ZOOM_REGION?.let { frame.getSubimage(it.x, it.y, it.width, it.height) } ?: frame,
                "png",
                File("${Settings.OUTPUT_DIRECTORY}/$outputFrameNumber.png")
            )

            outputFrameNumber += 1
        }

        frameBufferPosition = 0
        println()
    }

    fun flushBuffer() {
        flushBuffer(frameBuffer)
    }
}
