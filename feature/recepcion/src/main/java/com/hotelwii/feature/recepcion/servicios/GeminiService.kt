package com.hotelwii.feature.recepcion.servicios

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.hotelwii.core.kidev.Encriptar
import com.hotelwii.core.kidev.getSecure
import com.hotelwii.core.kidev.saveSecure
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * 🤖 GeminiService.kt — Servicio de Inteligencia Artificial Gemini Multimodal Vision OCR.
 * Basado en la arquitectura y lista de modelos de pancitawii:
 * 1. gemini-3.1-flash-lite
 * 2. gemini-3.1-flash
 * 3. gemini-2.5-flash
 * 4. gemini-2.0-flash
 * 5. gemini-1.5-flash
 */
class GeminiService(private val context: Context) {

    private val store = wiStore(context)

    /**
     * Obtiene la clave de API desencriptada desde WiStore
     */
    fun obtenerApiKeyDesencriptada(): String? {
        val keySeguridad = store.getSecure("mi_gemini_key", "").trim()
        if (keySeguridad.isNotBlank()) return keySeguridad

        val keyAlternativa = store.getSecure("MI_GEMINI_API", "").trim()
        if (keyAlternativa.isNotBlank()) return keyAlternativa

        val prefsLegacy = context.getSharedPreferences("wii_seguridad_prefs", Context.MODE_PRIVATE)
        val legacyVal = prefsLegacy.getString("MI_GEMINI_API", null)?.trim()
        if (!legacyVal.isNullOrBlank()) {
            return if (legacyVal.startsWith("ENC:")) Encriptar.descifrar(legacyVal) else legacyVal
        }

        return null
    }

    /**
     * Guarda una nueva clave cifrándola automáticamente en WiStore
     */
    fun guardarApiKeyCifrada(apiKeyLimpia: String) {
        store.saveSecure("mi_gemini_key", apiKeyLimpia.trim())
        store.saveSecure("MI_GEMINI_API", apiKeyLimpia.trim())
    }

