package de.lms.gj10

import korlibs.event.*
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

enum class UiBtnType {
    BuildFactory,
}
class GameUi(
    // private val scoreTextField: Text,
    private val container : SContainer,
    private val onBuildBtnPress : (UiBtnType) -> Unit,
) {
    private val textMoney: TextBlock
    private val myBtn: UIButton
    private val myBtn2: UIButton
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
    fun onBtnTypeChange(btnType: UiBtnType?) {
//        myBtn.bgColorOut = ()
        updateBtnActive(btnType == UiBtnType.BuildFactory, myBtn)
//        color = if UiBtnType = mytype ? RED : BLUE
    }

    private fun updateBtnActive(active: Boolean, btn: UIButton) {
//        if ()
    }

    init {
        textMoney = container.textBlock(
            RichTextData.fromHTML(
//                "hello <b>world</b>, <font color=red>this</font> is a long text that won't fit!",
                "<font color=gold>$</font> 0",
                RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
            ),
            size = Size(200f, 148f)
        )
        textMoney.position(windowWidth - textMoney.width - defaultSpacing, defaultSpacing)

        myBtn = container.generateButton()
        myBtn2 = container.generateButton()
        container.generateButton()
        container.generateButton()
        container.generateButton()
    }

    private fun SContainer.generateButton(
//        hotkey: String = "K",
        btnSize: Int = 80,
        btnPosX: Int = 20,
        btnPosY: Int = 20,
        spacing: Int = defaultSpacing,
    ): UIButton {
        // Initial Create button
        val newBtn = uiButton() {
            bgColorOut = Colors.TRANSPARENT
            bgColorDisabled = Colors.TRANSPARENT
            bgColorOver = Colors.TRANSPARENT
            bgColorSelected = Colors.TRANSPARENT
            keys { down(Key.K) { onBuildBtnPress(UiBtnType.BuildFactory) } }
            onPress { onBuildBtnPress(UiBtnType.BuildFactory) }

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
            }

            // Main Image
            image(gameResources.images.iconBitmap) {
                smoothing = false
                size(btnSize * .8, btnSize * .8)
                position(btnSize * .1, btnSize * .1)
            }

            // Hotkey Image
            image(gameResources.images.hotkeyBitmap) {
                smoothing = false
                size(btnSize * .4, btnSize * .4)
                position(-btnSize * .1, -btnSize * .1)
            }
        }
        btnCount++
        return newBtn
    }

}


