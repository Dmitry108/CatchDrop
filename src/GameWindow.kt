import java.awt.Graphics
import java.awt.Image
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import kotlin.random.Random
import kotlin.system.exitProcess

fun main(){
    GameWindow.start()
}

class GameWindow : JFrame() {

    companion object {
        private val gameWindow = GameWindow()

        private var lastFrameTime: Long = System.nanoTime()
        private val backgroundImage: Image
        private val gameOverImage: Image
        private val dropImage: Image
        private var dropLeft: Float = 200f
        private var dropTop: Float = -100f
        private var dropSpeed: Float = 10f
        private var score: Int = 0

        init {
            try {
                backgroundImage = ImageIO.read(GameWindow::class.java.getResourceAsStream("background.png"))
                gameOverImage = ImageIO.read(GameWindow::class.java.getResourceAsStream("game_over.png"))
                dropImage = ImageIO.read(GameWindow::class.java.getResourceAsStream("drop.png"))
            } catch (e: IOException) {
                e.printStackTrace()
                exitProcess(1)
            }
        }

        fun start() {
            gameWindow.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            gameWindow.setLocation(200, 100)
            gameWindow.setSize(906, 478)
            gameWindow.isResizable = false
            val gamePanel = GamePanel()
            gamePanel.addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    val x = e.x
                    val y = e.y
                    val dropRight = dropLeft + dropImage.getWidth(null)
                    val dropBottom = dropTop + dropImage.getHeight(null);
                    val isDrop = !(x < dropLeft || x > dropRight || y < dropTop || y > dropBottom)
                    if (isDrop) {
                        dropTop = -100f
                        dropLeft = (Math.random() * (gamePanel.width - dropImage.getWidth(null))).toFloat()
                        dropSpeed += 5
                        score++
                        gameWindow.title = "Score: $score"
                    }
                }
            })
            gameWindow.add(gamePanel)
            gameWindow.isVisible = true
        }

        private fun onRepaint(g: Graphics?) {
            val currentTime = System.nanoTime()
            val deltaTime = (currentTime - lastFrameTime) * 0.000000001f
            lastFrameTime = currentTime
            dropTop += dropSpeed * deltaTime
            g?.drawImage(backgroundImage, 0, 0, null);
            g?.drawImage(dropImage, dropLeft.toInt(), dropTop.toInt(), null)
            if (dropTop > gameWindow.height) g?.drawImage(gameOverImage, 280, 120, null)
        }

        private class GamePanel : JPanel() {
            override fun paintComponent(g: Graphics?) {
                super.paintComponent(g)
                onRepaint(g)
                repaint()
            }
        }
    }
}