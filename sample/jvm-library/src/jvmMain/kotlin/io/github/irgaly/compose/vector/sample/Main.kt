package io.github.irgaly.compose.vector.sample

import io.github.irgaly.compose.Logger
import io.github.irgaly.compose.vector.ImageVectorGenerator
import io.github.irgaly.compose.vector.svg.SvgParser

@Suppress("RedundantSuspendModifier")
suspend fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
    val input = svg.byteInputStream()
    val imageVector = SvgParser(object : Logger {
        override fun debug(message: String) {
            println("debug: $message")
        }

        override fun info(message: String) {
            println("info: $message")
        }

        override fun warn(message: String, error: Exception?) {
            println("warn: $message | $error")
        }

        override fun error(message: String, error: Exception?) {
            println("error: $message | $error")
        }
    }).parse(
        input,
        name = "Icon"
    )
    val codes = ImageVectorGenerator().generate(
        imageVector = imageVector,
        destinationPackage = "io.github.irgaly.icons",
        receiverClasses = listOf("Icons", "AutoMirrored", "Filled"),
        extensionPackage = "io.github.irgaly.icons.automirrored.filled",
        hasAndroidPreview = true,
    )
    println("--- Output.kt")
    print(codes)
    println("---")
}

val svg = """
<svg
  xmlns="http://www.w3.org/2000/svg"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  height="24px"
  viewBox="0 -960 960 960"
  width="24px"
  fill="#FFe8eaed"
  stroke-width="10px">
  <g display="none" style="display:none" />
  <g visibility="hidden" />
  <g stroke="rgb(1 2 3 4)" />
  <g stroke="rgb(1 2 3 / 4)" />
  <g stroke="rgba(1%,2%,3%,4%)" />
  <g stroke="#01020304" />
  <g stroke="transparent" />
  <g stroke="red" />
  <path
    d="M280-200v-80h284q63 0 109.5-40T720-420q0-60-46.5-100T564-560H312l104 104-56 56-200-200 200-200 56 56-104 104h252q97 0 166.5 63T800-420q0 94-69.5 157T564-200H280Z" />
  <clipPath id="clip1">
      <rect x="15" y="15" rx="5" ry="5" width="40" height="40" />
  </clipPath>
  <circle id="c1" cx="25" cy="25" r="20"
          style="fill: #0000ff; clip-path: url(#clip1); " />
  <g id="used1">
      <circle cx="10" cy="10" r="10" />
      <circle cx="10" cy="10" r="20" />
  </g>
  <use href="#used1" />
  <use href="#c1" />
  <symbol id="rect" viewBox="0 0 100 100">
    <rect x="0" y="0" width="100" height="100" fill="red"/>
  </symbol>
  <use href="#rect" x="10" y="10" width="50" height="50"/>
  <defs>
    <linearGradient id="grad1">
        <stop offset="0" stop-color="#FF000050"/>
        <stop offset="1" stop-color="blue"/>
    </linearGradient>
    <radialGradient id="grad2" cx="0.2" cy="0.2" r="0.8">
        <stop offset="0" stop-color="#FF000050"/>
        <stop offset="1" stop-color="blue"/>
    </radialGradient>
  </defs>
  <rect width="100" height="100" fill="url(#grad1)"/>
  <rect width="100" height="100" fill="url(#grad2)"/>
  <style>
    .red {
      stroke: red;
      stroke-width: 1;
    }
  </style>
  <rect width="10" height="10" class="red"/>
  <rect width="10" height="10" fill="lightgreen"/>
</svg>
"""
