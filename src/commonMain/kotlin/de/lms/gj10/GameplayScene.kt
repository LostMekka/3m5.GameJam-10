package de.lms.gj10

import korlibs.audio.sound.*
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
    lateinit var gridManager: GridManager
    lateinit var unitManager: UnitManager
    lateinit var ui: GameUi
    private var money = 100L
    private var currBuildingType: BuildingType? = null
    private var musicChannel: SoundChannel? = null

    override suspend fun SContainer.sceneMain() {
        initializeGameResources() // must be the first thing here!

        gridManager = GridManager(this, this@GameplayScene)
        gridManager.initializeGrid()

        unitManager = UnitManager(this, gridManager)

        ui = GameUi(this, this@GameplayScene::onButtonClicked)

        addFixedUpdater(1.timesPerSecond) { addIncome() }
        addFixedUpdater(0.2.timesPerSecond) { unitManager.addUnit() }

        addEscapeMenu()
        musicChannel = gameResources.audio.musicGameplay.playForever().also {
            it.volume = 0.5
        }
    }

    override suspend fun sceneBeforeLeaving() {
        musicChannel?.stop()
    }

    private fun SContainer.addEscapeMenu() {
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

                onAttachDetach(onDetach = {
                    menuOpen = false
                })
            }.centerOnStage()
        }

        keys {
            down(Key.ESCAPE) {
                if (!menuOpen) {
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

    fun onTileClicked(tileInfo: TileInfo, button: MouseButton) {
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
            when (buildingType) {
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
            unitManager.updateFlowField()
            if (!input.keys.pressing(Key.SHIFT)) {
                currBuildingType = null
                ui.onBuildingTypeChange(null)
            }
        }
    }

    private fun onButtonClicked(buildingType: BuildingType) {
        println("button $buildingType clicked")

        launchImmediately {
            if (money >= buildingType.cost) {
                if (currBuildingType == buildingType) {
                    val sound = gameResources.audio.sfxBtnDeselect
                    sound.volume = sfxLoudVolume
                    sound.play()
                } else {
                    val sound = gameResources.audio.sfxBtnSelect
                    sound.volume = sfxLoudVolume
                    sound.play()
                }
            } else {
                val sound = gameResources.audio.sfxBtnSelectInvalid
                sound.volume = sfxVolume
                sound.play()
            }
        }

        currBuildingType = if (currBuildingType == buildingType) {
            ui.onBuildingTypeChange(null)
            null
        } else {
            if (hasEnoughMoney(buildingType.cost)) {
                ui.onBuildingTypeChange(buildingType)
                buildingType
            } else null
        }
    }

    fun onBuildingDestroyed(tileInfo: TileInfo) {
        unitManager.updateFlowField()
    }

    private fun hasEnoughMoney(cost: Long): Boolean {
        if (money >= cost) return true
        ui.onNotEnoughMoney()
        return false
    }

    private fun changeMoney(diff: Long) {
        money += diff
        ui.onMoneyChanged(money)
    }
}
