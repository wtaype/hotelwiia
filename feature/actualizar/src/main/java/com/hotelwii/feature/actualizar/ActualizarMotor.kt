package com.hotelwii.feature.actualizar

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.Locale

/**
 * 📦 WiVersionInfo — Metadatos oficiales de versión desde Cloudflare R2
 */
data class WiVersionInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val sha256: String,
    val releaseNotes: String,
    val isMandatory: Boolean
)

/**
 * ⚡ ActualizarMotor — Motor singleton ultra-ligero y autónomo para actualizaciones OTA
 */
object ActualizarMotor {

    const val MANIFEST_URL = "https://hotelwii.amorwii.com/version.json"

    // Claves WiStore
    const val KEY_PERMISO_NOTIF = "permiso_notificaciones_ota"
    const val KEY_PERMISO_BG = "permiso_segundo_plano_ota"
    const val KEY_CERO_FRICCION = "actualizar_cero_friccion"
    const val KEY_AUTO_CHECK = "actualizar_auto_check"
    const val KEY_SOLO_WIFI = "actualizar_solo_wifi"
    const val KEY_LIMPIAR_CACHE = "actualizar_limpiar_cache"

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

    fun getInstalledVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    fun tienePermisoInstalar(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun abrirAjustesPermisoInstalar(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    /**
     * Consulta el manifiesto de versión en Cloudflare R2 sin caché
     */
    suspend fun checkUpdate(context: Context): Result<WiVersionInfo?> = withContext(Dispatchers.IO) {
        try {
            val currentCode = getInstalledVersionCode(context)
            val antiCacheUrl = "$MANIFEST_URL?_t=${System.currentTimeMillis()}"
            val url = URL(antiCacheUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "GET"
                setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
                setRequestProperty("Pragma", "no-cache")
                setRequestProperty("User-Agent", "HotelWii-OTA/${getInstalledVersionName(context)}")
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonString)
                val serverCode = json.getInt("versionCode")

                if (serverCode > currentCode) {
                    val info = WiVersionInfo(
                        versionCode = serverCode,
                        versionName = json.optString("versionName", "2.0.0"),
                        apkUrl = json.getString("apkUrl"),
                        sha256 = json.optString("sha256", ""),
                        releaseNotes = json.optString("releaseNotes", "Mejoras de rendimiento y estabilidad."),
                        isMandatory = json.optBoolean("isMandatory", false)
                    )
                    return@withContext Result.success(info)
                } else {
                    return@withContext Result.success(null)
                }
            } else {
                return@withContext Result.failure(Exception("HTTP ${conn.responseCode}: ${conn.responseMessage}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Descarga el APK con notificación de progreso y validación criptográfica SHA-256
     */
    suspend fun downloadApk(
        context: Context,
        info: WiVersionInfo,
        onProgress: (ratio: Float, texto: String) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val url = URL(info.apkUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 10000
                readTimeout = 30000
                connect()
            }

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(Exception("Error en servidor R2: HTTP ${conn.responseCode}"))
            }

            val totalBytes = conn.contentLength.toLong()
            val apkFile = File(context.cacheDir, "hotelwii_update.apk")
            if (apkFile.exists()) apkFile.delete()

            conn.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Long = 0
                    var count: Int

                    while (input.read(buffer).also { count = it } != -1) {
                        output.write(buffer, 0, count)
                        bytesRead += count

                        val ratio = if (totalBytes > 0) bytesRead.toFloat() / totalBytes.toFloat() else 0f
                        val mbActual = bytesRead / (1024f * 1024f)
                        val mbTotal = if (totalBytes > 0) totalBytes / (1024f * 1024f) else 0f
                        val porcentaje = (ratio * 100).toInt().coerceIn(0, 100)

                        val textoProgreso = if (totalBytes > 0) {
                            String.format(Locale.US, "%.1f / %.1f MB (%d%%)", mbActual, mbTotal, porcentaje)
                        } else {
                            String.format(Locale.US, "%.1f MB descargados", mbActual)
                        }

                        onProgress(ratio, textoProgreso)
                    }
                    output.flush()
                }
            }

            // Validar SHA-256 si la nube proporcionó el hash
            if (info.sha256.isNotBlank()) {
                val digest = MessageDigest.getInstance("SHA-256")
                FileInputStream(apkFile).use { fis ->
                    val buffer = ByteArray(8192)
                    var n: Int
                    while (fis.read(buffer).also { n = it } != -1) {
                        digest.update(buffer, 0, n)
                    }
                }
                val hashCalculado = digest.digest().joinToString("") { "%02x".format(it) }
                if (!hashCalculado.equals(info.sha256.trim(), ignoreCase = true)) {
                    apkFile.delete()
                    return@withContext Result.failure(Exception("Integridad SHA-256 no coincide."))
                }
            }

            return@withContext Result.success(apkFile)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Inicia la instalación oficial con PackageInstaller nativo sin diálogo "Open with"
     */
    fun installApk(context: Context, apkFile: File): Result<Unit> {
        return try {
            if (!apkFile.exists() || apkFile.length() == 0L) {
                return Result.failure(Exception("Archivo de actualización no encontrado."))
            }

            val apkUri = getUriForFile(context, apkFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            // Fijar el instalador nativo del sistema para evitar el selector molesto "Open with"
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

            // Otorgar permisos URI explícitos
            val resolved = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (res in resolved) {
                context.grantUriPermission(res.activityInfo.packageName, apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getUriForFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
    }
}
