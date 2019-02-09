package de.digitalfrontiers.geojson.dsl

import org.assertj.core.api.SoftAssertions

fun assertSoftly(block: SoftAssertions.() -> Unit) = SoftAssertions.assertSoftly(block)
