package com.donglab.crashviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donglab.crashviewer.ui.theme.CrashViewerTheme
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

private val crashTests = listOf(
    CrashTest("NullPointerException", "Trigger a null pointer exception") {
        val nullString: String? = null
        nullString!!.length
    },
    CrashTest("ArrayIndexOutOfBounds", "Access array with invalid index") {
        val array = arrayOf(1, 2, 3)
        array[10]
    },
    CrashTest("ArithmeticException", "Division by zero error") {
        val result = 10 / 0
    },
    CrashTest("ClassCastException", "Invalid type casting") {
        val obj: Any = "String"
        obj as Int
    },
    CrashTest("StackOverflowError", "Infinite recursive call") {
        fun recursiveFunction(): Unit = recursiveFunction()
        recursiveFunction()
    },
    CrashTest("OutOfMemoryError", "Allocate excessive memory") {
        val list = mutableListOf<ByteArray>()
        while (true) {
            list.add(ByteArray(1024 * 1024 * 10)) // 10MB per iteration
        }
    },
    CrashTest("Background Thread Crash", "Crash on a background thread") {
        CoroutineScope(Dispatchers.Default).launch {
            delay(100)
            throw RuntimeException("Background thread crash!")
        }
    },
    CrashTest("Custom Exception", "Throw a custom exception with detailed message") {
        throw CustomCrashException(
            "This is a custom crash with detailed information",
            errorCode = "ERR_001",
            userId = "user123"
        )
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashTestScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crash Viewer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = crashTests,
                key = { it.title },
                contentType = { "CrashTestCard" }
            ) { crashTest ->
                CrashTestCard(crashTest)
            }
        }
    }
}

@Composable
fun CrashTestCard(crashTest: CrashTest) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = crashTest.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = crashTest.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = crashTest.action,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
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
