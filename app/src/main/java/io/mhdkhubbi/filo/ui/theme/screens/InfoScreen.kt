package io.mhdkhubbi.filo.ui.theme.screens


import Gray100
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mhdkhubbi.filo.R

@Composable
fun InfoScreen() {

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        // verticalArrangement = Arrangement.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.filoapp), contentDescription = "App Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(
                    100.dp
                )
            )
            Text("Filoapp", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

        }
        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color =Gray100)
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Name :", fontSize = 15.sp,)
            Spacer(Modifier.width(55.dp))
            Text("Filoapp", fontSize = 15.sp)
        }
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color =Gray100)
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Version :", fontSize = 15.sp)
            Spacer(Modifier.width(45.dp))
            Text("1.0", fontSize = 15.sp)
        }
        HorizontalDivider(color =Gray100)
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Developer :", fontSize = 15.sp)
            Spacer(Modifier.width(30.dp))
            Text("mhdkhubbi", fontSize = 15.sp)
        }
        HorizontalDivider(color =Gray100)
        Spacer(Modifier.height(55.dp))
        Row(Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Built with curiosity, patience,and a lot ",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    buildAnnotatedString {
                        append("of love ")
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append("❤")
                        }
                        append(" this project is meant ")
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "for practice rather than ",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "production",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

        }


    }
}