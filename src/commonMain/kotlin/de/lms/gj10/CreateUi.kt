package de.lms.gj10

import korlibs.event.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.text.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.math.geom.*


class GameUi(
    private val container : SContainer,
    private val onBuildBuildingBtnPress : (BuildingType) -> Unit,
//    private val onBuildUnitBtnPress : (UnitType) -> Unit,
) {
    private var btnCount: Int = 0
    private val defaultSpacing: Int = 8
    private val costTextBlocks: MutableMap<BuildingType, TextBlock> = mutableMapOf()
    private val selectIcons: MutableMap<BuildingType, Image> = mutableMapOf()

    private val textWidth = 100f
    private val textHeight = 48f
    private val textPosX = windowWidth - textWidth - defaultSpacing

    private val textMoney: TextBlock
    private var money: Long = 0
    private val textScore: TextBlock
    private val btnBuildingExcavator: UIButton
    private val btnBuildingExtractor: UIButton
    private val btnBuildingTurret: UIButton

    private fun getMoneyText(newMoney: Long): RichTextData {
        return RichTextData.fromHTML(
            "<font color=gold>$</font> <font color=green>$newMoney</font>",
            RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
        )
    }
    private fun getScoreText(newScore: Long): RichTextData {
        return RichTextData.fromHTML(
            "<font color=lightblue>Score</font> $newScore",
            RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
        )
    }
    private fun getCostText(newCost: Long): RichTextData {
        return RichTextData.fromHTML(
            "<font color=white><b>$newCost</b></font>",
            RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
        )
    }

    fun onMoneyChanged(newMoney: Long) {
        money = newMoney
        textMoney.text = getMoneyText(newMoney)
    }
    fun onNotEnoughMoney() {

    }
    fun onBuildingTypeChange(buildingType: BuildingType?) {
        updateBtnActive(buildingType == BuildingType.Excavator, btnBuildingExcavator, BuildingType.Excavator)
        updateBtnActive(buildingType == BuildingType.Extractor, btnBuildingExtractor, BuildingType.Extractor)
        updateBtnActive(buildingType == BuildingType.Turret, btnBuildingTurret, BuildingType.Turret)
    }
    fun onBuildingCostChange(buildingType: BuildingType, newCost: Long) {
        // Retrieve the cost TextBlock associated with the buildingType
        val costTextBlock = costTextBlocks[buildingType]

        // Update the cost TextBlock if found
        costTextBlock?.text = getCostText(newCost)
    }

    private fun updateBtnActive(active: Boolean, btn: UIButton, buildingType: BuildingType?) {
        println(buildingType)
        val currentSelectIcon = selectIcons[buildingType]
        println(currentSelectIcon)
        if (active) {
            currentSelectIcon?.colorMul = Colors.WHITE
        } else {
            currentSelectIcon?.colorMul = Colors.TRANSPARENT_WHITE
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
            hotKey = 'e',
            cost = BuildingType.Excavator.cost,
        )
        btnBuildingExtractor = container.generateButton(
            mainImg = gameResources.tiles.buildings.getValue(BuildingType.Extractor),
            type = BuildingType.Extractor,
            hotKey = 'w',
            cost = BuildingType.Extractor.cost,
        )
        btnBuildingTurret = container.generateButton(
            mainImg = gameResources.tiles.buildings.getValue(BuildingType.Turret),
            type = BuildingType.Turret,
            hotKey = 'q',
            cost = BuildingType.Turret.cost,
        )
    }

    // TODO
    // BTN AUSGRAUEN WENN NICHT GENUG GELD
    // Start/Restart/Exit / Hauptmenu neue Scene
    // UI SOUNDS
    // BG Music (MACHT STEFKA)
    // Build MUSIC
    // Build SFX
    // SHOOT SFX

    private fun SContainer.generateButton(
        btnSize: Int = 80,
        btnPosX: Int = windowWidth,
        btnPosY: Int = windowHeight,
        spacing: Int = defaultSpacing,

        mainImg: Bitmap,
        hotKey: Char? = null,
        type: BuildingType,
        cost: Long,
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

            // Selected Image
            val selectImg = image(gameResources.images.btnSelectedIcon) {
                smoothing = false
                size(btnSize, btnSize)
                colorMul = Colors.TRANSPARENT_WHITE
                zIndex=-1.0
            }
            // Store the reference to the cost TextBlock
            selectIcons[type] = selectImg

            // Cost Text
            uiMaterialLayer() {
                size(btnSize * .7, 20)
                position(btnSize * .26, -btnSize * .06)
                radius = RectCorners(4f, 4f, 4f, 4f)
                bgColor = RGBA(0x00, 0x00, 0x00, 0x87)
                borderColor = Colors.LIGHTBLUE
                borderSize = 2.0
                zIndex=0.0
            }
            val costTextBlock = textBlock(
                getCostText(cost),
                size = Size(btnSize * .65, 15),
            )
            costTextBlock.position(btnSize * .32, -btnSize * .03)
            // Store the reference to the cost TextBlock
            costTextBlocks[type] = costTextBlock

            // hotkey
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
