import de.lms.gj10.GameplayScene
import de.lms.gj10.MainMenuScene
import de.lms.gj10.windowHeight
import de.lms.gj10.windowWidth
import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.Size

suspend fun main() = Korge(windowSize = Size(windowWidth, windowHeight), backgroundColor = Colors["#2b2b2b"]) {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MainMenuScene() }
}
