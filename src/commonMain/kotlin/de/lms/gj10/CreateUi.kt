package de.lms.gj10

import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.io.file.std.*
import korlibs.korge.ui.uiButton
import korlibs.korge.view.*
import korlibs.math.geom.*

suspend fun SContainer.createUi() {
//    textBlock(
//        RichTextData.fromHTML(
//            "hello <b>world</b>, <font color=red>this</font> is a long text that won't fit!",
//            RichTextData.Style.DEFAULT.copy(font = DefaultTtfFontAsBitmap)
//        ),
//        size = Size(100f, 48f)
//    )

//    uiButton(label = "1") {
//        position(10, 30 - 20.0)
//        onPress { println("TAPPED ON 1") }
//        height = 20.0
//    }
//    uiButton(label = "2") {
//        position(150, 380)
//        onPress { println("TAPPED ON 2") }
//    }

//    uiVerticalStack(width = 150.0) {
//        uiButton(label = "Open Window List") {
//            onClick {
//                container.openLazyLongListWindow()
//            }
//        }
//        uiButton(label = "Open Properties") {
//            onClick {
//                container.openPropertiesWindow()
//            }
//        }
//    }.position(0.0, 32.0)

    generateButton("a")
}

private suspend fun SContainer.generateButton(hotkey: String) {
    val glassPanel_cornerBR_Bitmap: BitmapSlice<out Bitmap> = resourcesVfs["ui/glassPanel_cornerBR.png"].readBitmapSlice()
    val hotkeyBitmap: BitmapSlice<out Bitmap> = resourcesVfs["ui/k.png"].readBitmapSlice()
    val iconBitmap: BitmapSlice<out Bitmap> = resourcesVfs["sprites/factory_red.png"].readBitmapSlice()

    // Create button
//    uiButton() {
//        bgColorOut = Colors.TRANSPARENT
//        bgColorDisabled = Colors.TRANSPARENT
//        bgColorOver = Colors.TRANSPARENT
//        bgColorSelected = Colors.TRANSPARENT
//        // Set the background image
//        size(100, 100)
//        image(glassPanel_cornerBR_Bitmap)
//
//        // Add icon at the top-left
//        image(hotkeyBitmap) {
//            position(5, 5)
//        }
//
//        // Add icon at the bottom-left
//        image(iconBitmap) {
//            position(5, height - height / 4)
//        }
//        position(300, 380)
////        anchor = Anchor.BOTTOM_RIGHT
//        onPress { println("TAPPED ON 3") }
////        bgColorOut = (0 0, 0, 0);
//    }
//    uiButton {
//        // Set the background image
////        image(glassPanel_cornerBR_Bitmap)
////
////        // Add icon at the top-left
////        image(hotkeyBitmap) {
////            position(5, 5)
////        }
////
////        // Add icon at the bottom-left
////        image(iconBitmap) {
////            position(5, height - height / 4)
////        }
//
//        // Set the button's size
//        size(200, 100)
//
//        // Add a container for custom content
//        container {
//            // Set the background image
//            image(glassPanel_cornerBR_Bitmap)
//
//            // Add icon at the top-left
//            image(hotkeyBitmap) {
//                position(5, 5)
//            }
//
//            // Add icon at the bottom-left
//            image(iconBitmap) {
//                position(5, height - height / 4)
//            }
//        }
//        position(300, 380)
//        onPress { println("TAPPED ON 3") }
////        bgColorOut = (0 0, 0, 0);
//    }


//    // Handle button click
//    button.onClick {
//        // Do something when the button is clicked
//        println("Button clicked!")
//    }

//    // Center the button on the stage
//    button.centerOn(stage)

//    uiButton(icon = glassPanel_cornerBR_Bitmap) {
//        position(300, 380)
//        onPress { println("TAPPED ON 3") }
//        width = 190.0
//        bgColorDisabled = MaterialColors.TRA
//                bgColorOut = Colors.TRANSPARENT
//        bgColorOver = Colors.TRANSPARENT
////        bgColorOut = MaterialColors.AMBER_500
////        bgColorOver = MaterialColors.AMBER_800
////        textColor = MaterialColors.BLUE_900
////        background.radius = RectCorners(16f, 0f, 12f, 4f)
//    }
//    uiButton(label = "3") {
//        position(300, 380)
//        onPress { println("TAPPED ON 3") }
//    }
}
