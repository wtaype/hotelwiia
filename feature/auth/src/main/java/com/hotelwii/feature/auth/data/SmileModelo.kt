import com.hotelwii.core.Wii
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 😊 SmileModelo.kt — Modelo de dominio 1:1 de la entidad public.smiles Supabase para HotelWii.
 * Conectado con la configuración central Wii.kt (Wii.dtema).
 */
@Serializable
data class SmileModelo(
    val id: String,
    val usuario: String,
    val email: String,
    val nombre: String = "",
    val apellidos: String = "",
    val avatar: String? = null,
    val bio: String? = null,
    val estado: String = "activo",
    val activo: Boolean = true,
    val plan: String = "free",
    val rol: String = "todos",
    val segmento: String = "negocios",
    val tema: String = Wii.dtema,


    val terminos: Boolean = true,
    @SerialName("terminos_fecha") val terminosFecha: String? = null,
    val verificado: Boolean = false,
    @SerialName("registrado_por") val registradoPor: String = "correo",
    val creado: String? = null,
    val actualizado: String? = null
)
