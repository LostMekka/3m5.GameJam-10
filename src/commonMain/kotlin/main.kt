import de.lms.gj10.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.math.geom.*

suspend fun main() = Korge(windowSize = Size(768, 768), backgroundColor = Colors["#2b2b2b"]) {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MyScene() }
}

class MyScene : Scene() {
    private lateinit var gridManager: GridManager
    private lateinit var ui: GameUi
    private var money = 0L
    private var currBuildingType: Nothing? = null // TODO: replace type

    override suspend fun SContainer.sceneMain() {
        initializeGameResources() // must be the first thing here!

        gridManager = GridManager(this, this@MyScene::onTileClicked)
        gridManager.initializeGrid()

        ui = GameUi(this, this@MyScene::onButtonClicked)
    }

    fun onTileClicked(tileInfo: TileInfo) {

    }

    fun onButtonClicked(type: UiBtnType) {

    }
}
