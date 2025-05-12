package com.example.dontwasteit.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dontwasteit.R
import com.example.dontwasteit.data.database.provider.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NotisCaducidad(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val dao = DatabaseProvider.provideDatabase(context).productDao()
        val productos = dao.getAllOnce()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val hoy = LocalDate.now()

        val productosProntos = productos.filter {
            !it.consumido && try {
                val fecha = LocalDate.parse(it.fechaCaducidad, formatter)
                !fecha.isBefore(hoy) && fecha.minusDays(2) <= hoy
            } catch (_: Exception) {
                false
            }
        }

        if (productosProntos.isNotEmpty()) {
            mostrarNotificacion(productosProntos.size)
        }

        Result.success()
    }

    private fun mostrarNotificacion(cantidad: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permisoConcedido = NotificationManagerCompat.from(context).areNotificationsEnabled()
            val tienePermiso = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!permisoConcedido || !tienePermiso) {
                return
            }
        }

        val channelId = "caducidad_channel"
        val notificationId = 1

        val channel = NotificationChannel(
            channelId,
            "Avisos de caducidad",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        NotificationManagerCompat.from(context).createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Alerta de productos!")
            .setContentText("Tienes $cantidad producto(s) que caducan pronto")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
}
