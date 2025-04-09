package com.example.localnewstracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.localnewstracker.appsettings.AppSettings
import com.example.localnewstracker.appsettings.AppSettingsSerializer
import com.example.localnewstracker.appsettings.NewsSource
import kotlinx.coroutines.launch

val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent  {
            val appSettings = dataStore.data.collectAsState(
                initial = AppSettings()
            ).value
            SettingsScreen(appSettings)
        }
    }

}

suspend fun setNewsSources(dataStore: DataStore<AppSettings>, newsSources: Set<NewsSource>) {
    dataStore.updateData {
        it.copy(
            sources = newsSources,
        )
    }
}
suspend fun setDomains(dataStore: DataStore<AppSettings>, domains: String) {
    dataStore.updateData {
        it.copy(
            domains = domains,
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(appSettings: AppSettings) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var domains by remember { mutableStateOf(appSettings.domains) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Settings")
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,

        ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text("Select Sources:", fontSize = 16.sp,
                modifier = Modifier.padding(
                    vertical = 16.dp,
                )
            )
            Column {
                NewsSource.entries.forEachIndexed{ index, newsSource ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = newsSource in appSettings.sources,
                            onCheckedChange = { selected ->
                                scope.launch {
                                    val sources = if (selected) {
                                        appSettings.sources.plusElement(newsSource)
                                    } else {
                                        appSettings.sources.minusElement(newsSource)
                                    }
                                    setNewsSources(context.dataStore, sources)
                                }
                            },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = newsSource.name)
                    }
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                TextField(
                    value = domains,
                    onValueChange = {
                        domains = it
                        scope.launch {
                           setDomains(context.dataStore, domains)
                        }
                    },
                    label = {
                        Text(text = "Enter Domains:")
                    },
                )

            }
        }

    }

}