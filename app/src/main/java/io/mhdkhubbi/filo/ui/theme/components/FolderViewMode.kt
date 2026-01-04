package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.mhdkhubbi.filo.ui.theme.Gray400

@Composable
fun FloderViewMode(){

    Row(Modifier.fillMaxWidth()){

        Icon(
            Icons.AutoMirrored.Filled.Sort,
            contentDescription = "Search",
            tint = Gray400,
            modifier = Modifier.padding(start = 15.dp).size(24.dp)
        )
         Spacer(Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.ViewList,
            contentDescription = "Search",
            tint = Gray400,
            modifier = Modifier.padding(end = 15.dp).size(24.dp)
        )
    }

}