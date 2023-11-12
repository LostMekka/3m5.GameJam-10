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

enum class UnitType {
    Soldier,
    Grenadier,
    Tank,
    RocketTank,
    Bomber,
}

data class GridElement(
    var image : Image,
    var buildImg : Image?,
    val id : Int,
    var imageNum : Int = -1, // 0 = hidden, 1 = bomb, 2 = empty uncovered, 3-10 = numbers(1-8), 10+ = buildings
    )

data class TileInfo(
    val tile : Tile,
    val currentBuilding : UiBtnType? = null,
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
    fun initializeGrid() = container.apply {
        for (x in 0 until mineSweeper.width) {
            for (y in 0 until mineSweeper.height) {
                val tile = mineSweeper[x,y]
                val img = tileImg(x,y,tile.imageNum)
                gridElements += GridElement(img,null, tile.id ,tile.imageNum)
            }
        }
        gridElements.sortBy { it.id }
    }

    fun reveal(x : Int, y : Int) {
        val tile = mineSweeper[x,y]
        val gridElement = gridElements[tile.id]
        mineSweeper.reveal(tile)
        gridElement.image.removeFromParent()
        gridElement.imageNum = tile.imageNum
        gridElement.image = tileImg(x,y,gridElement.imageNum)
    }

    fun build(x: Int, y : Int, building : BuildingType){
        val bitmap = gameResources.tiles.buildings[building] ?: return
        val img = container.image(bitmap)
        gridElements[mineSweeper[x,y].id].buildImg = img
        img.position(x * tileScale * tileSize, y * tileScale * tileSize)
        img.scale = tileScale
        val tile = mineSweeper[x, y]
        img.onClick { onTileClick(TileInfo(tile, null)) }
    }

    private fun tileImg(x : Int, y : Int, imageNum : Int) : Image {
        val img = when (imageNum) {
            0 -> container.image(gameResources.tiles.hidden)
            1 -> container.image(gameResources.tiles.bomb)
            2 -> container.image(gameResources.tiles.empty)
            in 3..10 -> container.image(gameResources.tiles.numbers[imageNum - 2])
            else -> container.image(gameResources.tiles.unknown)
        }
        img.position(x * tileScale * tileSize, y * tileScale * tileSize)
        img.scale = tileScale
        val tile = mineSweeper[x, y]
        img.onClick { onTileClick(TileInfo(tile, null)) }
        return img
    }
}

const val tileSize = 16
const val tileScale : Double = 1.5
