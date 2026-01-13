package io.mhdkhubbi.filo.ui.theme.components

import Gray500
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.ui.theme.screens.FileScreen


@Composable
fun StorageInfo(
    sizeStorage: Float,
    percentUsage: String,
    label: String,
    name: String, modifier: Modifier = Modifier,
    onNavigation: (NavKey) -> Unit = {}
) {

    Column(
        modifier = Modifier
            .padding(all = 15.dp)
            .clickable(onClick = {
                onNavigation(FileScreen("/storage/emulated/0"))
            }),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomCircularProgressIndicator(sizeStorage, label)
                Spacer(Modifier.width(15.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(name, color = Gray500)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        percentUsage,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }


    }
}

@Composable
fun CustomCircularProgressIndicator(
    sizeStorage: Float, label: String, modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background,RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            {sizeStorage},
            strokeWidth = 10.dp,
            modifier = Modifier.size(80.dp),
            trackColor = MaterialTheme.colorScheme.surfaceTint
        )
        Text(
            label,
            modifier = Modifier.padding(20.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

}
