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
    fun onMoneyChanged(newMoney: Long) {
//        textMoney.plainText = 'test'
        // TODO
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
                "hello <b>world</b>, <font color=red>this</font> is a long text that won't fit!",
                RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
            ),
            size = Size(100f, 48f)
        )

        myBtn = container.generateButton()
    }

    private fun SContainer.generateButton(
//        hotkey: String = "K",
        btnSize: Int = 80,
        btnPosX: Int = 20,
        btnPosY: Int = 20,
    ): UIButton {
        // Initial Create button
        return uiButton() {
            bgColorOut = Colors.TRANSPARENT
            bgColorDisabled = Colors.TRANSPARENT
            bgColorOver = Colors.TRANSPARENT
            bgColorSelected = Colors.TRANSPARENT
            keys { down(Key.K) { onBuildBtnPress(UiBtnType.BuildFactory) } }
            onPress { onBuildBtnPress(UiBtnType.BuildFactory) }

            position(100, 100)
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
    }

}


