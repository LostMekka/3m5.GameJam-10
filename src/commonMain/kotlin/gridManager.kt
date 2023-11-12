import de.lms.gj10.*
import de.lms.gj10.minesweeper.*
import korlibs.korge.input.*
import korlibs.korge.view.*


enum class BuildingType {
    Base,
    Factory,
    Extractor,
    Excavator,
    Turret,
    Turret2,
    Refinery,
}

data class BuildingData(
    var image : Image,
    var type: BuildingType,
)

enum class UnitType {
    Soldier,
    Grenadier,
    Tank,
    RocketTank,
    Bomber,
}

data class GridElement(
    var image : Image,
    var x : Int,
    var y : Int,
    var building : BuildingData?,
    val id : Int,
    var imageNum : Int = -1, // 0 = hidden, 1 = bomb, 2 = empty uncovered, 3-10 = numbers(1-8), 10+ = buildings
    )

data class TileInfo(
    val tile : Tile,
    val buildingType : BuildingType? = null,
)

val Tile.imageNum : Int get() {
    if (!isRevealed) return 0
    if (isBomb) return 1
    return number + 2
}

class GridManager(
    private val container : SContainer,
    private val onTileClick : (TileInfo) -> Unit,
) {
    private val gridElements = mutableListOf<GridElement>()
    private val mineSweeper = generateSolvableMinesweeperGrid(32, 32, 100)
    val totalFactoryIncome : Int get(){
        var income = 0
        val factories = gridElements.filter { it.building?.type == BuildingType.Factory }
        for (i in 0 until factories.size){
            val factoryTile = mineSweeper[factories[i].x, factories[i].y]
            income += factoryTile.number
        }
        return income
    }
    fun initializeGrid() = container.apply {
        for (x in 0 until mineSweeper.width) {
            for (y in 0 until mineSweeper.height) {
                val tile = mineSweeper[x,y]
                val img = tileImg(x,y,tile.imageNum)
                gridElements += GridElement(img,x, y, null, tile.id ,tile.imageNum)
            }
        }
        gridElements.sortBy { it.id }
    }

    fun reveal(x : Int, y : Int) {
        val oldTile = mineSweeper[x,y]
        val gridElement = gridElements[oldTile.id]
        val newTile = mineSweeper.reveal(oldTile)
        gridElement.image.removeFromParent()
        gridElement.imageNum = newTile.imageNum
        gridElement.image = tileImg(x,y,gridElement.imageNum)
    }

    fun build(x: Int, y : Int, buildingType : BuildingType){
        val bitmap = gameResources.tiles.buildings[buildingType] ?: return
        val building = BuildingData(container.image(bitmap), buildingType)
        val tile = mineSweeper[x, y]
        gridElements[tile.id].building = building
        building.image.position(x * tileScale * tileSize, y * tileScale * tileSize)
        building.image.scale = tileScale
    }

    private fun tileImg(x : Int,y : Int, imageNum: Int) : Image {
        val img = when (imageNum) {
            0 -> container.image(gameResources.tiles.hidden)
            1 -> container.image(gameResources.tiles.bomb)
            2 -> container.image(gameResources.tiles.empty)
            in 3..10 -> container.image(gameResources.tiles.numbers[imageNum - 2])
            else -> container.image(gameResources.tiles.unknown)
        }
        img.position(x * tileScale * tileSize, y * tileScale * tileSize)
        img.scale = tileScale
        img.onClick { clickPreProcessing(x, y) }
        return img
    }
    private fun clickPreProcessing(x : Int, y : Int){
        val tile = mineSweeper[x, y]
        val gridElement =  gridElements[tile.id]
        onTileClick(TileInfo(tile, gridElement.building?.type))
    }
}



const val tileSize = 16
const val tileScale : Double = 1.5
