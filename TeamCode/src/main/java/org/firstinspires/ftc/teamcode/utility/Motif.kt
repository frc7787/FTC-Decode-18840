package org.firstinspires.ftc.teamcode.utility

import org.firstinspires.ftc.teamcode.utility.ArtifactColour.*

enum class ArtifactColour {
    GREEN,
    PURPLE;

    fun opposite(): ArtifactColour {
        return when (this) {
            GREEN -> PURPLE
            PURPLE -> GREEN
        }
    }
}

private interface Motif {
    fun first(): ArtifactColour
    fun second(): ArtifactColour
    fun third(): ArtifactColour
}

class GreenPurplePurple: Motif {
    override fun first(): ArtifactColour  = GREEN
    override fun second(): ArtifactColour = PURPLE
    override fun third(): ArtifactColour  = PURPLE
}

class PurpleGreenPurple: Motif {
    override fun first(): ArtifactColour  = PURPLE
    override fun second(): ArtifactColour = GREEN
    override fun third(): ArtifactColour  = PURPLE
}

class PurplePurpleGreen: Motif {
    override fun first(): ArtifactColour  = PURPLE
    override fun second(): ArtifactColour = PURPLE
    override fun third(): ArtifactColour  = GREEN
}