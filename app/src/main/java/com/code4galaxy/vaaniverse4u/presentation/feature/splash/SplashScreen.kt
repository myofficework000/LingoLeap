package com.code4galaxy.vaaniverse4u.presentation.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.code4galaxy.vaaniverse4u.R

@Composable
fun SplashRoute(
    onNavigate: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->

            when (effect) {
                is SplashEffect.Navigate -> {
                    onNavigate(effect.route)
                }
            }
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.vaaniverse4u_logo),
            contentDescription = "VaaniVerse4U logo",
            modifier = Modifier.size(220.dp)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        CircularProgressIndicator(
            color = Color(0xFF007A3D)
        )
    }
}
