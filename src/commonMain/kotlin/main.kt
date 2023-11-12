import de.lms.gj10.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.math.geom.*
const val windowWidth = 1024
const val windowHeight = 768

suspend fun main() = Korge(windowSize = Size(windowWidth, windowHeight), backgroundColor = Colors["#2b2b2b"]) {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MyScene() }
}

class MyScene : Scene() {
    private lateinit var gridManager: GridManager
    private lateinit var ui: GameUi
    private var money = 0L
    private var currBuildingType: BuildingType? = null

    override suspend fun SContainer.sceneMain() {
        initializeGameResources() // must be the first thing here!

        gridManager = GridManager(this, this@MyScene::onTileClicked)
        gridManager.initializeGrid()

        ui = GameUi(this, this@MyScene::onButtonClicked)
    }

    private fun onTileClicked(tileInfo: TileInfo) {
        val (tile, building) = tileInfo
        println("tile at (${tile.x}, ${tile.y}) clicked")
        if (building != null) return
        if (!tile.isRevealed) return
        if (tile.isBomb) return
        if (tile.number <= 0) return
        val buildingType = currBuildingType
        if (buildingType == null) {
            changeMoney(tile.number.toLong())
        } else {
            changeMoney(-10L)
            gridManager.build(tile.x, tile.y, buildingType)
            currBuildingType = null
        }
    }

    private fun onButtonClicked(type: UiBtnType) {
        println("button $type clicked")
        val buildingType = when (type) {
            UiBtnType.BuildFactory -> BuildingType.Factory
        }
        currBuildingType = if (currBuildingType == buildingType) {
            ui.onBuildingTypeChange(null)
            null
        } else {
            ui.onBuildingTypeChange(type)
            buildingType
        }
    }

    private fun changeMoney(diff: Long) {
        money += diff
        ui.onMoneyChanged(money)
        println("money is now $money")
    }
}
