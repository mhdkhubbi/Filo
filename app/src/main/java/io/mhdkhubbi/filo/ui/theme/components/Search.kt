package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchField(
    values: String="",
    onValueChange: (String) -> Unit={}
) {
    var text by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row( modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF9CA3AF)
            )

            Spacer(Modifier.width(8.dp))

            BasicTextField(
                value = text,
                onValueChange = {text=it},
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            "Search folders or files",
                            color = Color(0xFF9CA3AF)
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Preview
@Composable
fun SearchPreview() {
    SearchField(
    )
}