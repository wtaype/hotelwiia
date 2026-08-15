package com.hotelwii.feature.personal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 👥 ModeloPersonal.kt — DTO oficial para la entidad public.personal en Supabase.
 */
@Serializable
data class ModeloPersonal(
    @SerialName("id") val id: String? = null,
    @SerialName("userId") val smileId: String = "",
    @SerialName("empresa_id") val empresaId: String = "",
    
    // 5 Campos Principales
    @SerialName("nombre") val nombre: String = "",
    @SerialName("num_doc") val numDoc: String = "",
    @SerialName("rol") val rol: String = "recepcion", // 'admin', 'recepcion', 'limpieza', 'caja', 'seguridad'
    @SerialName("pin") val pin: String = "",          // PIN de 4 dígitos (Opcional / Random editable)
    @SerialName("celular") val celular: String = "",
    
    // Opcionales / Perfil
    @SerialName("tipo_doc") val tipoDoc: String = "dni",
    @SerialName("avatar") val avatar: String = "",
    @SerialName("genero") val genero: String = "M",
    @SerialName("fecha_nacimiento") val fechaNacimiento: String? = null,
    @SerialName("correo") val correo: String = "",
    @SerialName("direccion") val direccion: String = "",
    @SerialName("fecha_ingreso") val fechaIngreso: String? = null,
    
    // Estados Operativos
    @SerialName("activo") val activo: Boolean = true,
    @SerialName("en_descanso") val enDescanso: Boolean = false,
    @SerialName("en_vacaciones") val enVacaciones: Boolean = false,
    
    // Auditoría
    @SerialName("creado") val creado: String? = null,
    @SerialName("actualizado") val actualizado: String? = null
)
