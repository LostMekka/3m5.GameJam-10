package de.lms.gj10

import korlibs.math.geom.*

fun Vector2F.rotate(angle: Angle): Vector2F {
    val cos = cos(angle)
    val sin = sin(angle)
    return Vector2F(
        (x * cos - y * sin).toFloat(),
        (x * sin + y * cos).toFloat(),
    )
}
