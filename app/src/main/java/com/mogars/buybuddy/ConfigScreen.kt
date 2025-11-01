package com.mogars.buybuddy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ConfigScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
            .paddingFromBaseline(top = 35.dp)
    ) {
        //
        Text(stringResource(R.string.configuracion))
    }
}