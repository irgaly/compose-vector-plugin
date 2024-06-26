package io.github.irgaly.compose.vector

import io.github.irgaly.compose.vector.svg.SvgParser

suspend fun main(args: Array<String>) {
    val input = svg.byteInputStream()
    val imageVector = SvgParser().parse(input)
    val codes = ImageVectorGenerator().generate(
        imageVector,
        "io.github.irgaly.icons",
    )
    println("--- Output.kt")
    print(codes)
    println("---")
}

val svg = """
<svg
  xmlns="http://www.w3.org/2000/svg"
  height="24px"
  viewBox="0 -960 960 960"
  width="24px"
  fill="#e8eaed">
  <g>
  </g>
  <path
    d="M280-200v-80h284q63 0 109.5-40T720-420q0-60-46.5-100T564-560H312l104 104-56 56-200-200 200-200 56 56-104 104h252q97 0 166.5 63T800-420q0 94-69.5 157T564-200H280Z"/>
  <path
    d="M100"/>
</svg>
"""
