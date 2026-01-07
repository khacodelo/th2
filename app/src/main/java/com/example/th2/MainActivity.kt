package com.example.th2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class UedPhoto(
    val imageRes: Int,
    val title: String,
    val extra: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme()
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UedGalleryApp(groupName = "Nhóm 13") // <-- đổi thành Nhóm X của bạn
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UedGalleryApp(groupName: String) {

    // Danh sách >= 5 ảnh
    val photos = remember {
        listOf(
            UedPhoto(
                R.drawable.ued1,
                "Cổng Trường UED",
                "Vị trí: 459 Tôn Đức Thắng, Đà Nẵng"
            ),
            UedPhoto(
                R.drawable.ued2,
                "Tòa nhà Hành chính",
                "Thông tin: Khu làm việc – tiếp nhận sinh viên"
            ),
            UedPhoto(
                R.drawable.ued3,
                "Thư viện UED",
                "Thông tin: Không gian học tập & tài liệu"
            ),
            UedPhoto(
                R.drawable.ued4,
                "Giảng đường",
                "Thông tin: Khu giảng dạy chính"
            ),
            UedPhoto(
                R.drawable.ued5,
                "Hoạt động sinh viên",
                "Thông tin: CLB – Sự kiện – Ngoại khóa"
            )
        )
    }


    var index by remember { mutableStateOf(0) }

    fun next() {
        index = (index + 1) % photos.size
    }

    fun prev() {
        index = if (index - 1 < 0) photos.size - 1 else index - 1
    }

    val current = photos[index]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("UED Gallery $groupName")
                }
            )
        }
    ) { padding ->
        // Responsive: tablet/landscape tự co giãn
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val isTabletWide = maxWidth >= 700.dp

            if (isTabletWide) {
                // Tablet / màn rộng: chia 2 cột
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PhotoArea(
                        modifier = Modifier.weight(1.2f).fillMaxHeight(),
                        titleOverlay = "UED Gallery $groupName",
                        photo = current,
                        onSwipeLeft = { next() },
                        onSwipeRight = { prev() }
                    )

                    InfoAndControls(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        title = current.title,
                        extra = current.extra,
                        onPrev = { prev() },
                        onNext = { next() }
                    )
                }
            } else {
                // Phone / màn nhỏ: xếp dọc
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PhotoArea(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        titleOverlay = "UED Gallery $groupName",
                        photo = current,
                        onSwipeLeft = { next() },
                        onSwipeRight = { prev() }
                    )

                    InfoCard(
                        title = current.title,
                        extra = current.extra
                    )

                    ControlsRow(
                        onPrev = { prev() },
                        onNext = { next() }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoArea(
    modifier: Modifier,
    titleOverlay: String,
    photo: UedPhoto,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Optional: vuốt để chuyển ảnh
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { /* nothing */ },
                        onHorizontalDrag = { _, dragAmount ->
                            // dragAmount > 0: kéo sang phải -> previous
                            // dragAmount < 0: kéo sang trái -> next
                            if (dragAmount > 18) onSwipeRight()
                            else if (dragAmount < -18) onSwipeLeft()
                        }
                    )
                }
        ) {
            Image(
                painter = painterResource(id = photo.imageRes),
                contentDescription = photo.title,
                modifier = Modifier.fillMaxSize(),
                // Chọn Crop để không méo + phủ khung đẹp
                // Nếu bạn muốn "lọt hết ảnh" thì đổi sang ContentScale.Fit
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // Tiêu đề app nằm phía trên hình ảnh (đúng yêu cầu đề)
            Surface(
                color = Color.Black.copy(alpha = 0.45f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = titleOverlay,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, extra: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = extra, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ControlsRow(onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onPrev,
            modifier = Modifier.weight(1f)
        ) {
            Text("Previous")
        }
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f)
        ) {
            Text("Next")
        }
    }
}

@Composable
private fun InfoAndControls(
    modifier: Modifier,
    title: String,
    extra: String,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoCard(title = title, extra = extra)
        Spacer(modifier = Modifier.weight(1f))
        ControlsRow(onPrev = onPrev, onNext = onNext)
    }
}
