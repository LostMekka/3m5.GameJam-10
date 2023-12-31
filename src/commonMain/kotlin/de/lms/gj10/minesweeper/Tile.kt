package de.lms.gj10.minesweeper

/**
 * A negative number on a tile means it is a bomb.
 */
data class Tile(val id: Int, val x: Int, val y: Int, var number: Int, val isRevealed: Boolean) {
    val isBomb = number < 0
    override fun equals(other: Any?) = other is Tile && other.id == id
    override fun hashCode() = id
}
