package de.lms.gj10

import korlibs.event.*
import korlibs.io.async.*
import korlibs.korge.component.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.time.*
import korlibs.korge.input.*

class GameplayScene : Scene() {
    private lateinit var gridManager: GridManager
    private lateinit var unitManager: UnitManager
    private lateinit var ui: GameUi
    private var money = 0L
    private var currBuildingType: BuildingType? = null

    override suspend fun SContainer.sceneMain() {
        initializeGameResources() // must be the first thing here!

        gridManager = GridManager(this, input, this@GameplayScene::onTileClicked)
        gridManager.initializeGrid()

        unitManager = UnitManager(this, gridManager)

        ui = GameUi(this, this@GameplayScene::onButtonClicked)

        addFixedUpdater(1.timesPerSecond) { addIncome() }

        var menuOpen = false
        fun openMenuWindow() {
            uiWindow("Menu", Size(windowWidth / 4, windowHeight / 4)) {
                uiButton("Restart Game") {
                    onPress {
                        launchImmediately {
                            sceneContainer.changeTo<GameplayScene>()
                        }
                    }
                    position(windowWidth / 16, windowWidth / 16)
                }

                this.onAttachDetach(onDetach = {
                    menuOpen = false
                })
            }.centerOnStage()
        }

        keys {
            down(Key.ESCAPE) {
                if (menuOpen == false) {
                    openMenuWindow()
                    menuOpen = true
                }
            }
        }
    }



    private fun addIncome() {
        val income = gridManager.totalExtractorIncome
        changeMoney(income.toLong())
    }

    private fun onTileClicked(tileInfo: TileInfo, button: MouseButton) {
        if (button == MouseButton.RIGHT) {
            currBuildingType = null
            ui.onBuildingTypeChange(null)
            return
        }
        val buildingType = currBuildingType
        val (tile, building) = tileInfo
        println("tile at (${tile.x}, ${tile.y}) clicked")
        if (building != null) return
        if (buildingType == null) {
            changeMoney(tile.number.toLong())
        } else {
            if (!hasEnoughMoney(buildingType.cost)) return
            // Building conditions:
            if (!gridManager.hasRevealedNeighbor(tile.x, tile.y)) return
            if (buildingType == BuildingType.Drill && tile.isRevealed) return
            if (buildingType != BuildingType.Drill && !tile.isRevealed) return
            if (buildingType == BuildingType.Extractor && tile.number <= 0) return
            when (buildingType){
                BuildingType.Drill -> {
                    if (tile.isRevealed) return
                }
                BuildingType.Extractor -> {
                    if (!tile.isRevealed) return
                    if (tile.number <= 0) return
                }
                else -> {
                    if (!tile.isRevealed) return
                }
            }
            // build call
            changeMoney(-buildingType.cost)
            gridManager.build(tile.x, tile.y, buildingType)
            if (!input.keys.pressing(Key.SHIFT)) {
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
            if (hasEnoughMoney(buildingType.cost)){
                ui.onBuildingTypeChange(buildingType)
                buildingType
            } else null
        }
    }

    private fun hasEnoughMoney(cost: Long):  Boolean{
        if (money >= cost) return true
        ui.onNotEnoughMoney()
        return false
    }

    private fun changeMoney(diff: Long) {
        money += diff
        ui.onMoneyChanged(money)
    }
}
