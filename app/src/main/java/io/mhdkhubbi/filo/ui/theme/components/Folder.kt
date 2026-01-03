package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
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
import io.mhdkhubbi.filo.ui.theme.Gray500

@Composable
fun FolderComose() {

    Row(
        modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.folder),
            contentDescription = null,
            tint = Color(0xFFFFCA28),
            modifier = Modifier
                .size(52.dp)

        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom,
            ) {
            Text("Download", fontSize = 15.sp, fontWeight = FontWeight.SemiBold,lineHeight = 15.sp )
            Row(Modifier.padding(top = 4.dp)){
                Text("1 Item.",fontSize=10.sp,color=Gray500,lineHeight = 10.sp)
                Text(" 1 KB",fontSize=10.sp,color=Gray500,lineHeight = 10.sp)
            }
        }

        Icon(
            painter = painterResource(R.drawable.dots),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )


    }
}