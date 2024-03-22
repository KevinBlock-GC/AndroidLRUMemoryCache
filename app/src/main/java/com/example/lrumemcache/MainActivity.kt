package com.example.lrumemcache

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lrumemcache.ui.theme.LRUMemCacheTheme
import kotlin.math.pow
import kotlin.math.round


class MainActivity : ComponentActivity() {
    private val viewModel = MainActivityViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.state.collectAsState()
            LRUMemCacheTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(100.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Memory used: ${round(state.memoryPercentageUsed * 100.0) / 100.0}%"
                        )
                    }
                    Button(onClick = { viewModel.putSmallItem() }) {
                        Text(text = "Put Small Item (.001MB)")
                    }
                    Button(onClick = { viewModel.putMediumItem() }) {
                        Text(text = "Put Medium Item (.1MB)")
                    }
                    Button(onClick = { viewModel.putHeftyItem() }) {
                        Text(text = "Put Hefty Item (1.8MB)")
                    }
                    Button(onClick = { viewModel.putLargeItem() }) {
                        Text(text = "Put Large Item (3.8MB)")
                    }
                    Button(onClick = { viewModel.putExtraLargeItem() }) {
                        Text(text = "Put Extra Large Item (Overflow)")
                    }
                    Button(onClick = { viewModel.clearCache() }) {
                        Text(text = "Clear Cache")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Column {
                        state.allItems.forEach { (key, value) ->
                            Text("$key: ${value.sizeInBytes / 10.0.pow(6.0)} MB")
                        }
                    }
                }
            }
        }
    }
}