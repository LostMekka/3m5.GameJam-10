package de.lms.gj10

import de.lms.gj10.minesweeper.*
import korlibs.korge.view.*
import korlibs.math.*
import korlibs.math.geom.*

class UnitManager(
    private val rootContainer: SContainer,
    private val gridManager: GridManager,
) {
    private var flowField: FlowField? = null

    init {
        updateFlowField()
    }

    fun updateFlowField() {
        flowField = FlowField(gridManager.gridInfo)
    }

    fun addUnit() {
        TODO()
    }
}

private class FlowField(
    tileInfos: List<TileInfo>
) {
    private val field: MutableList<Vector2F> = tileInfos.mapTo(mutableListOf()) { Vector2F.ZERO }
    private val width = tileInfos.maxOf { it.tile.x } + 1
    private val height = tileInfos.size / width

    init {
        val allTiles = tileInfos.map { it.tile }.sortedBy { it.index() }
        var currNodes = tileInfos.filter { it.buildingType != null }.map { it.tile }.toSet()
        val visitedNodeIds = currNodes.map { it.index() }.toMutableList()
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
        for (i in field.indices) field[i] = field[i].normalized
    }

    private fun index(x: Int, y: Int) = x + width * y
    private fun Tile.index() = index(x, y)

    fun sample(x: Float, y: Float): Vector2F {
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
}
