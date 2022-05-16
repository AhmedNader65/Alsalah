package com.crazyidea.alsalah.utils


import com.crazyidea.alsalah.R
import io.github.cosinekitty.astronomy.*
import kotlin.math.atan2

// https://github.com/cosinekitty/astronomy/blob/0547aaf/demo/csharp/camera/camera.cs#L98
fun sunlitSideMoonTiltAngle(time: Time, observer: Observer): Double {
    val moonEquator = equator(Body.Moon, time, observer, EquatorEpoch.OfDate, Aberration.None)
    val sunEquator = equator(Body.Sun, time, observer, EquatorEpoch.OfDate, Aberration.None)
    val moonHorizontal =
        horizon(time, observer, moonEquator.ra, moonEquator.dec, Refraction.None)
    val vec = rotationEqdHor(time, observer)
        .pivot(2, moonHorizontal.azimuth)
        .pivot(1, moonHorizontal.altitude)
        .rotate(sunEquator.vec)
    return Math.toDegrees(atan2(vec.z, vec.y))
}
val planetsTitles by lazy(LazyThreadSafetyMode.NONE) {
    mapOf(
        Body.Mercury to R.string.mercury,
        Body.Venus to R.string.venus,
        Body.Earth to R.string.earth,
        Body.Mars to R.string.mars,
        Body.Jupiter to R.string.jupiter,
        Body.Saturn to R.string.saturn,
        Body.Uranus to R.string.uranus,
        Body.Neptune to R.string.neptune,
        Body.Pluto to R.string.pluto,
        Body.Sun to R.string.sun,
        Body.Moon to R.string.moon,
    ).withDefault { R.string.empty }
}

