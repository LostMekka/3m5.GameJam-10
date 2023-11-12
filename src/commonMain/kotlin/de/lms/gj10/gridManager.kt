package de.lms.gj10

// bases
// attack function
// bugfix shift
//TODO:
// turrets
// background image

import de.lms.gj10.minesweeper.*
import korlibs.event.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.vector.*
import korlibs.korge.input.*
import korlibs.korge.render.SDFShaders.x
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.math.geom.Line
import korlibs.time.*
import kotlin.math.*


private data class BuildingData(
    var image : Image,
    var tempImg : Image? = null,
    var type: BuildingType,
    var timeLeft: Int = 0,
    var range: Double = 10.0,
    var hp: Int = 1,
)

private data class Animation(
    var image: Image,
    val imgList: List<Bitmap>,
    var state: Int = 0,
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
    private val container: SContainer,
    private val scene : GameplayScene,
    //private val onTileClick: (TileInfo, MouseButton) -> Unit,
    //private val onBuildingDestroy: (TileInfo) -> Unit,
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
        build(0,0, BuildingType.Nest)
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
        when (building.type) {
            BuildingType.Drill -> {
                building.timeLeft = 5
                building.image.addFixedUpdater(1.timesPerSecond) { drilling(x, y, building) }
            }
            BuildingType.Turret -> {
                building.timeLeft = 1 // shooting frequency
                building.range = 3.0
                building.image.addFixedUpdater(2.timesPerSecond) { shoot(mineSweeper[x,y].id, 10) }
            }
            BuildingType.Nest -> {
                building.image.scale *= 2
            }
            BuildingType.Base -> {
                building.image.scale *= 2
                building.hp = 50
                building.image.position((x - 1) * tileSize, (y - 1) * tileSize)
            }
            else -> {}
        }
    }

    fun attack(x : Int, y : Int, damage : Int) : Boolean{
        val building = gridElements[mineSweeper[x,y].id].building ?: return false
        building.hp -= damage
        if (building.hp <= 0) {
            // play sound - building destory
            scene.playSound(gameResources.audio.sfxBuildingBoom)

            building.image.removeFromParent()
            gridElements[mineSweeper[x,y].id].building = null
            scene.onBuildingDestroyed(TileInfo(tile= mineSweeper[x,y], buildingType= building.type))
        } else {
            // play sound - building dmg
            scene.playSound(gameResources.audio.sfxBuildingTakingDmg)
        }
        return true
    }

    private fun shoot(elemId : Int, damage : Int){
        val turret = gridElements[elemId].building ?: return
        val x = gridElements[elemId].x.toDouble()
        val y = gridElements[elemId].y.toDouble()
        val target = scene.unitManager.listEnemies()
            .firstOrNull{enemy -> (turret.range * turret.range) >= Point2.distanceSquared(x, y, enemy.x / tileSize, enemy.y / tileSize)}
        if (target == null){
            if (x+y <= turret.range) attack(0,0,damage)
            else return
        }
        else {
            scene.unitManager.damageEnemy(target.id, damage)
        }
        scene.playSound(gameResources.audio.sfxBuildingTurretPew)
//        turret.tempImg = Image(gameResources.tiles.boom)
//        turret.tempImg?.position(x * tileSize, y * tileSize)
//        turret.tempImg?.addFixedUpdater(3.timesPerSecond){ tempImgDestroy(elemId) }
    }

    private fun explode(x: Int, y: Int) {
        mineSweeper[x, y].number = 0
        reveal(x, y)
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (target in scene.unitManager.listEnemies()
                    .filter { enemy -> 2 >= Point2.distanceSquared(x.toDouble(), y.toDouble(), enemy.x / tileSize, enemy.y / tileSize) }) {
                    scene.unitManager.damageEnemy(target.id, 999)
                }
                val tile = mineSweeper[x + dx, y + dy]
                tile.number--
                if (tile.number < 0) mineSweeper[x + dx, y + dy].number = 0
                val elem = gridElements[mineSweeper[x + dx, y + dy].id]
                if (tile.isRevealed) {
                    reveal(tile.x, tile.y)
                    //if (tile.isBomb) explode(tile.x, tile.y)
                }
                if (elem.building != null) {
                    elem.building?.image?.removeFromParent()
                    gridElements[mineSweeper[x, y].id].building = null
                    scene.onBuildingDestroyed(TileInfo(tile = mineSweeper[x, y], buildingType = elem.building?.type))
                }

            }
        }
        scene.playSound(gameResources.audio.sfxBombBoom)
    }

    private fun tempImgDestroy(id: Int){
        gridElements[id].building?.tempImg?.removeFromParent()
    }

    private fun drilling(x : Int, y : Int, building : BuildingData){
        if (mineSweeper[x,y].isBomb){
            explode(x,y)
            reveal(x,y)
        }
        building.timeLeft--
        if (building.timeLeft > 0) return
        building.timeLeft = 0
        gridElements[mineSweeper[x,y].id].building?.image?.removeFromParent()
        gridElements[mineSweeper[x,y].id].building = null
        reveal(x,y)
        scene.onBuildingDestroyed(TileInfo(tile= mineSweeper[x,y], buildingType= building.type))
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
        if (x <= 1 && y <= 1) return img
        if (x >= gridWidth-2 && y >= gridHeight-2) return img
        img.onClick { clickPreProcessing(x, y, it) }
        return img
    }
    private fun clickPreProcessing(x: Int, y: Int, mouseEvents: MouseEvents){
        val tile = mineSweeper[x, y]
        val gridElement = gridElements[tile.id]
        scene.onTileClicked(TileInfo(tile = tile, buildingType = gridElement.building?.type), mouseEvents.button)
    }
}
