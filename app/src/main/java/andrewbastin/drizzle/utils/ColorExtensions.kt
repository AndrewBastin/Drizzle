package andrewbastin.drizzle.utils

import androidx.compose.ui.graphics.Color

val Color.luminosity: Float
    get() = ((0.2126F * this.red) + (0.7152F * this.green) + (0.0722F * this.blue))