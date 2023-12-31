package de.lms.gj10

const val windowWidth = 900
const val windowHeight = 768
const val gridWidth = 32
const val gridHeight = 32
const val tileSize = 24.0
const val sfxVolume = .4
const val sfxLoudVolume = .1
const val musicVolume = .4

enum class BuildingType(
    val cost: Long,
    val threatLevel: Long,
) {
    Base(0L, 1L),
    Nest(0L, 0L),
    Extractor(25L, 3L),
    Drill(25L, 2L),
    Turret(25L, 1L),
    Turret2(25L, 2L),
    Refinery(25L, 1L),
}

enum class UnitType {
    Soldier,
    Grenadier,
    Tank,
    RocketTank,
    Bomber,
}
