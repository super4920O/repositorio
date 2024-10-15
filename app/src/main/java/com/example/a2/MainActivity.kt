package com.example.a2

import android.os.Bundle
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import com.example.a2.ui.theme._2Theme
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _2Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var currentSongTitle by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreenContent(navController, currentSongTitle)
        }
        composable("music_screen") {
            MusicScreen(
                navController,
                currentSongTitle,
                onSongChange = { title -> currentSongTitle = title }
            )
        }
        composable("webview_screen") {
            WebViewScreen(url = "https://www.google.com")
        }
        composable("game_screen") {
            CatchTheFallingObjectsGame()
        }
    }
}

@Composable
fun MainScreenContent(navController: NavController, currentSongTitle: String) {
    val context = LocalContext.current
    val mediaPlayer1 = remember { MediaPlayer.create(context, R.raw.song1) }
    val mediaPlayer2 = remember { MediaPlayer.create(context, R.raw.song2) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer1.release()
            mediaPlayer2.release()
        }
    }

    var currentDateTime by remember { mutableStateOf("") }
    var currentWeather by remember { mutableStateOf("Loading...") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = getCurrentDateTime()
            coroutineScope.launch {
                currentWeather = getCurrentWeather()
            }
            delay(1000)
        }
    }

    TvLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDateTime,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = currentWeather,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }

        item {
            Image(
                painter = painterResource(id = R.drawable.carro),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        item {
            // Horizontal scroll container for the images
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(6) { index ->
                        Box(
                            modifier = Modifier
                                .width(175.dp)
                                .height(110.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    navController.navigate("game_screen")
                                }
                        ) {
                            Image(
                                painter = painterResource(id = when (index) {
                                    0 -> R.drawable.ajustes
                                    1 -> R.drawable.apple
                                    2 -> R.drawable.tv
                                    3 -> R.drawable.twitch
                                    4 -> R.drawable.youtube
                                    else -> R.drawable.juego // Provide a default image if needed
                                }),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.musica),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Musica",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("music_screen")
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}


@Composable
fun MusicScreen(navController: NavController, currentSongTitle: String, onSongChange: (String) -> Unit) {
    val songs = listOf(
        Song(R.drawable.fuerzaregida, "Fuerza Regida", R.raw.song1),
        Song(R.drawable.badbuny, "Bad Bunny", R.raw.song2),
        Song(R.drawable.pesopluma, "Lagunas Peso pluma", R.raw.song3),
        Song(R.drawable.fuerzaregida, "Fuerza Regida", R.raw.song4),
        Song(R.drawable.adriel, "Miami Vibe", R.raw.song5),
        Song(R.drawable.marcaregistrada, "Marca registrada", R.raw.song6)
    )

    var isPlaying by remember { mutableStateOf<Song?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Biblioteca de Musica",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(songs) { song ->
                MusicItem(song, isPlaying?.title == song.title, onClick = {
                    if (isPlaying?.title == song.title) {
                        isPlaying?.mediaPlayer?.stop()
                        isPlaying = null
                        onSongChange("")
                    } else {
                        isPlaying?.mediaPlayer?.stop()
                        val mediaPlayer = MediaPlayer.create(context, song.resId)
                        mediaPlayer.start()
                        isPlaying = song.copy(mediaPlayer = mediaPlayer)
                        onSongChange(song.title)
                    }
                })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MusicItem(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isPlaying) Color.DarkGray else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = song.imageResId),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = song.title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WebViewScreen(url: String) {
    val state = rememberWebViewState(url = url)
    WebView(
        state = state,
        modifier = Modifier.fillMaxSize()
    )
}

data class Song(
    val imageResId: Int,
    val title: String,
    val resId: Int,
    val mediaPlayer: MediaPlayer? = null
)

suspend fun getCurrentWeather(): String {
    delay(1000) // Simula un retraso de red
    return "22°C, Nublado"
}



data class WeatherResponse(
    val weather: List<WeatherData>
)

data class WeatherData(
    val main: String
)

fun getCurrentDateTime(): String {
    val currentDateTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(currentDateTime)
}

@Composable
fun CatchTheFallingObjectsGame() {
    var score by remember { mutableStateOf(0) }
    var catcherX by remember { mutableStateOf(0f) }
    var fallingObjectY by remember { mutableStateOf(0f) }
    var fallingObjectX by remember { mutableStateOf(0f) }
    var gameOver by remember { mutableStateOf(false) }

    var fallingSpeed by remember { mutableStateOf(5f) } // Initial speed

    val scope = rememberCoroutineScope()
    val catcherSize = 100.dp
    val objectSize = 50.dp

    val density = LocalDensity.current

    // Convert Dp to Px
    val catcherSizePx = with(density) { catcherSize.toPx() }
    val objectSizePx = with(density) { objectSize.toPx() }
    val screenWidth = with(density) { 400.dp.toPx() }

    // Restart the game
    LaunchedEffect(gameOver) {
        if (gameOver) {
            delay(2000)
            score = 0
            catcherX = 0f
            fallingObjectY = 0f
            fallingObjectX = (0..screenWidth.toInt()).random().toFloat()
            gameOver = false
            // Reset falling speed
            fallingSpeed = 5f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
            .pointerInput(Unit) {
                detectTapGestures {
                    if (!gameOver) {
                        scope.launch {
                            catcherX = it.x
                        }
                    }
                }
            }
    ) {
        // Catcher
        Box(
            modifier = Modifier
                .size(catcherSize)
                .offset(x = with(density) { catcherX.toDp() }, y = 500.dp)
                .background(Color.Green)
        )

        // Falling Object
        Box(
            modifier = Modifier
                .size(objectSize)
                .offset(x = with(density) { fallingObjectX.toDp() }, y = with(density) { fallingObjectY.toDp() })
                .background(Color.Red)
        )

        // Game Over text
        if (gameOver) {
            Text(
                text = "Game Over\nScore: $score",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    // Game logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (!gameOver) {
                fallingObjectY += fallingSpeed

                if (fallingObjectY > with(density) { 500.dp.toPx() }) {
                    if (fallingObjectX in catcherX..(catcherX + catcherSizePx)) {
                        score += 1
                        // Increase falling speed by 50% for each point
                        fallingSpeed *= 1.2f
                    } else {
                        gameOver = true
                    }
                    fallingObjectY = 0f
                    fallingObjectX = (0..screenWidth.toInt()).random().toFloat()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    _2Theme {
        MainScreen()
    }
}