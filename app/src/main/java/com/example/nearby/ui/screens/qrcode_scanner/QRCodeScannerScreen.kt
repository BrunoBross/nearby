package com.example.nearby.ui.screens.qrcode_scanner

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.nearby.MainActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE

@Composable
fun QRCodeScannerScreen(modifier: Modifier = Modifier, onCompletedScan: (String) -> Unit) {

    val context = LocalContext.current

    val scanOptions = ScanOptions()
        .setDesiredBarcodeFormats(QR_CODE)
        .setPrompt("Leia o QRCode do cupom")
        .setCameraId(0)
        .setBeepEnabled(false)
        .setOrientationLocked(false)
        .setBarcodeImageEnabled(true)

    val barcodeLauncher = rememberLauncherForActivityResult(
        ScanContract()
    ) { result ->
        onCompletedScan(result.contents.orEmpty())
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        RequestPermission()
    ) { isGranted ->
        if (!isGranted) RequestPermission() else barcodeLauncher.launch(
            scanOptions
        )
    }

    fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                CAMERA
            ) == PERMISSION_GRANTED
        ) {
            barcodeLauncher.launch(scanOptions)
        } else if (shouldShowRequestPermissionRationale(
                context as MainActivity,
                CAMERA
            )
        ) {
            Toast.makeText(
                context,
                "Necessário permitir o acesso à câmera para continuar.",
                LENGTH_SHORT
            ).show()
        } else {
            cameraPermissionLauncher.launch(CAMERA)
        }
    }

    LaunchedEffect(true) {
        checkCameraPermission()
    }

    Column(modifier = modifier.fillMaxSize()) { }
}