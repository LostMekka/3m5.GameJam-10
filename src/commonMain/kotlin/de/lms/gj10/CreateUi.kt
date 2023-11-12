package de.lms.gj10

import BuildingType
import UnitType
import korlibs.event.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.text.*
import korlibs.korge.input.*
import korlibs.korge.render.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.logger.*
import korlibs.math.geom.*
import windowHeight
import windowWidth

//enum class UiBtnType {
////    StartGame,
////    ExitGame,
//    BuildBuilding,
//    BuildUnit,
////    BuildFactory,
////    BuildSoldier,
//}
class GameUi(
    // private val scoreTextField: Text,
    private val container : SContainer,
    private val onBuildBuildingBtnPress : (BuildingType) -> Unit,
//    private val onBuildUnitBtnPress : (UnitType) -> Unit,
) {
    private val textMoney: TextBlock
    private val myBtn: UIButton
//    private val myBtn2: UIButton
    private var btnCount: Int = 0
    private val defaultSpacing: Int = 8

    fun onMoneyChanged(newMoney: Long) {
        textMoney.text = RichTextData.fromHTML(
//                "hello <b>world</b>, <font color=red>this</font> is a long text that won't fit!",
            "<font color=gold>$</font> $newMoney",
            RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
        )
        // TODO
    }
    fun onNotEnoughMoney() {

    }
    fun onBuildingTypeChange(btnType: BuildingType?) {
//        myBtn.bgColorOut = ()
//        updateBtnActive(btnType == UiBtnType.BuildFactory, myBtn)
//        color = if UiBtnType = mytype ? RED : BLUE
    }

    private fun updateBtnActive(active: Boolean, btn: UIButton) {
//        if ()
    }

    init {
        textMoney = container.textBlock(
            RichTextData.fromHTML(
                "<font color=gold>$</font> 0",
                RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
            ),
            size = Size(100f, 48f)
        )
        textMoney.position(windowWidth - textMoney.width - defaultSpacing, defaultSpacing)

        myBtn = container.generateButton(
            mainImg = gameResources.tiles.buildings.getValue(BuildingType.Factory),
            type = BuildingType.Factory,
        )
//        myBtn2 = container.generateButton()
//        container.generateButton()
//        container.generateButton()
//        container.generateButton()
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
        btnPosX: Int = 20,
        btnPosY: Int = 20,
        spacing: Int = defaultSpacing,
        mainImg: Bitmap,
        hotKey: Char? = null,
        type: BuildingType
//        hotKey: Bitmap?,
    ): UIButton {
        // Initial Create button
        val newBtn = uiButton() {
            bgColorOut = Colors.TRANSPARENT
            bgColorDisabled = Colors.TRANSPARENT
            bgColorOver = Colors.TRANSPARENT
            bgColorSelected = Colors.TRANSPARENT
            elevation = false
            if (hotKey !== null) {
                keys { down(Key.K) {
                    onBuildBuildingBtnPress(type) }
                }
//            onPress { onBuildBtnPress(UiBtnType.BuildFactory) }
                onPress {
                    onBuildBuildingBtnPress(type)
                }
            }

            position(
                windowWidth - btnSize - spacing,
                windowHeight - ((btnCount + 1) * btnSize) // place btn
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

            val hotKeyBitmap =  gameResources.images.hotkeyBtnBitmapMap[hotKey]
            if (hotKey != null && hotKeyBitmap != null) {
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


