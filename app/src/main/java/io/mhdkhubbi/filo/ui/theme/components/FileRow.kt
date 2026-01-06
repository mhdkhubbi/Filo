package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mhdkhubbi.filo.ui.theme.Gray400

@Composable
fun FileRow(nameFile: String) {

    Row(Modifier.fillMaxWidth()) {

        Text(
            nameFile,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
        )
        Spacer(Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.ViewList,
            contentDescription = "Search",
            tint = Gray400,
            modifier = Modifier
                .padding(end = 15.dp)
                .size(24.dp)
        )
    }

}