package de.lms.gj10

import de.lms.gj10.minesweeper.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlin.math.*
import kotlin.random.*

class UnitManager(
    private val rootContainer: SContainer,
    private val gridManager: GridManager,
) {
    private var nextUnitId = 0
    private data class UnitEntry(
        var container: Container,
        var hp: Int,
        var speed: Float,
    )

    private lateinit var flowField: FlowField
    private val unitsById = mutableMapOf<Int, UnitEntry>()

    init {
        updateFlowField()
    }

    fun updateFlowField() {
        flowField = FlowField(gridManager.gridInfo)
    }

    fun addUnit() {
        val container = rootContainer.container()
        val unitId = nextUnitId++
        unitsById[unitId] = UnitEntry(container, 100, 0.4f)
        container.position(
            x = Random.nextDouble(0.6, 2.5) * tileSize,
            y = Random.nextDouble(0.6, 2.5) * tileSize,
        )
        container.addUpdater {
            val xt = x / tileSize - 0.5
            val yt = y / tileSize - 0.5
            val direction = flowField.sample(xt, yt)
            val (dx, dy) = direction.rotate(Random.nextDouble(-45.0, 45.0).degrees)
            x += dx * it.seconds * tileSize
            y += dy * it.seconds * tileSize
            val xi = xt.roundToInt()
            val yi = yt.roundToInt()
            if (flowField.isTarget(xi, yi)) {
                val success = gridManager.attack(xi, yi, 1)
                if (success) {
                    removeUnit(unitId)
                }
            }
        }
        container.image(gameResources.tiles.spider) {
            center()
            centerOn(container)
        }
    }

    private fun removeUnit(id: Int) {
        val unit = unitsById[id] ?: return
        unitsById -= id
        unit.container.removeFromParent()
    }

    fun listEnemies() = unitsById.map { (id, unit) -> Enemy(unit.container.x, unit.container.y, unit.hp, id) }

    fun damageEnemy(enemyId: Int, damage: Int) {
        val unit = unitsById[enemyId] ?: return
        unit.hp -= damage
        if (unit.hp <= 0) removeUnit(enemyId)
    }
}

data class Enemy(
    val x: Double,
    val y: Double,
    val hp: Int,
    val id: Int,
)

private class FlowField(
    tileInfos: List<TileInfo>
) {
    private val field: MutableList<Vector2F> = tileInfos.mapTo(mutableListOf()) { Vector2F.ZERO }
    private val width = tileInfos.maxOf { it.tile.x } + 1
    private val height = tileInfos.size / width
    private val targetIndices: Set<Int>

    init {
        val allTiles = tileInfos
            .map { it.tile }
            .sortedBy { it.index() }
        var currNodes = tileInfos
            .filter { it.buildingType != null && it.buildingType != BuildingType.Nest }
            .map { it.tile }
            .toSet()
        val visitedNodeIds = currNodes
            .map { it.index() }
            .toMutableList()
        targetIndices = visitedNodeIds.toSet()
        while (currNodes.isNotEmpty()) {
            val newNodes = mutableSetOf<Tile>()
            for (n in currNodes) {
                for (dx in -1..1) {
                    val x = n.x + dx
                    if (x !in 0 until width) continue
                    for (dy in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        val y = n.y + dy
                        if (y !in 0 until height) continue
                        val i = index(x, y)
                        if (i in visitedNodeIds) continue
                        val tile = allTiles[i]
                        field[i] = field[i] + Vector2F(-dx, -dy).normalized
                        if (tile.isRevealed) newNodes += tile
                    }
                }
            }
            newNodes.mapTo(visitedNodeIds) { it.index() }
            currNodes = newNodes
        }
        for (i in field.indices) if (field[i] != Vector2F.ZERO) field[i] = field[i].normalized
    }

    private fun index(x: Int, y: Int) = x + width * y
    private fun Tile.index() = index(x, y)

    @Suppress("NAME_SHADOWING", "ReplaceRangeToWithRangeUntil")
    fun sample(x: Double, y: Double): Vector2F {
        val x = x.coerceIn(0.0..(width-1.0))
        val y = y.coerceIn(0.0..(height-1.0))
        val x1 = x.toIntFloor()
        val x2 = x.toIntCeil()
        val y1 = y.toIntFloor()
        val y2 = y.toIntCeil()
        val dx = fract(x)
        val dy = fract(y)
        val left = field[index(x1, y1)] * (1f - dy) + field[index(x1, y2)] * dy
        val right = field[index(x2, y1)] * (1f - dy) + field[index(x2, y2)] * dy
        return left * (1f - dx) + right * dx
    }

    fun isTarget(x: Int, y: Int) = index(x, y) in targetIndices
}
