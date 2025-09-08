package org.firstinspires.ftc.teamcode.utility

import org.firstinspires.ftc.teamcode.utility.ArtifactColour.*

enum class ArtifactColour {
    GREEN,
    PURPLE
}

class ObeliskPattern private constructor(
    val first: ArtifactColour,
    val second: ArtifactColour,
    val third: ArtifactColour
) {
    val greenPurplePurple = ObeliskPattern(GREEN, PURPLE, PURPLE)
    val purpleGreenPurple = ObeliskPattern(PURPLE, GREEN, PURPLE)
    val purplePurpleGreen = ObeliskPattern(PURPLE, PURPLE, GREEN)
}