package de.digitalfrontiers.geojson.dsl

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.geojson.FeatureCollection
import org.geojson.LineString
import org.geojson.Point
import org.junit.jupiter.api.Test

class GeoJsonDslTest {

    @Test
    fun `can create empty FeatureCollection`() {
        val subject = featureCollection {  }

        assertThat(subject).isInstanceOfSatisfying(FeatureCollection::class.java) {
            assertThat(it.features).isEmpty()
        }
    }

    @Test
    fun `can create Points with and without properties within FeatureCollection`() {
        val subject = featureCollection {
            point(lng = 1.1, lat = 1.2)
            point(lng = 2.1, lat = 2.2) {
                "key" value 42
            }
        }

        assertSoftly {
            assertThat(subject.features).hasSize(2)
            assertThat(subject.first()).satisfies {
                assertThat(it.geometry).isInstanceOfSatisfying(Point::class.java) {
                    assertThat(it.coordinates.longitude).isEqualTo(1.1)
                    assertThat(it.coordinates.latitude).isEqualTo(1.2)
                }
                assertThat(it.properties).isEmpty()
            }
            assertThat(subject.last()).satisfies {
                assertThat(it.geometry).isInstanceOfSatisfying(Point::class.java) {
                    assertThat(it.coordinates.longitude).isEqualTo(2.1)
                    assertThat(it.coordinates.latitude).isEqualTo(2.2)
                }
                assertThat(it.properties)
                    .hasSize(1)
                    .containsEntry("key", 42)
            }
        }
    }

    @Test
    fun `can create LineString within FeatureCollection`() {
        val subject = featureCollection {
            lineString {
                coord lng 1.1 lat 1.2
                coord lat 2.2 lng 2.1

                "key" value 42
            }
        }

        assertThat(subject.features).hasSize(1).allSatisfy {
            assertThat(it.geometry).isInstanceOfSatisfying(LineString::class.java) {
                assertThat(it.coordinates).hasSize(2)
                assertThat(it.coordinates.first().longitude).isEqualTo(1.1)
                assertThat(it.coordinates.first().latitude).isEqualTo(1.2)
                assertThat(it.coordinates.last().longitude).isEqualTo(2.1)
                assertThat(it.coordinates.last().latitude).isEqualTo(2.2)
            }
            assertThat(it.properties)
                .hasSize(1)
                .containsEntry("key", 42)
        }
    }

    @Test
    fun `detects duplicate Point Feature properties`() {
        assertThatThrownBy {
            featureCollection {
                point(1.1, 1.2) {
                    "key" value 42
                    "key" value 43
                }
            }
        }.isInstanceOf(AssertionError::class.java).hasMessageContaining("key")
    }

    @Test
    fun `detects duplicate LineString Feature properties`() {
        assertThatThrownBy {
            featureCollection {
                lineString {
                    coord lng 1.1 lat 1.2
                    coord lat 2.2 lng 2.1

                    "key" value 42
                    "key" value 43
                }
            }
        }.isInstanceOf(AssertionError::class.java).hasMessageContaining("key")
    }

    @Test
    fun `detects minimum LineString coordinates not met`() {
        assertThatThrownBy {
            featureCollection {
                lineString {
                    coord lng 1.1 lat 1.2
                }
            }
        }.isInstanceOf(AssertionError::class.java).hasMessageContaining("at least two coordinates")
    }
}
