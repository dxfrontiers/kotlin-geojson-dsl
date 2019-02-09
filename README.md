# Kotlin Builder DSL for GeoJson

This project contains domain specific language (DSL) for creating 
[GeoJson POJOs for Jackson](https://github.com/opendatalab-de/geojson-jackson).
Currently it supports creation of the following object:
* ``FeatureCollection``
* ``Point`` features
* ``LineString`` features

## Example Usage
````kotlin
featureCollection {
        point(13.404148, 52.513806)
        point(8.668799, 50.109993)
        point(9.179614, 48.776450) {
            "name" value "Stuttgart"
        }

        lineString {
            coord lat 52.554265 lng 13.292653
            coord lat 50.037919 lng 8.562066
            coord lng 9.205651 lat 48.687849

            "description" value "Berlin -> Frankfurt -> Stuttgart"
            "distance" value 565
        }
    }
````
