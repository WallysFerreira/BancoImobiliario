@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cartaoimobiliario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cartaoimobiliario.ui.theme.CartaoImobiliarioTheme

val cores = listOf(Color.Black, Color.White, Color.Blue, Color.Yellow, Color.Red)

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
    cor: Color,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
    ) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Jogador $index") },
            trailingIcon = {
                Button(
                    onClick = { /* Escolher cor */ },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(cor),
                ) {
                    Text("")
                }
           },
            modifier = Modifier.width(330.dp),
        )
        IconButton(
            onClick = {},
            modifier = Modifier
                .padding(4.dp)
        ) {
            Icon(Icons.Rounded.QrCode, contentDescription = null)
        }
    }
}

@Composable
fun InserirJogadores(
    modifier: Modifier = Modifier
) {
    var qntJogadores by remember { mutableStateOf(2) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..qntJogadores) {
            JogadorForm(index = i, modifier = Modifier.padding(bottom = 8.dp), cor = cores[i - 1])
        }

        if (qntJogadores < 5) {
            IconButton(
                onClick = {
                    qntJogadores++
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        }
    }
}

@Composable
fun CartaoImobiliarioApp() {
    CartaoImobiliarioTheme {
        InserirJogadores()
    }
}

@Preview(showBackground = true)
@Composable
fun JogadorFormPreview() {
    JogadorForm(index = 1, cor = cores[1])
}

@Preview(showBackground = true)
@Composable
fun InserirJogadoresPreview() {
    InserirJogadores()
}