package com.hotelwii.feature.auth.api

import com.hotelwii.core.data.supabase.HotelWiiSupabase
import com.hotelwii.feature.auth.data.SmileModelo
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

data class ResultadoAuthGoogle(
    val smile: SmileModelo? = null,
    val esNuevoUsuario: Boolean = false,
    val email: String = "",
    val userId: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val avatar: String = ""
)

/**
 * 🔑 AuthApi.kt — Servicio remoto de Autenticación Supabase Auth + public.smiles para HotelWii.
 */
object AuthApi {
    private val client get() = HotelWiiSupabase.client

    private fun cleanMail(s: String) = s.replace("\"", "").replace("'", "").trim().lowercase().replace(" ", "")
    private fun cleanUser(s: String) = s.replace("\"", "").replace("'", "").trim().lowercase().replace(" ", "").replace(Regex("[^a-z0-9_]"), "")
    private fun cleanName(s: String) = s.replace("\"", "").replace("'", "").trim().replace(Regex("\\s+"), " ")

    private fun splitName(fullName: String, givenName: String, familyName: String): Pair<String, String> {
        val g = cleanName(givenName)
        val f = cleanName(familyName)
        if (g.isNotBlank()) return Pair(g, f)

        val full = cleanName(fullName)
        if (full.isBlank()) return Pair("Usuario", "")

        val partes = full.split(" ")
        return if (partes.size > 1) {
            Pair(partes.first(), partes.drop(1).joinToString(" "))
        } else {
            Pair(full, "")
        }
    }

    suspend fun existeUsuario(username: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val u = cleanUser(username)
            if (u.isBlank()) return@withContext false
            val params = buildJsonObject { put("username_buscado", u) }
            client.postgrest.rpc("existe_usuario", params).decodeAs<Boolean>()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun existeEmail(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val eStr = cleanMail(email)
            if (eStr.isBlank()) return@withContext false
            val params = buildJsonObject { put("email_buscado", eStr) }
            client.postgrest.rpc("existe_email", params).decodeAs<Boolean>()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun registrar(
        emailStr: String,
        passStr: String,
        usuarioStr: String,
        nombreStr: String,
        apellidosStr: String
    ): Result<SmileModelo> = withContext(Dispatchers.IO) {
        try {
            val mail = cleanMail(emailStr)
            val usr = cleanUser(usuarioStr)
            val nom = cleanName(nombreStr)
            val ape = cleanName(apellidosStr)

            // 1. Crear usuario en Supabase Auth
            try {
                client.auth.signUpWith(Email) {
                    email = mail
                    password = passStr
                }
            } catch (e: Exception) {
                return@withContext Result.failure(Exception(e.localizedMessage ?: "Error al registrar en Supabase Auth"))
            }

            var currentUserId = client.auth.currentUserOrNull()?.id
            if (currentUserId == null) {
                try {
                    client.auth.signInWith(Email) {
                        email = mail
                        password = passStr
                    }
                    currentUserId = client.auth.currentUserOrNull()?.id
                } catch (e: Exception) {
                    // Si requiere confirmación
                }
            }

            val finalUserId = currentUserId
                ?: return@withContext Result.failure(Exception("Cuenta creada. Confirma el correo para continuar."))

            val smile = SmileModelo(
                id = finalUserId,
                usuario = usr,
                email = mail,
                nombre = nom,
                apellidos = ape,
                registradoPor = "correo"
            )

            // 2. Insertar en public.smiles
            client.postgrest["smiles"].insert(smile)
            Result.success(smile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ingresar(
        emailOrUsername: String,
        passStr: String
    ): Result<SmileModelo> = withContext(Dispatchers.IO) {
        try {
            val input = cleanMail(emailOrUsername)
            val emailFinal = if (!input.contains("@")) {
                val usr = cleanUser(emailOrUsername)
                val params = buildJsonObject { put("username_buscado", usr) }
                client.postgrest.rpc("obtener_email_por_usuario", params).decodeAs<String>()
                    ?: return@withContext Result.failure(Exception("Usuario no encontrado"))
            } else {
                input
            }

            client.auth.signInWith(Email) {
                email = emailFinal
                password = passStr
            }

            val userId = client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.failure(Exception("Error al recuperar sesión"))

            val smile = client.postgrest["smiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingle<SmileModelo>()

            Result.success(smile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ingresarConGoogleIdToken(idTokenStr: String): Result<ResultadoAuthGoogle> = withContext(Dispatchers.IO) {
        try {
            client.auth.signInWith(IDToken) {
                idToken = idTokenStr
                provider = Google
            }

            val currentUser = client.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("Error al verificar credencial Google"))

            val userId = currentUser.id
            val emailVal = cleanMail(currentUser.email ?: "")

            val meta = currentUser.userMetadata
            val avatarVal = meta?.get("avatar_url")?.jsonPrimitive?.contentOrNull
                ?: meta?.get("picture")?.jsonPrimitive?.contentOrNull
                ?: ""

            val fullNameVal = meta?.get("full_name")?.jsonPrimitive?.contentOrNull
                ?: meta?.get("name")?.jsonPrimitive?.contentOrNull
                ?: ""
            val givenNameVal = meta?.get("given_name")?.jsonPrimitive?.contentOrNull ?: ""
            val familyNameVal = meta?.get("family_name")?.jsonPrimitive?.contentOrNull ?: ""

            val (nombreVal, apellidosVal) = splitName(fullNameVal, givenNameVal, familyNameVal)

            val smileExistente = try {
                client.postgrest["smiles"]
                    .select { filter { eq("id", userId) } }
                    .decodeSingleOrNull<SmileModelo>()
            } catch (e: Exception) {
                null
            }

            if (smileExistente != null) {
                Result.success(ResultadoAuthGoogle(smile = smileExistente, esNuevoUsuario = false))
            } else {
                Result.success(
                    ResultadoAuthGoogle(
                        smile = null,
                        esNuevoUsuario = true,
                        email = emailVal,
                        userId = userId,
                        nombre = nombreVal,
                        apellidos = apellidosVal,
                        avatar = avatarVal
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completarRegistroGoogle(
        userId: String,
        email: String,
        usuarioStr: String,
        nombreStr: String,
        apellidosStr: String,
        avatarStr: String
    ): Result<SmileModelo> = withContext(Dispatchers.IO) {
        try {
            val usr = cleanUser(usuarioStr)
            val nom = cleanName(nombreStr)
            val ape = cleanName(apellidosStr)

            if (existeUsuario(usr)) {
                return@withContext Result.failure(Exception("El usuario '@$usr' ya está ocupado"))
            }

            val smile = SmileModelo(
                id = userId,
                usuario = usr,
                email = email,
                nombre = nom,
                apellidos = ape,
                avatar = avatarStr.ifBlank { null },
                registradoPor = "google"
            )

            client.postgrest["smiles"].insert(smile)
            Result.success(smile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
