package de.digitalfrontiers.geojson.dsl

import org.geojson.*


abstract class FeatureDsl {

    private val properties = mutableMapOf<String, Any>()

    infix fun String.value(property: Any) {
        assert(!properties.containsKey(this)) { "Duplicate property assignment: $this" }
        properties[this] = property
    }

    internal fun toGeoJson() =
        Feature().apply {
            geometry = geoJsonObject()
            properties = this@FeatureDsl.properties
        }

    protected abstract fun geoJsonObject(): GeoJsonObject
}

class PointFeatureDsl(val lngLat: LngLatAlt) : FeatureDsl() {
    override fun geoJsonObject(): GeoJsonObject = Point(lngLat)
}

class LineStringFeatureDsl : FeatureDsl() {

    private val coordinates = mutableListOf<LngLatAlt>()

    override fun geoJsonObject(): GeoJsonObject {
        assert(coordinates.size >= 2) { "A LineString must have at least two coordinates." }
        return LineString().apply {
            coordinates = this@LineStringFeatureDsl.coordinates
        }
    }

    fun coord(lng: Double, lat: Double) {
        coordinates.add(LngLatAlt(lng, lat))
    }

    val coord: CoordStart
        get() = CoordStart()

    inner class CoordStart {
        infix fun lng(lng: Double) = CoordLng(lng)
        infix fun lat(lat: Double) = CoordLat(lat)

        inner class CoordLng(private val lng: Double) {
            infix fun lat(lat: Double) {
                coord(lng = lng, lat = lat)
            }
        }

        inner class CoordLat(private val lat: Double) {
            infix fun lng(lng: Double) {
                coord(lng = lng, lat = lat)
            }
        }
    }
}

class FeatureCollectionDsl {

    private val features = mutableListOf<Feature>()

    private fun <T: FeatureDsl> add(feature: T, init: T.() -> Unit) {
        feature.init()
        features.add(feature.toGeoJson())
    }

    internal fun toGeoJson(): FeatureCollection =
        FeatureCollection().apply {
            addAll(this@FeatureCollectionDsl.features)
        }

    fun point(lng: Double, lat: Double, init: PointFeatureDsl.() -> Unit = {}): Unit =
        add(PointFeatureDsl(LngLatAlt(lng, lat)), init)

    fun lineString(init: LineStringFeatureDsl.() -> Unit): Unit =
        add(LineStringFeatureDsl(), init)
}

fun featureCollection(init: FeatureCollectionDsl.() -> Unit): FeatureCollection =
    FeatureCollectionDsl()
        .apply(init)
        .toGeoJson()



