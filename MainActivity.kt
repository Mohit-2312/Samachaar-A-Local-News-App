package com.example.localnewstracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.example.localnewstracker.appsettings.AppSettings
import com.example.localnewstracker.appsettings.NewsSource
import com.example.localnewstracker.db.NewsItemDao
import com.example.localnewstracker.db.NewsItemDatabase
import com.example.localnewstracker.db.NewsItemEntity
import com.example.localnewstracker.newsapi.NewsApi
import com.example.localnewstracker.ui.theme.LocalNewsTrackerTheme
import com.example.localnewstracker.worldnewsapi.WorldNewsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.IsoCountryCode


val newsApi = NewsApi.create()
val worldNewsApi = WorldNewsApi.create()
private var locationLoaded by mutableStateOf(false)


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var newsItemDao: NewsItemDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        val  database = NewsItemDatabase.getDatabase(applicationContext)
        newsItemDao = database.newsItemDao()

        setContent {
            LocalNewsTrackerTheme {
                if(locationLoaded) {
                    LocalNewsApp(newsItemDao)
                } else {
                    Text("Fetching Location")
                }
            }
        }
        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

    }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation(fusedLocationClient, geocoder)
            }
        }
    override fun onStart() {
        super.onStart()
//        getLocation(fusedLocationClient, geocoder)
    }
}
@SuppressLint("MissingPermission")
fun getLocation(fusedLocationClient: FusedLocationProviderClient, geocoder: Geocoder) {
    println("Get Location")
    try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                println(location)
                location?.let {
                    LocationInfo.locationString = "${location.latitude},${location.longitude}"
                    println(LocationInfo.locationString)
                    var address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        ?.firstOrNull()
                    address?.let {
                        LocationInfo.countryCode = address.countryCode
                        LocationInfo.countryName = address.countryName
                        LocationInfo.state = address.adminArea
                        LocationInfo.city = address.locality
                        locationLoaded = true
                    }
                    println(address)
                }
            }
            .addOnFailureListener {
                println(it)
                println("Error")
            }
    } catch (e: SecurityException) {
        println("Location permission denied")
    } catch (e: Exception) {
        println("Error getting location: ${e.message}")
    }
}

private fun isInternetConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    if (connectivityManager != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null) {
                return networkInfo.isConnected &&
                        (networkInfo.type == ConnectivityManager.TYPE_WIFI ||
                                networkInfo.type == ConnectivityManager.TYPE_MOBILE ||
                                networkInfo.type == ConnectivityManager.TYPE_ETHERNET)
            }
        }
    }
    return false
}


val navigationItems = listOf(
    NavigationItem("country", "By Country"),
    NavigationItem("state", "By State"),
    NavigationItem("city", "By City"),
    NavigationItem("location", "By Location"),
    NavigationItem("settings", "Settings", Icons.Default.Settings),
    NavigationItem("about", "About", Icons.Default.Info),
)



@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalNewsApp(newsItemDao: NewsItemDao) {
    var newsItems by remember { mutableStateOf<List<NewsItem>>(emptyList())  }
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val appSettings = context.dataStore.data.collectAsState(initial = AppSettings()).value
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(0.7f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        println("Menu closed")
                        scope.launch {
                            drawerState.close()
                            scrollState.scrollToItem(0)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                    Text("Menu")
                }
                Spacer(modifier = Modifier.padding(2.dp))
                navigationItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = item.title)
                        },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch { drawerState.close() }
                            when (item.id) {
                                "country", "state", "city", "location" -> {
                                    println("Opened ${item.title}")
                                }

                                "settings" -> {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            SettingsActivity::class.java
                                        )
                                    )
                                    selectedItemIndex = 0
                                }

                                "about" -> {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            AboutActivity::class.java
                                        )
                                    )
                                    selectedItemIndex = 0
                                }
                            }
                        },
                        icon = {
                            if (item.icon != null) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            println("Menu opened")
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name)
                        )
                    },
                )
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    scope.launch {
                        val navigationItem = navigationItems[selectedItemIndex]
                        newsItems = getNewsItems(context, appSettings, newsItemDao, navigationItem.id, LocationInfo)
                    }
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                val navigationItem = navigationItems[selectedItemIndex]
                GlobalScope.launch {
                    newsItems = getNewsItems(context, appSettings, newsItemDao, navigationItem.id, LocationInfo)
                }
                val region = when (navigationItem.id) {
                    "country" -> LocationInfo.countryName
                    "state" -> LocationInfo.state
                    "city" -> LocationInfo.city
                    "location" -> LocationInfo.locationString
                    else -> ""
                }
                Text(
                    text = region ?: navigationItem.title,
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    textAlign = TextAlign.Center,
                )
                NewsColumn(newsItems)
            }
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


