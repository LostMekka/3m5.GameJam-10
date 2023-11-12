package de.lms.gj10

import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.time.*

class GameplayScene : Scene() {
    private lateinit var gridManager: GridManager
    private lateinit var unitManager: UnitManager
    private lateinit var ui: GameUi
    private var money = 0L
    private var currBuildingType: BuildingType? = null

    override suspend fun SContainer.sceneMain() {
        initializeGameResources() // must be the first thing here!

        gridManager = GridManager(this, this@GameplayScene::onTileClicked)
        gridManager.initializeGrid()

        unitManager = UnitManager(this, gridManager)

        ui = GameUi(this, this@GameplayScene::onButtonClicked)

        addFixedUpdater(1.timesPerSecond) { addIncome() }
    }

    private fun addIncome() {
        val income = gridManager.totalExtractorIncome
        changeMoney(income.toLong())
    }

    private fun onTileClicked(tileInfo: TileInfo) {
        val buildingType = currBuildingType
        val (tile, building) = tileInfo
        println("tile at (${tile.x}, ${tile.y}) clicked")
        if (building != null) return
        if (buildingType == null) {
            changeMoney(tile.number.toLong())
        } else {
            if (!tile.isRevealed && buildingType != BuildingType.Drill) return
            if (!gridManager.hasRevealedNeighbor(tile.x, tile.y)) return
            changeMoney(-buildingType.cost)
            gridManager.build(tile.x, tile.y, buildingType)
            if (!keys.shift) {
                currBuildingType = null
                ui.onBuildingTypeChange(null)
            }
        }
    }

    private fun onButtonClicked(buildingType: BuildingType) {
        println("button $buildingType clicked")
        currBuildingType = if (currBuildingType == buildingType) {
            ui.onBuildingTypeChange(null)
            null
        } else {
            if (money < buildingType.cost) {
                ui.onNotEnoughMoney()
                null
            } else {
                ui.onBuildingTypeChange(buildingType)
                buildingType
            }
        }
    }

    private fun changeMoney(diff: Long) {
        money += diff
        ui.onMoneyChanged(money)
    }
}
