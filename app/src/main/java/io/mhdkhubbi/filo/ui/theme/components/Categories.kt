package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.VideoLabel
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mhdkhubbi.filo.ui.theme.Gray100

@Composable
fun Categories(modifier: Modifier = Modifier) {


    Column(
        Modifier
            .padding(start = 10.dp, top = 15.dp)
            .fillMaxSize()
    )
    {
        Text(

            text = "Categories",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
        )
        FlowRow(
            Modifier
                .padding(top = 15.dp)
                .fillMaxWidth(),
            maxItemsInEachRow = 2,


        ) {

            Box(
                Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                    width = 1.dp,
                    color = Gray100,
                    shape = RoundedCornerShape(12.dp)
                ).clickable {},
                contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Photo, contentDescription = "photos",
                        modifier = Modifier
                            .size(52.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = "Photos",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                    )
                }
            }

            Box( Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                width = 1.dp,
                color = Gray100,
                shape = RoundedCornerShape(12.dp)
            ).clickable {}, contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.VideoLabel, contentDescription = "photos",
                        modifier = Modifier
                            .size(52.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = "Videos",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                    )
                }
            }



            Box( Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                width = 1.dp,
                color = Gray100,
                shape = RoundedCornerShape(12.dp)
            ).clickable {}, contentAlignment = Alignment.Center)
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AudioFile, contentDescription = "photos",
                        modifier = Modifier
                            .size(52.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = "Music",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                    )
                }
            }

            Box( Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                width = 1.dp,
                color = Gray100,
                shape = RoundedCornerShape(12.dp)
            ).clickable {}, contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FilePresent, contentDescription = "photos",
                        modifier = Modifier
                            .size(52.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = "Documents",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                    )
                }
            }



            Box( Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                width = 1.dp,
                color = Gray100,
                shape = RoundedCornerShape(12.dp)
            ).clickable {}, contentAlignment = Alignment.Center)
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Download, contentDescription = "photos",
                        modifier = Modifier
                            .size(52.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Downloads",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                    )
                }
            }


        }
    }

}


@Preview
@Composable
private fun CategoriesPreView() {
    Categories()
}