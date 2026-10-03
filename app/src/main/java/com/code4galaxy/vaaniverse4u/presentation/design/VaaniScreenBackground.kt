package com.code4galaxy.vaaniverse4u.presentation.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.isSystemInDarkTheme
import com.code4galaxy.vaaniverse4u.R

/**
 * A low-contrast scenic layer shared by learner screens. The source artwork lives in
 * SVG source files under `assets/backgrounds`; equivalent VectorDrawables are used at runtime so no raster
 * decoding or network image loading is needed.
 */
enum class VaaniScene(@param:DrawableRes val drawable: Int) {
    Garden(R.drawable.vaani_garden_background),
    River(R.drawable.vaani_river_background),
    Twilight(R.drawable.vaani_twilight_background),
}

@Composable
fun VaaniScreenBackground(
    modifier: Modifier = Modifier,
    scene: VaaniScene = VaaniScene.Garden,
    content: @Composable () -> Unit,
) {
    val darkTheme = isSystemInDarkTheme()
    val backgroundScene = if (darkTheme && scene == VaaniScene.Garden) VaaniScene.Twilight else scene
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Image(
            painter = painterResource(backgroundScene.drawable),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (darkTheme) 0.62f else 0.24f),
            alignment = Alignment.BottomCenter,
            contentScale = ContentScale.Crop,
        )
        content()
    }
}
