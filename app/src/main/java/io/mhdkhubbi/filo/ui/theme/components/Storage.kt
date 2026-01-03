package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mhdkhubbi.filo.ui.theme.Gray100
import io.mhdkhubbi.filo.ui.theme.Gray500

@Composable
fun StorageInfo() {

    Column(
        modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {  Box(
        modifier = Modifier.border(
            width=1.dp,
            color = Gray100,
            shape = RoundedCornerShape(12.dp)
        ).padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomCircularProgressIndicator()
            Spacer(Modifier.width(15.dp))
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Internal Storage", color = Gray500)
                Spacer(Modifier.height(8.dp))
                Text("85 GB of 128 GB Used", fontWeight = FontWeight.SemiBold)
            }

        }
    }



    }
}
@Composable
fun CustomCircularProgressIndicator(){
    Box(
        modifier = Modifier

            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator({ 0.6f }, strokeWidth = 10.dp, modifier = Modifier.size(70.dp))
        Text("66%", modifier = Modifier.padding(20.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }

}
@Composable
@Preview
fun StorageInfoPreview() {
    StorageInfo()
}