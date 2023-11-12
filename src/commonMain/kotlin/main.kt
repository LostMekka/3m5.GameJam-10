import de.lms.gj10.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.math.geom.*
import korlibs.time.*

const val windowWidth = 1024
const val windowHeight = 768

suspend fun main() = Korge(windowSize = Size(windowWidth, windowHeight), backgroundColor = Colors["#2b2b2b"]) {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MyScene() }
}

private val buildingCosts = mapOf(
    BuildingType.Factory to 25,
)

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

        addFixedUpdater(1.timesPerSecond) { addIncome() }
    }

    private fun addIncome() {
        val income = gridManager.totalExtractorIncome
        changeMoney(income.toLong())
    }

    private fun onTileClicked(tileInfo: TileInfo) {
        val (tile, building) = tileInfo
        println("tile at (${tile.x}, ${tile.y}) clicked")
        if (building != null) return
        if (!tile.isRevealed) {
            // TODO: dont reveal tiles that easily. need to build something on top first or smth
            gridManager.reveal(tile.x, tile.y)
            return
        }
        if (tile.isBomb) return
        if (tile.number <= 0) return
        val buildingType = currBuildingType
        if (buildingType == null) {
            changeMoney(tile.number.toLong())
        } else {
            changeMoney(-10L)
            gridManager.build(tile.x, tile.y, buildingType)
            currBuildingType = null
            ui.onBuildingTypeChange(null)
        }
    }

    private fun onButtonClicked(type: BuildingType) {
        println("button $type clicked")
        val buildingType = when (type) {
            BuildingType.Base -> BuildingType.Base
            BuildingType.Factory -> BuildingType.Factory
            BuildingType.Extractor -> BuildingType.Extractor
            BuildingType.Excavator -> BuildingType.Excavator
            BuildingType.Turret -> BuildingType.Turret
            BuildingType.Turret2 -> BuildingType.Turret2
            BuildingType.Refinery -> BuildingType.Refinery
        }
        currBuildingType = if (currBuildingType == buildingType) {
            ui.onBuildingTypeChange(null)
            null
        } else {
            val cost = buildingCosts[buildingType] ?: 10.also { println("WARNING: no cost for building $buildingType configured") }
            if (money < cost) {
                ui.onNotEnoughMoney()
                null
            } else {
                ui.onBuildingTypeChange(type)
                buildingType
            }
        }
    }

    private fun changeMoney(diff: Long) {
        money += diff
        ui.onMoneyChanged(money)
    }
}
