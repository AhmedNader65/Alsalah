package com.crazyidea.alsalah.ui.tryingggg.compass

import com.crazyidea.alsalah.utils.planetsTitles
import com.crazyidea.alsalah.utils.sunlitSideMoonTiltAngle
import io.github.cosinekitty.astronomy.Aberration
import io.github.cosinekitty.astronomy.Body
import io.github.cosinekitty.astronomy.EquatorEpoch
import io.github.cosinekitty.astronomy.Observer
import io.github.cosinekitty.astronomy.Refraction
import io.github.cosinekitty.astronomy.Time
import io.github.cosinekitty.astronomy.equator
import io.github.cosinekitty.astronomy.equatorialToEcliptic
import io.github.cosinekitty.astronomy.geoVector
import io.github.cosinekitty.astronomy.horizon
import java.util.*

class AstronomyState(observer: Observer, date: GregorianCalendar) {
    private val time = Time.fromMillisecondsSince1970(date.time.time)
    val sun = equatorialToEcliptic(geoVector(Body.Sun, time, Aberration.Corrected))
    val moon = equatorialToEcliptic(geoVector(Body.Moon, time, Aberration.Corrected))
    private val sunEquator =
        equator(Body.Sun, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
    val sunHorizon = horizon(time, observer, sunEquator.ra, sunEquator.dec, Refraction.Normal)
    private val moonEquator =
        equator(Body.Moon, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
    val moonHorizon = horizon(time, observer, moonEquator.ra, moonEquator.dec, Refraction.Normal)
    val isNight get() = sunHorizon.altitude <= -10
    val isMoonGone get() = moonHorizon.altitude <= -5
    val moonTiltAngle = sunlitSideMoonTiltAngle(time, observer).toFloat()

}
