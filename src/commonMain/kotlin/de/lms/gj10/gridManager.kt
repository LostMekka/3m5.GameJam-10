package de.lms.gj10

//TODO:
// bases
// bugfix shift
// turrets


import de.lms.gj10.minesweeper.*
import korlibs.image.color.*
import korlibs.korge.input.*
import korlibs.korge.view.*
import korlibs.time.*


private data class BuildingData(
    var image : Image,
    var type: BuildingType,
    var timeLeft: Int = 0,
)

private data class GridElement(
    var image : Image,
    var x : Int,
    var y : Int,
    var building : BuildingData? = null,
    val id : Int,
    var imageNum : Int = -1, // 0 = hidden, 1 = bomb, 2 = empty uncovered, 3-10 = numbers(1-8), 10+ = buildings
    )

data class TileInfo(
    val tile : Tile,
    val buildingType : BuildingType? = null,
)

private val Tile.imageNum : Int get() {
    if (!isRevealed) return 0
    if (isBomb) return 1
    return number + 2
}

class GridManager(
    private val container : SContainer,
    private val onTileClick : (TileInfo) -> Unit,
) {

    private val gridElements = mutableListOf<GridElement>()
    private val mineSweeper = generateSolvableMinesweeperGrid(gridWidth, gridHeight, 150)
    val totalExtractorIncome : Int get(){
        return gridElements
            .filter { it.building?.type == BuildingType.Extractor }
            .sumOf { mineSweeper[it.x, it.y].number }
    }
    val gridInfo : List<TileInfo> get() = mineSweeper.map { TileInfo(tile = it, buildingType = gridElements[it.id].building?.type) }
    fun hasRevealedNeighbor(x : Int , y : Int) : Boolean {
        for (dx in -1..1) {
            for (dy in -1..1) {
                if (x+dx < 0 || x+dy < 0 || x+dx >= gridWidth || y+dy >= gridHeight || (dx+dy)%2 == 0) continue
                if (mineSweeper[dx + x, dy + y].isRevealed) return true
            }
        }
        return false
    }
    fun initializeGrid() = container.apply {
        for (x in 0 until mineSweeper.width) {
            for (y in 0 until mineSweeper.height) {
                val tile = mineSweeper[x,y]
                val img = tileImg(x,y,tile.imageNum)
                gridElements += GridElement(image = img,x = x, y = y, id = tile.id, imageNum = tile.imageNum)
            }
        }
        build(gridWidth-1,gridHeight-1, BuildingType.Base)
        build(0,0, BuildingType.Base)
        gridElements.sortBy { it.id }
    }

    fun reveal(x : Int, y : Int) {
        val oldTile = mineSweeper[x,y]
        val revealedTiles = mineSweeper.autoReveal(oldTile)
        for (newTile in revealedTiles) {
            val gridElement = gridElements[newTile.id]
            gridElement.image.removeFromParent()
            gridElement.imageNum = newTile.imageNum
            gridElement.image = tileImg(newTile.x, newTile.y, gridElement.imageNum)
        }
    }

    fun build(x: Int, y : Int, buildingType : BuildingType){
        val bitmap = gameResources.tiles.buildings[buildingType] ?: return
        val building = BuildingData(image = container.image(bitmap), type = buildingType)
        val tile = mineSweeper[x, y]
        gridElements[tile.id].building = building
        building.image.position(x * tileSize, y * tileSize)
        building.image.scale = tileSize / building.image.size.width
        if (building.type == BuildingType.Drill){
            building.timeLeft = 5
            building.image.addFixedUpdater(1.timesPerSecond) { drilling(x,y,building) }
        }
    }

    private fun drilling(x : Int, y : Int, building : BuildingData){
        building.timeLeft--
        if (building.timeLeft > 0) return
        building.timeLeft = 0
        gridElements[mineSweeper[x,y].id].building?.image?.removeFromParent()
        reveal(x,y)
    }

    private fun tileImg(x : Int,y : Int, imageNum: Int) : Image {
        val img = when (imageNum) {
            0 -> container.image(gameResources.tiles.hidden)
            1 -> container.image(gameResources.tiles.bomb)
            2 -> container.image(gameResources.tiles.empty)
            in 3..10 -> container.image(gameResources.tiles.numbers[imageNum - 2]) {
                when (imageNum) {
                    3 -> colorMul = RGBA(0xFF, 0xF2, 0xE0, 0xFF) // #fff2e0
                    4 -> colorMul = RGBA(0xFF, 0xD4, 0x99, 0xFF) // #ffd499
                    5 -> colorMul = RGBA(0xFF, 0xBC, 0x60, 0xFF) //y #ffbc60
                    6 -> colorMul = RGBA(0xFF, 0xAE, 0x40, 0xFF) //y #ffae40
                    7 -> colorMul = RGBA(0xFF, 0xA6, 0x20, 0xFF) //y #ffa620
                    8 -> colorMul = RGBA(0xFF, 0xA3, 0x10, 0xFF) //y #ffa310
                    9 -> colorMul = RGBA(0xFF, 0xA1, 0x08, 0xFF) //y #ffa108
                    10 -> colorMul = RGBA(0xFF, 0xA0, 0x04, 0xFF) //y #ffa004
                }
            }
            else -> container.image(gameResources.tiles.unknown)
        }
        img.position(x * tileSize, y * tileSize)
        img.scale = tileSize / img.size.width
        img.onClick { clickPreProcessing(x, y) }
        return img
    }
    private fun clickPreProcessing(x : Int, y : Int){
        val tile = mineSweeper[x, y]
        val gridElement =  gridElements[tile.id]
        onTileClick(TileInfo(tile = tile, buildingType = gridElement.building?.type))
    }
}
