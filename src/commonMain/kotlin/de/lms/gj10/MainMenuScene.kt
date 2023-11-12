package de.lms.gj10


import korlibs.event.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.io.async.*
import korlibs.io.file.std.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.logger.AnsiEscape.Companion.bold
import korlibs.math.geom.*

class MainMenuScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        uiImage(
            Size(windowWidth, windowHeight),
            resourcesVfs["menuBackground.png"].readBitmapSlice(),
            scaleMode = ScaleMode.FIT,
            contentAnchor = Anchor.CENTER,
        )

        uiButton() {
            text = "START"
            width = 250.0
            height = 60.0
            bgColorOut = MaterialColors.GREEN_700
            bgColorOver = MaterialColors.GREEN_500
            textColor = Colors.WHITE
            textSize = 26.0
            textAlignment = TextAlignment.MIDDLE_CENTER
            background.radius = RectCorners(12f, 12f, 12f, 12f)
            elevation = true
            onPress {
                launchImmediately {
                    sceneContainer.changeTo { GameplayScene() }
                }
            }
        }.centerOnStage()
    }
}
