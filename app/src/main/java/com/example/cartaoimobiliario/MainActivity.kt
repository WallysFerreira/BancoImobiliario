@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cartaoimobiliario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cartaoimobiliario.ui.theme.CartaoImobiliarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CartaoImobiliarioApp()
        }
    }
}

@Composable
fun JogadorForm(
    index: Int,
    modifier: Modifier = Modifier,
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Jogador $index") },
            trailingIcon = {
                Button(
                    onClick = { /* Escolher cor */ },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("")
                }
           },
        )
        IconButton(
            onClick = {},
        ) {
            Icon(Icons.Rounded.QrCode, contentDescription = null)
        }
    }
}

@Composable
fun InserirJogadores(
    qntJogadores: Int = 2,
    modifier: Modifier = Modifier
) {
    LazyColumn(

    ) {
        items(qntJogadores) { index ->
            JogadorForm(index = index + 1, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
fun CartaoImobiliarioApp() {
    CartaoImobiliarioTheme {
    }
}

@Preview(showBackground = true)
@Composable
fun JogadorFormPreview() {
    JogadorForm(1)
}

@Preview(showBackground = true)
@Composable
fun InserirJogadoresPreview() {
    InserirJogadores(2)
}