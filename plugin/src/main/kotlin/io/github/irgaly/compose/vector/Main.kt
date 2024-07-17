package io.github.irgaly.compose.vector

import io.github.irgaly.compose.vector.svg.SvgParser

@Suppress("RedundantSuspendModifier")
suspend fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
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
  fill="#FFe8eaed"
  stroke-width="10px">
  <g display="none" style="display:none" />
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
  <circle cx="25" cy="25" r="20"
          style="fill: #0000ff; clip-path: url(#clip1); " />
</svg>
"""