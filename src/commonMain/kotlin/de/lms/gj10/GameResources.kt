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
            img("sprites/Nest.png"),
            img("sprites/stone.png"),
            img("tiles/empty.png"),
            img("icons/question_mark.png"),
            (0..8).map { img("icons/$it.png") },
            mapOf(
                BuildingType.Base to img("sprites/BuildingRedBase.png"),
                BuildingType.Factory to img("sprites/BuildingRedFactory.png"),
                BuildingType.Extractor to img("sprites/BuildingRedExtractor.png"),
                BuildingType.Drill to img("sprites/BuildingRedExcavator.png"),
                BuildingType.Refinery to img("sprites/BuildingRedRefinery.png"),
                BuildingType.Turret to img("sprites/BuildingRedTurret.png"),
                BuildingType.Turret2 to img("sprites/BuildingRedTurret2.png"),
            ),
            mapOf(
                UnitType.Soldier to img("sprites/UnitRedSoldier.png"),
                UnitType.Grenadier to img("sprites/UnitRedGrenadier.png"),
                UnitType.Tank to img("sprites/UnitRedTank.png"),
                UnitType.RocketTank to img("sprites/UnitRedRocketTank.png"),
                UnitType.Bomber to img("sprites/UnitRedBomber.png"),
            ),
        ),
        GameResources.Images(
            img("ui/glassPanel_cornerBR.png"),
            img("ui/selectIcon.png"),
            imgMapFromAToZ(),
        )
    )
}

private suspend fun img(path: String) = resourcesVfs[path].readBitmap()

private suspend fun imgMapFromAToZ(): Map<Char, Bitmap> {
    return ('a'..'z').associateWith { char ->
        img("ui/$char.png")
    }
}

class GameResources(
    val tiles: Tiles,
    val images: Images,
) {
    class Tiles(
        val hidden: Bitmap,
        val bomb: Bitmap,
        val nest: Bitmap,
        val stone: Bitmap,
        val empty: Bitmap,
        val unknown: Bitmap,
        val numbers: List<Bitmap>,
        val buildings: Map<BuildingType, Bitmap>,
        val units: Map<UnitType, Bitmap>,
    )

    class Images(
        val glassPanel_cornerBR_Bitmap: Bitmap,
        val btnSelectedIcon: Bitmap,
        val hotkeyBtnBitmapMap: Map<Char, Bitmap>,
    )
}
