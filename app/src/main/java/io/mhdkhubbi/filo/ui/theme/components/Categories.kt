package io.mhdkhubbi.filo.ui.theme.components

import Gray100
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.R
import io.mhdkhubbi.filo.domain.MediaType
import io.mhdkhubbi.filo.ui.theme.screens.FileScreen

@Composable
fun Categories(onNavigation:(NavKey)->Unit,
     load:(MediaType)->Unit
    ,modifier: Modifier = Modifier) {
    Column(
        Modifier
            .padding(start = 15.dp,)
            .fillMaxSize()
    )
    {
        Text(
            text = "Categories",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
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
                    Gray100,
                    shape = RoundedCornerShape(12.dp)
                ).clickable {
                    onNavigation(FileScreen("media_images_root"))

                },
                contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter=painterResource(R.drawable.images), contentDescription = "photos",
                        modifier = Modifier.size(52.dp),tint=Color.Unspecified,

                    )
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = "Photos",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    )
                }
            }

            Box( Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                width = 1.dp,
                Gray100,
                shape = RoundedCornerShape(12.dp)
            ).clickable {
                onNavigation(FileScreen("media_videos_root"))
            }, contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painterResource(R.drawable.videos), contentDescription = "videos",
                        modifier = Modifier
                            .size(52.dp),tint=Color.Unspecified,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = "Videos",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    )
                }
            }



            Box( Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                width = 1.dp,
                Gray100,
                shape = RoundedCornerShape(12.dp)
            ).clickable {onNavigation(FileScreen("media_music_root"))}, contentAlignment = Alignment.Center)
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painterResource(R.drawable.music), contentDescription = "Music",
                        modifier = Modifier
                            .size(52.dp),tint=Color.Unspecified,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = "Music",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    )
                }
            }

            Box( Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                width = 1.dp,
                color = Gray100,
                shape = RoundedCornerShape(12.dp)
            ).clickable {onNavigation(FileScreen("media_document_root"))}, contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painterResource(R.drawable.documents), contentDescription = "Documents",
                        modifier = Modifier
                            .size(52.dp),tint=Color.Unspecified,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = "Documents",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    )
                }
            }



            Box( Modifier.padding(20.dp).size(100.dp).weight(1f).border(
                width = 1.dp,
                color = Gray100,
                shape = RoundedCornerShape(12.dp)
            ).clickable {onNavigation(FileScreen("media_download_root"))}, contentAlignment = Alignment.Center)
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painterResource(R.drawable.downloads), contentDescription = "Downloads",
                        modifier = Modifier
                            .size(52.dp),tint=Color.Unspecified,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Downloads",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    )
                }
            }
            Spacer(Modifier.width(15.dp).weight(1f))


        }
    }

}


