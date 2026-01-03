package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mhdkhubbi.filo.R

@Composable
fun FolderComose() {

    Row(
        modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.folder),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(50.dp)

        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Download", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Row() {
                Text("1 Item.")
                Text(" 1 KB")
            }
        }

        Icon(
            painter = painterResource(R.drawable.dots),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp)
        )


    }
}