package com.example.playground.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.playground.R
import com.example.playground.ui.theme.PlaygroundTheme
import com.example.playground.utils.ImageDownloader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageView(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    placeholderText: String = "Describe an image"
) {
    val cornerRadius = MaterialTheme.shapes.medium
    val loadingEmojis = remember { listOf("🔍", "🖼️", "✨", "🎨", "🧩") }
    var currentEmojiIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showContextMenu by remember { mutableStateOf(false) }
    
    // 新增：是否显示全屏图片查看器
    var showFullScreenImage by remember { mutableStateOf(false) }
    
    // Emoji animation effect
    LaunchedEffect(isLoading) {
        if (isLoading) {
            while (true) {
                delay(500) // 500ms delay between emoji changes
                currentEmojiIndex = (currentEmojiIndex + 1) % loadingEmojis.size
            }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.33f), // 4:3 aspect ratio
        shape = cornerRadius,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    // Loading state with animated emojis and progress indicator
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = loadingEmojis[currentEmojiIndex],
                            fontSize = 48.sp,
                            textAlign = TextAlign.Center
                        )
                        CircularProgressIndicator(
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
                imageUrl != null -> {
                    // Image loaded state with context menu
                    Box {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Generated image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.33f)
                                .combinedClickable(
                                    onClick = { 
                                        // 点击图片时显示全屏查看器
                                        showFullScreenImage = true 
                                    },
                                    onLongClick = { showContextMenu = true }
                                )
                        )
                        
                        // Long press menu
                        DropdownMenu(
                            expanded = showContextMenu,
                            onDismissRequest = { showContextMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Download Image") },
                                leadingIcon = { 
                                    Icon(
                                        imageVector = Icons.Outlined.Download,
                                        contentDescription = "Download"
                                    )
                                },
                                onClick = {
                                    showContextMenu = false
                                    
                                    // Check permissions and download image
                                    if (ImageDownloader.checkStoragePermission(context)) {
                                        coroutineScope.launch {
                                            ImageDownloader.downloadImage(context, imageUrl)
                                        }
                                    } else {
                                        // Request permission if not granted
                                        Toast.makeText(
                                            context,
                                            "Storage permission required to save images",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        
                                        // Try to request permission (if context is Activity)
                                        val activity = context as? android.app.Activity
                                        activity?.let {
                                            ImageDownloader.requestStoragePermission(it)
                                        }
                                    }
                                }
                            )
                        }
                    }
                    
                    // 全屏图片查看器
                    if (showFullScreenImage) {
                        Dialog(
                            onDismissRequest = { showFullScreenImage = false },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                                usePlatformDefaultWidth = false // 使对话框全屏
                            )
                        ) {
                            FullScreenImageViewer(
                                imageUrl = imageUrl,
                                onDismiss = { showFullScreenImage = false }
                            )
                        }
                    }
                }
                else -> {
                    // Placeholder state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.33f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = placeholderText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageViewPlaceholderPreview() {
    PlaygroundTheme {
        ImageView(
            imageUrl = null,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageViewLoadingPreview() {
    PlaygroundTheme {
        ImageView(
            imageUrl = null,
            isLoading = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageViewLoadedPreview() {
    PlaygroundTheme {
        // Local image for preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Sample image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.33f)
                )
            }
        }
    }
} 