package io.mhdkhubbi.filo.ui.theme.components

import Gray500
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.R
import io.mhdkhubbi.filo.ui.theme.screens.InfoScreen

@Composable
fun TopBar(modifier: Modifier = Modifier,onNagivation:(NavKey)->Unit){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(
                painter = painterResource(R.drawable.filoapp),
                contentDescription = "App Icon",
                tint=Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )

            Text(modifier=Modifier.weight(1f),
                text= stringResource(R.string.app_name), fontSize = 25.sp, fontWeight = FontWeight.Medium)
            Icon(
                painter = painterResource(R.drawable.settings),
                contentDescription = "Settings",tint= Gray500,
                modifier = Modifier.size(25.dp).clickable{
                    onNagivation(InfoScreen)
                }
            )
        }
        Spacer(Modifier.height(30.dp))
        SearchField()
    }
}