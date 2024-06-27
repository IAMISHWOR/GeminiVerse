package com.example.geminiverse


import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.geminiverse.presentation.ChatUiEvent
import com.example.geminiverse.presentation.ChatViewModel
import com.example.geminiverse.ui.theme.GeminiVerseTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


val uriState = MutableStateFlow("")
class MainScreen : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiVerseTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "GeminiVerse",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            },
                            actions = {
                                    Box(modifier = Modifier
                                        .size(35.dp)
                                        .clip(CircleShape)
                                        .padding(end = 8.dp)
                                    ){
                                        Image(painter = painterResource(id = R.drawable.removebggemini),
                                            contentDescription = "img gemini", contentScale = ContentScale.Crop)
                                    }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFFe1f8ff)
                            )

                        )
                    },

                ) { PaddingValues ->
                    ChatScreen(PaddingValues)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    paddingValues: PaddingValues,
) {
    val viewmodel = viewModel<ChatViewModel>()
    val  chatState =  viewmodel.chatState.collectAsState().value

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uriState.update {
            uri.toString()
        }
    }
    val bitmap = getBitmap()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
            .background(Color(0xffe1f8ff))
        ,
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            reverseLayout = true
        ) {
            items(chatState.chatList){chat->
                if (chat.isFormUser){
                    UserChatItem(prompt = chat.prompt, bitmap = chat.bitmap)
                }
                else{
                    ModelChatItem(response = chat.prompt)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                bitmap?.let {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(bottom = 2.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentDescription = "picked image",
                        contentScale = ContentScale.Crop,
                        bitmap = it.asImageBitmap()
                    )
                }

                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            launcher.launch("image/*")
                        },
                    imageVector = Icons.Rounded.AddPhotoAlternate,
                    contentDescription = "Add Photo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(30.dp))
                ,
                value = chatState.prompt,
                onValueChange = {
                    viewmodel.onEvent(ChatUiEvent.updatePompt(it))
                },
                placeholder = {
                    Text(text = "Type a prompt")
                },
                )

            Spacer(modifier = Modifier.width(8.dp))


            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        viewmodel.onEvent(ChatUiEvent.sendPompt(chatState.prompt, bitmap))
                        uriState.update { "" }
                    },
                painter = painterResource(id = R.drawable.upload),
                contentDescription = "upload",
                tint = MaterialTheme.colorScheme.primary
            )

        }

    }

}

@Composable
fun UserChatItem(prompt: String, bitmap: Bitmap?) {

    Column(
        modifier = Modifier.padding(start = 150.dp, bottom = 16.dp)
    ) {
                bitmap?.let {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentDescription = "image",
                        contentScale = ContentScale.Crop,
                        bitmap = it.asImageBitmap()
                    )
                }
                Row {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(.8f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F4C75))
                            .padding(16.dp),
                        text = prompt,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .border(
                                BorderStroke(2.dp, DarkGray),
                                CircleShape
                            ), contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.human),
                            "human img"
                        )
                    }
                }
            }
        }
@Composable
fun ModelChatItem(response: String) {
    Row {
        Box(modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
        ){
            Image(painter = painterResource(id = R.drawable.gemini1),
                contentDescription = "img gemini", contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.width(1.dp))
        Column(
            modifier = Modifier.padding(end = 100.dp, bottom = 16.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF393E46))
                    .padding(16.dp),
                text = response,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }
    }
}
@Composable
private fun getBitmap(): Bitmap? {
    val uri = uriState.collectAsState().value

    val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .size(Size.ORIGINAL)
            .build()
    ).state

    if (imageState is AsyncImagePainter.State.Success) {
        return imageState.result.drawable.toBitmap()
    }

    return null
}
