package com.donglab.crashviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donglab.crashviewer.ui.theme.CrashViewerTheme
import com.donglab.crashviewer.ui.theme.GeminiBlue
import com.donglab.crashviewer.ui.theme.GeminiPurple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrashViewerTheme {
                CrashTestScreen()
            }
        }
    }
}

data class CrashTest(
    val title: String,
    val description: String,
    val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashTestScreen() {
    val crashTests = listOf(
        CrashTest(
            title = "NullPointerException",
            description = "Trigger a null pointer exception"
        ) {
            val nullString: String? = null
            nullString!!.length
        },
        CrashTest(
            title = "ArrayIndexOutOfBounds",
            description = "Access array with invalid index"
        ) {
            val array = arrayOf(1, 2, 3)
            array[10]
        },
        CrashTest(
            title = "ArithmeticException",
            description = "Division by zero error"
        ) {
            val result = 10 / 0
        },
        CrashTest(
            title = "ClassCastException",
            description = "Invalid type casting"
        ) {
            val obj: Any = "String"
            obj as Int
        },
        CrashTest(
            title = "StackOverflowError",
            description = "Infinite recursive call"
        ) {
            fun recursiveFunction(): Unit = recursiveFunction()
            recursiveFunction()
        },
        CrashTest(
            title = "OutOfMemoryError",
            description = "Allocate excessive memory"
        ) {
            val list = mutableListOf<ByteArray>()
            while (true) {
                list.add(ByteArray(1024 * 1024 * 10)) // 10MB per iteration
            }
        },
        CrashTest(
            title = "Background Thread Crash",
            description = "Crash on a background thread"
        ) {
            CoroutineScope(Dispatchers.Default).launch {
                delay(100)
                throw RuntimeException("Background thread crash!")
            }
        },
        CrashTest(
            title = "Custom Exception",
            description = "Throw a custom exception with detailed message"
        ) {
            throw CustomCrashException(
                "This is a custom crash with detailed information",
                errorCode = "ERR_001",
                userId = "user123"
            )
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crash Viewer",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HeaderCard()
            }

            items(crashTests) { crashTest ->
                CrashTestCard(crashTest)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(GeminiBlue, GeminiPurple)
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Welcome to Crash Viewer",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Test various crash scenarios with Gemini-inspired design",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CrashTestCard(crashTest: CrashTest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = crashTest.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = crashTest.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = crashTest.action,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Trigger Crash",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

class CustomCrashException(
    message: String,
    val errorCode: String,
    val userId: String
) : Exception(message) {
    override fun toString(): String {
        return "CustomCrashException(errorCode='$errorCode', userId='$userId'): $message"
    }
}

@Preview(showBackground = true)
@Composable
fun CrashTestScreenPreview() {
    CrashViewerTheme {
        CrashTestScreen()
    }
}