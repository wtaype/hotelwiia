package com.hotelwii.feature.actualizar.api

import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

/**
 * ⚡ ActualizarApi — Servicio de red ultra-ligero para consultar y descargar actualizaciones desde Cloudflare R2
 */
object ActualizarApi {
    const val MANIFEST_URL = "https://hotelwii.amorwii.com/version.json"

    /**
     * Obtiene el versionCode instalado en el dispositivo
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
     * Obtiene el versionName instalado en el dispositivo
     */
    fun getInstalledVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    /**
     * Consulta asíncrona no bloqueante al manifiesto en Cloudflare R2
     */
    suspend fun consultarVersionRemota(context: Context): Result<ModeloVersion?> = withContext(Dispatchers.IO) {
        val currentCode = getInstalledVersionCode(context)
        try {
            val urlString = "$MANIFEST_URL?_t=${System.currentTimeMillis()}"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 4000
            conn.readTimeout = 4000
            conn.requestMethod = "GET"
            conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
            conn.setRequestProperty("Pragma", "no-cache")

            if (conn.responseCode == 200) {
                val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonString)
                val serverCode = json.getInt("versionCode")

                if (serverCode > currentCode) {
                    val info = ModeloVersion(
                        versionCode = serverCode,
                        versionName = json.optString("versionName", "1.1.0"),
                        apkUrl = json.getString("apkUrl"),
                        releaseNotes = json.optString("releaseNotes", "Nuevas optimizaciones y mejoras para HotelWii."),
                        sha256 = if (json.has("sha256")) json.getString("sha256") else null,
                        isMandatory = json.optBoolean("isMandatory", false)
                    )
                    Result.success(info)
                } else {
                    Result.success(null)
                }
            } else {
                Result.failure(Exception("Error de conexión con el servidor (${conn.responseCode})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Descarga el archivo APK en stream a la caché interna de la app
     */
    suspend fun descargarApk(
        context: Context,
        apkUrl: String,
        onProgress: (Float, String) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val url = URL(apkUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 45000
            conn.connect()

            val fileLength = conn.contentLength.toLong()
            val apkFile = File(context.cacheDir, "update.apk")
            if (apkFile.exists()) {
                apkFile.delete()
            }

            conn.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    val data = ByteArray(8192)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        total += count.toLong()
                        if (fileLength > 0) {
                            val ratio = (total.toFloat() / fileLength.toFloat()).coerceIn(0f, 1f)
                            val mbDescargados = String.format(
                                Locale.US,
                                "%.1f / %.1f MB",
                                total / (1024f * 1024f),
                                fileLength / (1024f * 1024f)
                            )
                            onProgress(ratio, mbDescargados)
                        } else {
                            val mbDescargados = String.format(
                                Locale.US,
                                "%.1f MB",
                                total / (1024f * 1024f)
                            )
                            onProgress(0.5f, mbDescargados)
                        }
                        output.write(data, 0, count)
                    }
                    output.flush()
                }
            }
            Result.success(apkFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
