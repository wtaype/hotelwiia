package com.hotelwii.feature.recepcion.servicios

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.hotelwii.core.kidev.Encriptar
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
 * 🤖 GeminiService.kt — Servicio de Inteligencia Artificial Gemini Vision OCR.
 * Obtiene la clave cifrada `"MI_GEMINI_API"` de SharedPreferences y procesa la fotografía del DNI en tiempo real.
 */
class GeminiService(private val context: Context) {

    private val prefs = context.getSharedPreferences("wii_seguridad_prefs", Context.MODE_PRIVATE)

    /**
     * Obtiene la clave de API desencriptada desde SharedPreferences ("MI_GEMINI_API")
     */
    fun obtenerApiKeyDesencriptada(): String? {
        val claveGuardada = prefs.getString("MI_GEMINI_API", null) ?: return null
        return if (claveGuardada.startsWith("ENC:")) {
            Encriptar.descifrar(claveGuardada)
        } else {
            claveGuardada
        }
    }

    /**
     * Guarda una nueva clave cifrándola automáticamente con Encriptar.cifrar()
     */
    fun guardarApiKeyCifrada(apiKeyLimpia: String) {
        val claveCifrada = Encriptar.cifrar(apiKeyLimpia)
        prefs.edit().putString("MI_GEMINI_API", claveCifrada).apply()
    }

    /**
     * 📸 Procesa una fotografía de la cámara con Gemini Vision API (Multimodal OCR)
     */
    suspend fun procesarFotoDocumentoConGemini(bitmap: Bitmap, tipoDocDefault: String = "dni"): DatosHuespedParseados = withContext(Dispatchers.IO) {
        val apiKey = obtenerApiKeyDesencriptada()
        if (apiKey.isNullOrBlank()) {
            return@withContext DatosHuespedParseados(
                numDoc = "",
                clienteNombre = "",
                nacionalidad = "Perú",
                tipoDoc = tipoDocDefault,
                esExitoso = false,
                mensajeInfo = "Clave MI_GEMINI_API no configurada. Ingresa los datos manualmente."
            )
        }

        try {
            // 1. Convertir Bitmap a JPEG comprimido en Base64
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val imageBytes = outputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            // 2. Construir Payload JSON para Gemini 1.5 Flash API
            val promptText = "Analiza esta fotografía de un documento de identidad (DNI peruano, Pasaporte o Carnet de Extranjería). " +
                    "Extrae los datos y responde ÚNICAMENTE con un objeto JSON válido con este formato: " +
                    "{\"numDoc\": \"...\", \"clienteNombre\": \"...\", \"nacionalidad\": \"Perú\", \"tipoDoc\": \"dni\"}. " +
                    "No incluyas explicaciones, saludos ni formato Markdown, solo el objeto JSON."

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            // Parte 1: Texto
                            put(JSONObject().put("text", promptText))
                            // Parte 2: Imagen Base64
                            put(JSONObject().apply {
                                put("inline_data", JSONObject().apply {
                                    put("mime_type", "image/jpeg")
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

            // 3. Petición HTTP POST a Gemini API
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                doOutput = true
                doInput = true
                connectTimeout = 15000
                readTimeout = 20000
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

                    // Limpiar markdown si Gemini devuelve ```json ... ```
                    val cleanJsonStr = rawText
                        .replace("```json", "")
                        .replace("```", "")
                        .trim()

                    val extractedJson = JSONObject(cleanJsonStr)
                    val numDoc = extractedJson.optString("numDoc", "").trim()
                    val clienteNombre = extractedJson.optString("clienteNombre", "").trim()
                    val nacionalidad = extractedJson.optString("nacionalidad", "Perú").trim()
                    val tipoDoc = extractedJson.optString("tipoDoc", tipoDocDefault).trim()

                    if (numDoc.isNotEmpty() || clienteNombre.isNotEmpty()) {
                        DatosHuespedParseados(
                            numDoc = numDoc,
                            clienteNombre = clienteNombre,
                            nacionalidad = if (nacionalidad.isBlank()) "Perú" else nacionalidad,
                            tipoDoc = if (tipoDoc.isBlank()) tipoDocDefault else tipoDoc,
                            esExitoso = true,
                            mensajeInfo = "Documento verificado por Gemini IA OCR: $clienteNombre"
                        )
                    } else {
                        DatosHuespedParseados(
                            numDoc = "",
                            clienteNombre = "",
                            nacionalidad = "Perú",
                            tipoDoc = tipoDocDefault,
                            esExitoso = false,
                            mensajeInfo = "No se detectó texto claro en la imagen. Ingresa los datos manualmente."
                        )
                    }
                } else {
                    DatosHuespedParseados(
                        numDoc = "",
                        clienteNombre = "",
                        nacionalidad = "Perú",
                        tipoDoc = tipoDocDefault,
                        esExitoso = false,
                        mensajeInfo = "Gemini no devolvió respuesta. Ingresa los datos manualmente."
                    )
                }
            } else {
                DatosHuespedParseados(
                    numDoc = "",
                    clienteNombre = "",
                    nacionalidad = "Perú",
                    tipoDoc = tipoDocDefault,
                    esExitoso = false,
                    mensajeInfo = "Error al conectar con Gemini ($responseCode). Ingresa los datos manualmente."
                )
            }
        } catch (e: Exception) {
            DatosHuespedParseados(
                numDoc = "",
                clienteNombre = "",
                nacionalidad = "Perú",
                tipoDoc = tipoDocDefault,
                esExitoso = false,
                mensajeInfo = "No se pudo procesar la foto (${e.localizedMessage ?: "Error"}). Ingresa los datos manualmente."
            )
        }
    }
}

data class DatosHuespedParseados(
    val numDoc: String,
    val clienteNombre: String,
    val nacionalidad: String,
    val tipoDoc: String,
    val esExitoso: Boolean,
    val mensajeInfo: String
)