    /**
     * 📸 Procesa una fotografía del DNI / Pasaporte con Gemini Vision API (Multimodal OCR)
     */
    suspend fun procesarFotoDocumentoConGemini(bitmap: Bitmap, tipoDocDefault: String = "dni"): DatosHuespedParseados = withContext(Dispatchers.IO) {
        val apiKey = obtenerApiKeyDesencriptada()
        if (apiKey.isNullOrBlank()) {
            return@withContext DatosHuespedParseados(
                numDoc = "",
                clienteNombre = "",
                nacionalidad = "Perú",
                tipoDoc = tipoDocDefault,
                fechaNacimiento = "",
                estadoCivil = "Soltero/a",
                esExitoso = false,
                mensajeInfo = "Clave de Gemini no configurada en Cuenta > Seguridad."
            )
        }

        try {
            // 1. Redimensionar y Comprimir Bitmap a JPEG en Base64
            val outputStream = ByteArrayOutputStream()
            val scaledBitmap = if (bitmap.width > 1280 || bitmap.height > 1280) {
                val ratio = 1280f / maxOf(bitmap.width, bitmap.height)
                Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
            } else {
                bitmap
            }
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val imageBytes = outputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            // 2. Construir Payload JSON conforme al estándar oficial de Google Generative AI
            val promptText = "Analiza minuciosamente esta fotografía de un documento de identidad de Perú o internacional (DNI, Pasaporte o Carnet de Extranjería). " +
                    "Extrae: " +
                    "1. 'numDoc': El número de documento (si es DNI peruano, los 8 dígitos numéricos del CUI sin guiones ni dígito verificador). " +
                    "2. 'clienteNombre': Nombres y apellidos completos del titular en orden 'PRIMER_APELLIDO SEGUNDO_APELLIDO PRENOMBRES'. " +
                    "3. 'nacionalidad': País de nacionalidad (ej: 'Perú', 'Colombia', 'España', etc.). " +
                    "4. 'tipoDoc': 'dni', 'pasaporte' o 'ce'. " +
                    "5. 'fechaNacimiento': Fecha de nacimiento en formato 'DD/MM/AAAA' (ej: '16/05/1997'). " +
                    "6. 'estadoCivil': Estado civil (ej: 'SOLTERO', 'CASADO', 'VIUDO', 'DIVORCIADO'). " +
                    "Responde ÚNICAMENTE un objeto JSON válido con los campos numDoc, clienteNombre, nacionalidad, tipoDoc, fechaNacimiento, estadoCivil sin bloques markdown."

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", promptText))
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            // 3. Cascada de modelos de pancitawii (gemini-3.1-flash-lite, gemini-3.1-flash, gemini-2.5-flash, gemini-2.0-flash, gemini-1.5-flash)
            val modelosDisponibles = listOf(
                "gemini-3.1-flash-lite",
                "gemini-3.1-flash",
                "gemini-2.5-flash",
                "gemini-2.0-flash",
                "gemini-1.5-flash",
                "gemini-2.0-flash-exp"
            )
            var ultimoError = ""

            for (modelo in modelosDisponibles) {
                try {
                    val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$modelo:generateContent?key=$apiKey")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                        doOutput = true
                        doInput = true
                        connectTimeout = 15000
                        readTimeout = 25000
                    }

                    OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                        writer.write(jsonBody.toString())
                        writer.flush()
                    }

                    val responseCode = conn.responseCode
                    if (responseCode == 200) {
                        val responseText = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8")).use { it.readText() }
                        val rootJson = JSONObject(responseText)
                        val candidates = rootJson.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val candidate = candidates.getJSONObject(0)
                            val content = candidate.optJSONObject("content")
                            val parts = content?.optJSONArray("parts")
                            val rawText = parts?.getJSONObject(0)?.optString("text") ?: ""

                            val cleanJsonStr = rawText
                                .replace("```json", "")
                                .replace("```", "")
                                .trim()

                            // Si viene contenido con llaves {...}
                            val jsonExtraido = if (cleanJsonStr.contains("{") && cleanJsonStr.contains("}")) {
                                val inicio = cleanJsonStr.indexOf("{")
                                val fin = cleanJsonStr.lastIndexOf("}") + 1
                                cleanJsonStr.substring(inicio, fin)
                            } else {
                                cleanJsonStr
                            }

                            val extractedJson = JSONObject(jsonExtraido)
                            var numDoc = extractedJson.optString("numDoc", "").trim()
                            val clienteNombre = extractedJson.optString("clienteNombre", "").trim()
                            val nacionalidad = extractedJson.optString("nacionalidad", "Perú").trim()
                            val tipoDoc = extractedJson.optString("tipoDoc", tipoDocDefault).trim()
                            val fechaNacimiento = extractedJson.optString("fechaNacimiento", "").trim()
                            val estadoCivil = extractedJson.optString("estadoCivil", "Soltero/a").trim()

                            // Si numDoc viene con guion (ej: 71779978-5), tomar solo los 8 dígitos principales
                            if (numDoc.contains("-")) {
                                numDoc = numDoc.substringBefore("-").trim()
                            }

                            if (numDoc.isNotEmpty() || clienteNombre.isNotEmpty()) {
                                return@withContext DatosHuespedParseados(
                                    numDoc = numDoc,
                                    clienteNombre = clienteNombre,
                                    nacionalidad = if (nacionalidad.isBlank()) "Perú" else nacionalidad,
                                    tipoDoc = if (tipoDoc.isBlank()) tipoDocDefault else tipoDoc,
                                    fechaNacimiento = fechaNacimiento,
                                    estadoCivil = estadoCivil,
                                    esExitoso = true,
                                    mensajeInfo = "¡DNI verificado por Gemini ($modelo)! $clienteNombre"
                                )
                            }
                        }
                    } else {
                        val errorStream = conn.errorStream
                        val errText = if (errorStream != null) {
                            BufferedReader(InputStreamReader(errorStream, "UTF-8")).use { it.readText() }
                        } else ""
                        ultimoError = "HTTP $responseCode ($modelo): $errText"
                    }
                } catch (e: Exception) {
                    ultimoError = "$modelo: ${e.localizedMessage ?: "Error de red"}"
                }
            }

            DatosHuespedParseados(
                numDoc = "",
                clienteNombre = "",
                nacionalidad = "Perú",
                tipoDoc = tipoDocDefault,
                fechaNacimiento = "",
                estadoCivil = "Soltero/a",
                esExitoso = false,
                mensajeInfo = "No se pudo procesar con Gemini ($ultimoError). Ingresa los datos manualmente."
            )
        } catch (e: Exception) {
            DatosHuespedParseados(
                numDoc = "",
                clienteNombre = "",
                nacionalidad = "Perú",
                tipoDoc = tipoDocDefault,
                fechaNacimiento = "",
                estadoCivil = "Soltero/a",
                esExitoso = false,
                mensajeInfo = "Error en escaneo: ${e.localizedMessage ?: "Error"}"
            )
        }
    }
}

data class DatosHuespedParseados(
    val numDoc: String = "",
    val clienteNombre: String = "",
    val nacionalidad: String = "Perú",
    val tipoDoc: String = "dni",
    val fechaNacimiento: String = "",
    val estadoCivil: String = "Soltero/a",
    val esExitoso: Boolean = false,
    val mensajeInfo: String = ""
)
