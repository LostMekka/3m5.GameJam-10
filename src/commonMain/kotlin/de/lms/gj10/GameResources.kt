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
            img("tiles/hidden.png"),
            img("icons/bomb.png"),
            img("tiles/empty.png"),
            img("icons/question_mark.png"),
            (0..8).map { img("ui/$it.png") },
            img("sprites/factory_red.png"),
        ),
        GameResources.Images(
            img("ui/glassPanel_cornerBR.png"),
            img("ui/k.png"),
            img("sprites/factory_red.png"),
        )
    )
}

private suspend fun img(path: String) = resourcesVfs[path].readBitmap()

class GameResources(
    val tiles: Tiles,
    val images: Images,
) {
    class Tiles(
        val hidden: Bitmap,
        val bomb: Bitmap,
        val empty: Bitmap,
        val unknown: Bitmap,
        val numbers: List<Bitmap>,
        val factory: Bitmap,
    )
    class Images(
        val glassPanel_cornerBR_Bitmap: Bitmap,
        val hotkeyBitmap: Bitmap,
        val iconBitmap: Bitmap,
    )
}
