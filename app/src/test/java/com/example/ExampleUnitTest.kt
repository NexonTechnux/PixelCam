package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCameraModesAndResolutions() {
    val modes = com.example.model.CameraMode.values()
    assertTrue(modes.contains(com.example.model.CameraMode.NIGHT))
    assertTrue(modes.contains(com.example.model.CameraMode.PRO))

    val lenses = com.example.model.ZoomLens.values()
    assertEquals(3, lenses.size)
    assertEquals(0.5f, com.example.model.ZoomLens.ULTRA_WIDE.ratio, 0.01f)
    assertEquals(1.0f, com.example.model.ZoomLens.WIDE.ratio, 0.01f)
    assertEquals(2.0f, com.example.model.ZoomLens.TELE_CROP.ratio, 0.01f)

    val resolutions = com.example.model.VideoResolution.values()
    assertEquals(4, resolutions.size)
  }
}
