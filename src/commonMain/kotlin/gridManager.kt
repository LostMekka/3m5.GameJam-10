import de.lms.gj10.*
import de.lms.gj10.minesweeper.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.view.*

data class GridElement(
    var image : Image,
    val id : Int,
    var imageNum : Int = -1, // 0 = hidden, 1 = bomb, 2 = empty uncovered, 3-11 = numbers(1-9)
    )

val Tile.imageNum : Int get() {
    if (!isRevealed) return 0
    if (isBomb) return 1
    return number + 2
}

class GridManager(private val container : SContainer) {
    private val gridElements = mutableListOf<GridElement>()
    private val mineSweeper = generateSolvableMinesweeperGrid(32, 32, 100)
    suspend fun initializeGrid() = container.apply {
        /*val image = image(resourcesVfs["korge.png"].readBitmap()) {
        rotation = 45
        anchor(.5, .5)
        scale(0.8)
        position(256, 256)
    }*/
        for (x in 0 until mineSweeper.width) {
            for (y in 0 until mineSweeper.height) {
                val tile = mineSweeper[x,y]
                val img = tileImg(x,y,tile.imageNum)
                gridElements += GridElement(img,tile.id ,tile.imageNum)
            }
        }
        gridElements.sortBy { it.id }
    }

    suspend fun reveal(x : Int, y : Int) {
        val tile = mineSweeper[x,y]
        val gridElement = gridElements[tile.id]
        mineSweeper.reveal(tile)
        gridElement.image.removeFromParent()
        gridElement.imageNum = tile.imageNum
        gridElement.image = tileImg(x,y,gridElement.imageNum)
    }

    private suspend fun tileImg(x : Int, y : Int, imageNum : Int) : Image{
        val img = when (imageNum) {
            0 -> container.image(gameResources.tiles.hidden)
            1 -> container.image(gameResources.tiles.bomb)
            2 -> container.image(gameResources.tiles.empty)
            else -> container.image(gameResources.tiles.numbers[imageNum - 2])
        }
        img.position(x * tileScale * tileSize,y * tileScale * tileSize)
        img.scale = tileScale
        return img
    }
}

const val tileSize = 16
const val tileScale : Double = 1.5
