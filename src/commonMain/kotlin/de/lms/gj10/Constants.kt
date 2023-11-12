package de.lms.gj10

const val windowWidth = 900
const val windowHeight = 768
const val gridWidth = 32
const val gridHeight = 32
const val tileSize = 24.0

enum class BuildingType(
    val cost: Long,
    val threatLevel: Long,
) {
    Base(25L, 1L),
    Extractor(25L, 1L),
    Drill(25L, 1L),
    Turret(25L, 1L),
    Turret2(25L, 1L),
    Refinery(25L, 1L),
}

enum class UnitType {
    Soldier,
    Grenadier,
    Tank,
    RocketTank,
    Bomber,
}
