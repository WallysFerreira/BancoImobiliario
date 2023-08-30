@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cartaoimobiliario

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.example.cartaoimobiliario.databinding.CameraBinding
import com.example.cartaoimobiliario.ui.theme.CartaoImobiliarioTheme
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.Objects
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val cores = listOf(Color.Black, Color.White, Color.Blue, Color.Yellow, Color.Red)
lateinit var viewBinding: CameraBinding
lateinit var cameraExecutor: ExecutorService
lateinit var cameraController: LifecycleCameraController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultLauncher.launch(mutableListOf(android.Manifest.permission.CAMERA).toTypedArray())

        setContent {
            CartaoImobiliarioApp()
        }
    }

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key == android.Manifest.permission.CAMERA && it.value == false)
                    permissionGranted = false
            }

            if (!permissionGranted) {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission request accepted", Toast.LENGTH_SHORT).show()
            }
        }
}

private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
) {
    imageProxy.image?.let {image ->
        val inputImage =
            InputImage.fromMediaImage(
                image,
                imageProxy.imageInfo.rotationDegrees
            )

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodeList ->
                val barcode = barcodeList.getOrNull(0)

                barcode?.rawValue?.let { value ->
                    Log.d("QrCode", "${value}")
                }
            }
            .addOnFailureListener {
                Log.e("QrCode", it.message.orEmpty())
            }
            .addOnCompleteListener {
                imageProxy.image?.close()
                imageProxy.close()
            }
    }
}

@Composable
fun TelaCamera(
    modifier: Modifier = Modifier,
    irParaTelaPassada: () -> Unit,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val scanner = BarcodeScanning.getClient(options)
    val analysis = ImageAnalysis.Builder()
        .build()
    analysis.setAnalyzer(
        Executors.newSingleThreadExecutor(),
        {imageProxy ->
            processImageProxy(scanner, imageProxy)
        }
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {


    AndroidView(
        modifier = modifier,
        factory = { context ->
        val previewView = PreviewView(context).apply {
            this.scaleType = scaleType
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, analysis
                )
            } catch (exc: Exception) {
                Log.e("QrCode", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))

        previewView
    })
        Button(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick =  irParaTelaPassada
        ) {
            Text("Voltar")
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
    irParaProximaTela: () -> Unit,
    modifier: Modifier = Modifier
) {
    var qntJogadores by remember { mutableStateOf(2) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
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

        ElevatedButton(
            onClick = irParaProximaTela,
            modifier = modifier
                .width(200.dp)
                .height(110.dp)
                .padding(top = 40.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(2.dp)
        ) {
            Text(
                text = "Começar",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun CartaoImobiliarioApp() {
    var etapa by remember { mutableStateOf(1) }

    CartaoImobiliarioTheme {
        when (etapa) {
            1 -> InserirJogadores(irParaProximaTela = { etapa++ })
            2 -> TelaCamera(irParaTelaPassada = { etapa-- })
        }
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
    InserirJogadores(irParaProximaTela = {})
}

@Preview(showBackground = true)
@Composable
fun TelaPrincipalPreview() {
}