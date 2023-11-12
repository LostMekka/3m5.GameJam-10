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
    private val defaultSpacingX: Int = 8
    private val defaultSpacingY: Int = 45
    private val costTextBlocks: MutableMap<BuildingType, TextBlock> = mutableMapOf()
    private val selectIcons: MutableMap<BuildingType, Image> = mutableMapOf()
    private val buttons: MutableMap<BuildingType, UIButton> = mutableMapOf()

    private val textWidth = 100f
    private val textHeight = 48f
    private val textPosX = windowWidth - textWidth - defaultSpacingX

    private val textMoney: TextBlock
    private var money: Long = 100
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

        for((buildingType, btn) in buttons) {
            if (buildingType.cost > newMoney) {
                btn.colorMul = MaterialColors.GRAY_700
            } else {
                btn.colorMul = Colors.WHITE
            }
        }
    }

    fun onNotEnoughMoney() {

    }

    fun onBuildingTypeChange(buildingType: BuildingType?) {
        updateBtnActive(buildingType == BuildingType.Drill, btnBuildingExcavator, BuildingType.Drill)
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
            getMoneyText(money),
            size = Size(textWidth, textHeight)
        )
        textMoney.position(textPosX, defaultSpacingX)

        textScore = container.textBlock(
            getScoreText(0),
            size = Size(textWidth, textHeight)
        )
        textScore.position(textPosX, (defaultSpacingX * 2) + textHeight)

        btnBuildingExcavator = container.generateButton(
            mainImg = gameResources.tiles.buildings.getValue(BuildingType.Drill),
            type = BuildingType.Drill,
            hotKey = 'e',
            cost = BuildingType.Drill.cost,
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
    // Start/Restart/Exit / Hauptmenu neue Scene
    // UI SOUNDS
    // BG Music (MACHT STEFKA)
    // Build MUSIC
    // Build SFX
    // SHOOT SFX

    private fun SContainer.generateButton(
        btnSize: Int = 100,
        btnPosX: Int = windowWidth,
        btnPosY: Int = windowHeight,
        spacingX: Int = defaultSpacingX,
        spacingY: Int = defaultSpacingY,

        mainImg: Bitmap,
        hotKey: Char? = null,
        type: BuildingType,
        cost: Long,
    ): UIButton {
        val validHotKey = hotKey?.takeIf { it in 'a'..'z' }

        // Initial Create button
        val newBtn = uiButton() {
            if (cost > money) {
                colorMul = MaterialColors.GRAY_700
            }

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
                btnPosX - btnSize - spacingX,
                btnPosY - ((btnCount + 1) * btnSize) // place btn
                    - ((btnCount + 1) * spacingY) + (defaultSpacingY / 3) // add spacing
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

            // Building Name
            uiMaterialLayer() {
                size(btnSize * 0.95, 20)
                position(btnSize * .025, btnSize + 5)
                radius = RectCorners(4f, 4f, 4f, 4f)
                bgColor = RGBA(0x00, 0x00, 0x00, 0x87)
                borderColor = MaterialColors.BLUE_500
                borderSize = 2.0
                zIndex=0.0
            }
            val nameTextBlock = textBlock(
                RichTextData.fromHTML(
                    "<font color=white><b>${type.name}</b></font>",
                    RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
                ),
                size = Size(btnSize * .65, 15),
            )
            nameTextBlock.position(btnSize * .085, btnSize + 7.5)

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

        // Store the reference to the cost TextBlock
        buttons[type] = newBtn

        btnCount++
        return newBtn
    }

}
