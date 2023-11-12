package de.lms.gj10

import BuildingType
import korlibs.event.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.text.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import windowHeight
import windowWidth


class GameUi(
    private val container : SContainer,
    private val onBuildBuildingBtnPress : (BuildingType) -> Unit,
//    private val onBuildUnitBtnPress : (UnitType) -> Unit,
) {
    private var btnCount: Int = 0
    private val defaultSpacing: Int = 8

    private val textWidth = 100f
    private val textHeight = 48f
    private val textPosX = windowWidth - textWidth - defaultSpacing

    private val textMoney: TextBlock
    private val textScore: TextBlock
    private val btnBuildingExcavator: UIButton
    private val btnBuildingExtractor: UIButton
    private val btnBuildingTurret: UIButton

    fun getMoneyText(newMoney: Long): RichTextData {
        return RichTextData.fromHTML(
            "<font color=gold>$</font> <font color=green>$newMoney</font>",
            RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
        )
    }
    fun getScoreText(newScore: Long): RichTextData {
        return RichTextData.fromHTML(
            "<font color=lightblue>Score</font> $newScore",
            RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
        )
    }

    fun onMoneyChanged(newMoney: Long) {
        textMoney.text = getMoneyText(newMoney)
    }
    fun onNotEnoughMoney() {

    }
    fun onBuildingTypeChange(btnType: BuildingType?) {
        updateBtnActive(btnType == BuildingType.Excavator, btnBuildingExcavator)
        updateBtnActive(btnType == BuildingType.Extractor, btnBuildingExtractor)
        updateBtnActive(btnType == BuildingType.Turret, btnBuildingTurret)
    }

    private fun updateBtnActive(active: Boolean, btn: UIButton) {
        if (active) {
            btn.colorMul = Colors.RED
        } else {
            btn.colorMul = Colors.BLUE
        }
    }

    init {
        textMoney = container.textBlock(
            getMoneyText(0),
            size = Size(textWidth, textHeight)
        )
        textMoney.position(textPosX, defaultSpacing)

        textScore = container.textBlock(
            getScoreText(0),
            size = Size(textWidth, textHeight)
        )
        textScore.position(textPosX, (defaultSpacing * 2) + textHeight)

        btnBuildingExcavator = container.generateButton(
            mainImg = gameResources.tiles.buildings.getValue(BuildingType.Excavator),
            type = BuildingType.Excavator,
            hotKey = 'e'
        )
        btnBuildingExtractor = container.generateButton(
            mainImg = gameResources.tiles.buildings.getValue(BuildingType.Extractor),
            type = BuildingType.Extractor,
            hotKey = 'w'
        )
        btnBuildingTurret = container.generateButton(
            mainImg = gameResources.tiles.buildings.getValue(BuildingType.Turret),
            type = BuildingType.Turret,
            hotKey = 'q'
        )
    }

    // TODO
    // BTN AUSGRAUEN WENN NICHT GENUG GELD
    //  UPDATE COST ON BTN
    // ADD COST ON BTN
    // Start/Restart/Exit / Hauptmenu neue Scene
    // UI SOUNDS
    // BG Music (MACHT STEFKA)
    // Build MUSIC
    // SHOOT SFX

    private fun SContainer.generateButton(
        btnSize: Int = 80,
        btnPosX: Int = windowWidth,
        btnPosY: Int = windowHeight,
        spacing: Int = defaultSpacing,
        mainImg: Bitmap,
        hotKey: Char? = null,
        type: BuildingType
    ): UIButton {
        val validHotKey = hotKey?.takeIf { it in 'a'..'z' }

        // Initial Create button
        val newBtn = uiButton() {
            bgColorOut = Colors.TRANSPARENT
            bgColorDisabled = Colors.TRANSPARENT
            bgColorOver = Colors.TRANSPARENT
            bgColorSelected = Colors.TRANSPARENT
            elevation = false
            if (validHotKey !== null) {
                keys {
                    // Use a when statement with a range of possible keys
                    down(when (validHotKey) {
                        in 'a'..'z' -> Key.valueOf("${validHotKey.uppercaseChar()}") // Assuming Key enum has constants like KEY_A, KEY_B, etc.
                        else -> Key.UNKNOWN // or any default Key you want to use
                    }) {
                        onBuildBuildingBtnPress(type)
                    }
                }

                onPress { onBuildBuildingBtnPress(type) }
            }

            position(
                btnPosX - btnSize - spacing,
                btnPosY - ((btnCount + 1) * btnSize) // place btn
                    - ((btnCount + 1) * spacing) // add spacing
            )
            size(btnSize, btnSize)
            background.radius = RectCorners(btnSize / 12, btnSize / 12, btnSize / 4, btnSize / 12)


            // Background Image
            image(gameResources.images.glassPanel_cornerBR_Bitmap) {
                smoothing = false
                size(btnSize, btnSize)
                zIndex=-2.0
            }

            // Main Image
            image(mainImg) {
                smoothing = false
                size(btnSize * .8, btnSize * .8)
                position(btnSize * .1, btnSize * .1)
                zIndex=-1.0
            }

            val hotKeyBitmap =  gameResources.images.hotkeyBtnBitmapMap[validHotKey]
            if (validHotKey != null && hotKeyBitmap != null) {
                // Hotkey Image
                image(hotKeyBitmap) {
                    smoothing = false
                    size(btnSize * .4, btnSize * .4)
                    position(-btnSize * .1, -btnSize * .1)
                    zIndex=-1.0
                }
            }
        }

        btnCount++
        return newBtn
    }

}
