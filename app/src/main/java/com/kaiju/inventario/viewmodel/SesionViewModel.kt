
package com.kaiju.inventario.viewmodel

import androidx.lifecycle.ViewModel
import com.kaiju.inventario.model.Usuario
import com.kaiju.inventario.repository.DatosDePrueba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SesionViewModel : ViewModel() {

    private val _usuarios = MutableStateFlow(
        DatosDePrueba.usuarios
    )

    val usuarios = _usuarios.asStateFlow()

    private val _usuarioActivo =
        MutableStateFlow<Usuario?>(null)

    val usuarioActivo = _usuarioActivo.asStateFlow()

    private val _errorLogin =
        MutableStateFlow<String?>(null)

    val errorLogin = _errorLogin.asStateFlow()

    fun iniciarSesion(idUsuario: String?): Boolean {

        if (idUsuario.isNullOrBlank()) {
            _errorLogin.value = "Debe seleccionar un usuario"
            return false
        }

        val usuario = _usuarios.value.find {
            it.id == idUsuario
        }

        if (usuario == null) {
            _errorLogin.value = "Usuario no válido"
            return false
        }

        _usuarioActivo.value = usuario
        _errorLogin.value = null

        return true
    }

    fun cerrarSesion() {
        _usuarioActivo.value = null
        _errorLogin.value = null
    }
}
