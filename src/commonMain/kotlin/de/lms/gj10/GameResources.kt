package de.lms.gj10

import korlibs.image.bitmap.*
import korlibs.image.format.*
import korlibs.io.file.std.*

private var res: GameResources? = null
val gameResources by lazy { res ?: error("tried to access gameResources before it was initialized!") }

suspend fun initializeGameResources() {
    if (res != null) return
    res = GameResources(
        GameResources.Tiles(
            img("tiles/stone.png"),
            img("icons/bomb.png"),
            img("tiles/dirt.png"),
            (1..8).map { img("icons/$it.png") },
        )
    )
}

private suspend fun img(path: String) = resourcesVfs[path].readBitmap()

class GameResources(
    val tiles: Tiles,
) {
    class Tiles(
        val hidden: Bitmap,
        val bomb: Bitmap,
        val empty: Bitmap,
        val numbers: List<Bitmap>,
    )
}
