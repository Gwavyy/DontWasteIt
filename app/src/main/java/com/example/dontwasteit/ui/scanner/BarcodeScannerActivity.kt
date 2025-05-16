package com.example.dontwasteit.ui.scanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dontwasteit.databinding.ActivityBarcodeScannerBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

//Activity para escanear codigos de barras con el ML Kit de Google.
class BarcodeScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeScannerBinding

    private val requestCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                scanBarcode(imageBitmap)
            } else {
                Toast.makeText(this, "No se pudo capturar la imagen", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            finish()
        }
    }
    //Escanea el codigo de barras en la imagen capturada
    private fun scanBarcode(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                // Si hay algun codigo escaneado, lo devuelve a la actividad anterior
                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes[0].rawValue
                    val intent = Intent().apply {
                        putExtra("barcode", barcode)
                    }
                    setResult(RESULT_OK, intent)
                } else {
                    Toast.makeText(this, "No se detectó ningún código de barras", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_CANCELED)
                }
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al escanear: ${it.message}", Toast.LENGTH_SHORT).show()
                setResult(RESULT_CANCELED)
                finish()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Ejecutado al crear la actividad
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar y solicitar permiso de camara si es necesario
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1002)
        } else {
            lanzarCamara()
        }
    }

    //Lanza la camara para capturar una imagen.
    private fun lanzarCamara() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            requestCameraLauncher.launch(takePictureIntent)
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    //Se lanza al aceptar el permiso
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1002 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            lanzarCamara()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
