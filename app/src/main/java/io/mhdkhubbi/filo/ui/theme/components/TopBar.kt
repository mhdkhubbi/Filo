package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mhdkhubbi.filo.R
import io.mhdkhubbi.filo.ui.theme.Gray500

@Composable
fun TopBar(){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(
                painter = painterResource(R.drawable.settings),
                contentDescription = "Search",
                tint = Gray500,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier=Modifier.width(10.dp))
            Text(modifier=Modifier.weight(1f),
                text="Filo", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                painter = painterResource(R.drawable.settings),
                contentDescription = "Search",
                tint = Gray500,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(30.dp))
        SearchField()
    }
}