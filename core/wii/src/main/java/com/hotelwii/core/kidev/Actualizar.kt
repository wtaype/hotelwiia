// Actualizar.kt — Motor ultra-ligero y automático de actualizaciones OTA desde Cloudflare R2
package com.hotelwii.core.kidev

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Modelo de datos de la versión remota
 */
data class WiVersionInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val releaseNotes: String,
    val isMandatory: Boolean
)

/**
 * Actualizar — Motor 100% automático de actualizaciones OTA
 */
object Actualizar {
    const val MANIFEST_URL = "https://HotelWii.amorwii.com/version.json"

    /**
     * Obtiene el versionCode real instalado en el celular (ej. 2, 3, 4...)
     */
    fun getInstalledVersionCode(context: Context): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: Exception) {
            1
        }
    }

    /**
     * Obtiene el versionName real instalado (ej. "2.0.0", "3.0.0")
     */
    fun getInstalledVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "2.0.0"
        } catch (e: Exception) {
            "2.0.0"
        }
    }

    /**
     * Comprobación matemática infalible: Detecta actualización si serverVersionCode > installedVersionCode
     */
    suspend fun checkUpdate(context: Context): WiVersionInfo? = withContext(Dispatchers.IO) {
        val currentCode = getInstalledVersionCode(context)
        try {
            val url = URL(MANIFEST_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "GET"

            if (conn.responseCode == 200) {
                val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonString)
                val serverCode = json.getInt("versionCode")

                // Si el código en la nube es mayor (ej. 3 > 2), hay nueva versión!
                if (serverCode > currentCode) {
                    return@withContext WiVersionInfo(
                        versionCode = serverCode,
                        versionName = json.optString("versionName", "3.0.0"),
                        apkUrl = json.getString("apkUrl"),
                        releaseNotes = json.optString("releaseNotes", "Nuevas mejoras y optimizaciones."),
                        isMandatory = json.optBoolean("isMandatory", false)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    /**
     * Descarga el archivo APK en segundo plano notificando el progreso (0.0f a 1.0f)
     */
    suspend fun downloadApk(
        context: Context,
        apkUrl: String,
        onProgress: (Float) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            val url = URL(apkUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 10000
            conn.readTimeout = 30000
            conn.connect()

            val fileLength = conn.contentLength
            val apkFile = File(context.cacheDir, "update.apk")
            if (apkFile.exists()) apkFile.delete()

            conn.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        total += count.toLong()
                        if (fileLength > 0) {
                            onProgress(total.toFloat() / fileLength.toFloat())
                        }
                        output.write(data, 0, count)
                    }
                    output.flush()
                }
            }
            return@withContext apkFile
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    /**
     * Lanza el instalador NATIVO DIRECTO de Android
     */
    fun installApk(context: Context, apkFile: File) {
        try {
            val apkUri = getUriForFile(context, apkFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            val packageManager = context.packageManager
            val installerApps = listOf(
                "com.google.android.packageinstaller",
                "com.android.packageinstaller"
            )

            for (pkg in installerApps) {
                try {
                    packageManager.getPackageInfo(pkg, 0)
                    intent.setPackage(pkg)
                    break
                } catch (e: PackageManager.NameNotFoundException) {
                }
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUriForFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
    }
}

/**
 * 🚀 WiUpdateDialog — Popup Glassmorphic elegante
 */
@Composable
fun WiUpdateDialog(
    show: Boolean,
    versionInfo: WiVersionInfo,
    isDownloading: Boolean,
    downloadProgress: Float,
    onConfirmUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    WiDialog(
        show = show,
        title = "🚀 Actualización Disponible v${versionInfo.versionName}",
        text = versionInfo.releaseNotes,
        onConfirm = onConfirmUpdate,
        onDismiss = onDismiss,
        confirmText = if (isDownloading) "Descargando ${(downloadProgress * 100).toInt()}%..." else "Actualizar Ahora",
        dismissText = if (versionInfo.isMandatory) "" else "Más tarde"
    )
}
