package de.lms.gj10

import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.text.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
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
    fun onBtnTypeChange(btnType: UiBtnType) {
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

        myBtn = container.generateButton("a")
    }

    private fun SContainer.generateButton(hotkey: String): UIButton {
        val btnSize = 500
        val btnPosX = 20
        val btnPosY = 20


        // Initial Create button
        return uiButton() {
            bgColorOut = Colors.TRANSPARENT
            bgColorDisabled = Colors.TRANSPARENT
            bgColorOver = Colors.TRANSPARENT
            bgColorSelected = Colors.TRANSPARENT
//        colorMul  = Colors.TRANSPARENT
//        color = Colors.RED

//        bgColorOut = Colors.TRANSPARENT_WHITE
//        bgColorDisabled = Colors.TRANSPARENT_WHITE
//        bgColorOver = Colors.TRANSPARENT_WHITE
//        bgColorSelected = Colors.TRANSPARENT_WHITE
            // Set the background image
            size(btnSize, btnSize)
            background.radius = RectCorners(btnSize / 12, btnSize / 12, btnSize / 4, btnSize / 12)
            position(10, 10)
            onPress { println("TAPPED ON 3") }

            image(gameResources.images.glassPanel_cornerBR_Bitmap) {
                smoothing = false
                size(btnSize, btnSize)
            }

            // Hotkey Image
            image(gameResources.images.hotkeyBitmap) {
                smoothing = false
                position(5, 5)
            }

            // Main Image
            image(gameResources.images.iconBitmap) {
                smoothing = false
                position(0, height - height / 4)
                size(btnSize / 2, btnSize / 2)
            }
        }
    }

}


