package de.lms.gj10

import korlibs.audio.sound.*
import korlibs.image.bitmap.*
import korlibs.image.format.*
import korlibs.io.file.std.*

private var res: GameResources? = null
val gameResources by lazy { res ?: error("tried to access gameResources before it was initialized!") }

suspend fun initializeGameResources() {
    if (res != null) return
    res = GameResources(
        GameResources.Tiles(
            hidden = img("tiles/hidden.png"),
            bomb = img("icons/bomb.png"),
            stone = img("sprites/stone.png"),
            empty = img("tiles/empty.png"),
            unknown = img("icons/question_mark.png"),
            spider = img("sprites/mobs/spider.png"),
            numbers = (0..8).map { img("icons/$it.png") },
            buildings = mapOf(
                BuildingType.Base to img("sprites/BuildingRedBase.png"),
                BuildingType.Extractor to img("sprites/BuildingRedExtractor.png"),
                BuildingType.Drill to img("sprites/BuildingRedExcavator.png"),
                BuildingType.Refinery to img("sprites/BuildingRedRefinery.png"),
                BuildingType.Turret to img("sprites/BuildingRedTurret.png"),
                BuildingType.Turret2 to img("sprites/BuildingRedTurret2.png"),
                BuildingType.Nest to img("sprites/Nest.png"),
            ),
            units = mapOf(
                UnitType.Soldier to img("sprites/UnitRedSoldier.png"),
                UnitType.Grenadier to img("sprites/UnitRedGrenadier.png"),
                UnitType.Tank to img("sprites/UnitRedTank.png"),
                UnitType.RocketTank to img("sprites/UnitRedRocketTank.png"),
                UnitType.Bomber to img("sprites/UnitRedBomber.png"),
            ),
            boom = img("effects/boom_anim_2.png"),
        ),
        GameResources.Images(
            glassPanel_cornerBR_Bitmap = img("ui/glassPanel_cornerBR.png"),
            btnSelectedIcon = img("ui/selectIcon.png"),
            hotkeyBtnBitmapMap = imgMapFromAToZ(),
        ),
        GameResources.Audio(
            musicGameplay = resourcesVfs["sfx/3m5gj10.mp3"].readMusic(),
            sfxBtnSelect = resourcesVfs["sfx/sfx_btn_select.wav"].readSound(),
            sfxBtnDeselect = resourcesVfs["sfx/sfx_btn_deselect.wav"].readSound(),
            sfxBtnSelectInvalid = resourcesVfs["sfx/sfx_btn_select_invalid.wav"].readSound(),

            sfxAlienDeathscream = resourcesVfs["sfx/sfx_alien_deathscream.wav"].readSound(),
            sfxBombBoom = resourcesVfs["sfx/sfx_bomb_boom.wav"].readSound(),
            sfxBtnStart = resourcesVfs["sfx/sfx_btn_start.wav"].readSound(),
            sfxBuildingBoom = resourcesVfs["sfx/sfx_building_boom.wav"].readSound(),
            sfxBuildingBuild = resourcesVfs["sfx/sfx_building_build.wav"].readSound(),
            sfxBuildingDrill = resourcesVfs["sfx/sfx_building_drill.wav"].readSound(),
            sfxBuildingTakingDmg = resourcesVfs["sfx/sfx_building_taking-dmg.wav"].readSound(),
            sfxBuildingTurretPew = resourcesVfs["sfx/sfx_building_turret_pew.wav"].readSound(),
            sfxClickMoneyTile = resourcesVfs["sfx/sfx_click_money_tile.wav"].readSound(),
            sfxWin = resourcesVfs["sfx/sfx_win.wav"].readSound(),
            sfxLose = resourcesVfs["sfx/sfx_lose.wav"].readSound(),
            sfxLose2 = resourcesVfs["sfx/sfx_lose2.wav"].readSound(),
        ),
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
    val audio: Audio,
) {
    class Tiles(
        val hidden: Bitmap,
        val bomb: Bitmap,
        val stone: Bitmap,
        val empty: Bitmap,
        val unknown: Bitmap,
        val spider: Bitmap,
        val numbers: List<Bitmap>,
        val buildings: Map<BuildingType, Bitmap>,
        val units: Map<UnitType, Bitmap>,
        val boom: Bitmap
    )

    class Images(
        val glassPanel_cornerBR_Bitmap: Bitmap,
        val btnSelectedIcon: Bitmap,
        val hotkeyBtnBitmapMap: Map<Char, Bitmap>,
    )

    class Audio(
        val musicGameplay: Sound,
        val sfxBtnSelect: Sound,
        val sfxBtnDeselect: Sound,
        val sfxBtnSelectInvalid: Sound,

        val sfxAlienDeathscream: Sound,
        val sfxBombBoom: Sound,
        val sfxBtnStart: Sound,
        val sfxBuildingBoom: Sound,
        val sfxBuildingBuild: Sound,
        val sfxBuildingDrill: Sound,
        val sfxBuildingTakingDmg: Sound,
        val sfxBuildingTurretPew: Sound,
        val sfxClickMoneyTile: Sound,
        val sfxWin: Sound,
        val sfxLose: Sound,
        val sfxLose2: Sound,
    )
}
