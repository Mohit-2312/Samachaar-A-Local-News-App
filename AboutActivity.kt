package com.example.localnewstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AboutScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("About")
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,

    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
            ,
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
//            Text(
//                text = "Your App Name",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 32.sp,
                color = Color.Magenta,
            )
            Spacer(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
            Text(
                text = "Version 1.0",
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.padding(top = 32.dp, bottom = 32.dp))
            Text(
                text = "Get news from various sources, customised to your location"
                ,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
            )
            Text(
                text = "Creators:- \n\n\n" +
                        "Deshdeepak\n\n" +
                        "Nitesh Kumar\n\n" +
                        "Dheeraj Pandey\n\n" +
                        "Mohit Singh Tanwar\n\n"
                ,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
            )
        }

    }

}