suspend fun getNewsItems(context: Context, appSettings: AppSettings, newsItemDao: NewsItemDao, locTypeId: String, locationInfo: LocationInfo) : List<NewsItem>{
    if (! isInternetConnected(context))  {
        withContext(Dispatchers.Main) {
            showToast(context, "Loading offline news")
        }
        try {
            return loadNewsItems(newsItemDao, locTypeId)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToast(context, "Offline Loading Failed")
            }
            return emptyList()
        }
    }
    val newsApiArticles = if (NewsSource.NewsApi in appSettings.sources){
        try {
            when (locTypeId) {
                "country" -> {
                    newsApi.getByCountry(locationInfo.countryCode)
                }
                "state" -> {
                    newsApi.getByQuery(locationInfo.state, appSettings.domains)
                }
                "city" -> {
                    newsApi.getByQuery(locationInfo.city, appSettings.domains)
                }
                else -> emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    } else {
        emptyList()
    }
    val worldNewsApiArticle = if(NewsSource.WorldNewsApi in appSettings.sources) {
        try {
            when (locTypeId) {
                "country" -> {
                    worldNewsApi.getByCountry(locationInfo.countryCode)
                }
                "state" -> {
                    worldNewsApi.getByQuery(locationInfo.state, locationInfo.countryCode)
                }
                "city" -> {
                    worldNewsApi.getByQuery(locationInfo.city, locationInfo.countryCode)
                }
                "location" -> {
                    worldNewsApi.getByLocation(locationInfo.locationString, locationInfo.countryCode)
                }
                else -> emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    } else {
        emptyList()
    }
    val newsItems = newsApiArticles.map { it.toNewsItem() } + worldNewsApiArticle.map { it.toNewsItem() }
    try {
        saveNewsItems(newsItemDao, locTypeId, newsItems)
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            showToast(context, "Saving news failed")
        }
    }
    return  newsItems
}

suspend fun saveNewsItems(newsItemDao: NewsItemDao, locType: String, newsItems: List<NewsItem>) {
    val newsItemsArticles = newsItems.map { NewsItemEntity.fromNewsItem(locType, it) }
    newsItemDao.insertNewsItems(newsItemsArticles)
}

suspend fun loadNewsItems(newsItemDao: NewsItemDao, locType: String): List<NewsItem> {
    return newsItemDao.getNewsItems(locType).map {  it.getNewsItem() }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsColumn(newsItems: List<NewsItem>) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, bottom = 8.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(newsItems) {newsItem ->
            val cardColor = if (newsItem.source == 0) {
                MaterialTheme.colorScheme.tertiaryContainer }
            else {
                MaterialTheme.colorScheme.surfaceTint
            }
//            PlainTooltipBox(tooltip = {Text(newsItem.description?:"No description")}) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(200.dp)
                    ,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp,
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor,
                    ),
                    onClick = {uriHandler.openUri(newsItem.url)},

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = newsItem.title,
                            textAlign = TextAlign.Center,
                        )
                        Divider()
                        if (newsItem.urlToImage != null) {
                            AsyncImage(
                                model = newsItem.urlToImage,
                                contentDescription = newsItem.description,
                            )
                        } else {
                            Text(newsItem.description?:"")
                        }
                    }

                }
//            }
        }
    }

}

