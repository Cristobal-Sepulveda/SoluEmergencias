package com.example.soluemergencias.ui.crearcuenta

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.data.data_objects.domainObjects.PreDataUsuarioEnFirestore
import com.example.soluemergencias.databinding.FragmentCrearCuentaBinding
import com.example.soluemergencias.ui.base.BaseFragment
import com.example.soluemergencias.utils.Constants.REQUEST_TAKE_PHOTO
import com.example.soluemergencias.utils.NavigationCommand
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream

class CrearCuentaFragment: BaseFragment() {

    private var _binding: FragmentCrearCuentaBinding? = null
    override val _viewModel: CrearCuentaViewModel by inject()
    private var imageBitmap: Bitmap? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{

        _binding = FragmentCrearCuentaBinding.inflate(inflater, container, false)

        val perfiles = listOf("Dueño de casa","Asesora del Hogar")
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item,perfiles)
        _binding!!.editTextDataUsuarioRol.setAdapter(adapter)

        _binding!!.buttonDataUsuarioConfirmar.setOnClickListener{
            lifecycleScope.launch{
                withContext(Dispatchers.IO){
                    canICreateANewAccountValidatorAndLauncher()
                }
            }
        }

        _binding!!.buttonCrearCuentaTomarFoto.setOnClickListener{
            dispatchTakePictureIntent()
        }

        _binding!!.buttonDataUsuarioVolver.setOnClickListener{
            _viewModel.navigationCommand.value =
                NavigationCommand.To(
                    CrearCuentaFragmentDirections
                        .actionNavigationCrearcuentafragmentToNavigationLoginfragment())
        }

        return _binding!!.root
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            // Do something with the imageBitmap, such as saving it to a file or displaying it in an ImageView
        }else{
            Toast.makeText(requireActivity(),"Debes de tomar una foto para poder guardar un usuario", Toast.LENGTH_LONG).show()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun parseandoImagenParaSubirlaAFirestore(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return Base64.encodeToString(data, Base64.NO_PADDING)
    }

    private suspend fun canICreateANewAccountValidatorAndLauncher() {
        if(imageBitmap == null){
            Snackbar.make(_binding!!.root, R.string.falto_foto, Snackbar.LENGTH_LONG).show()
            return
        }
        val foto = parseandoImagenParaSubirlaAFirestore(imageBitmap!!)
        val nombreCompleto = _binding!!.editTextDataUsuarioNombreCompleto.text.toString()
        val rut = _binding!!.editTextDataUsuarioRut.text.toString()
        val telefono = _binding!!.editTextDataUsuarioTelefono.text.toString()
        val email = _binding!!.editTextDataUsuarioUsuario.text.toString()
        val password = _binding!!.editTextDataUsuarioPassword.text.toString()
        val password2 = _binding!!.editTextDataUsuarioConfirmarPassword.text.toString()
        val perfil = _binding!!.editTextDataUsuarioRol.text.toString();

        if (validarInputsYFoto(nombreCompleto, rut, email, password, password2, perfil)) return

        _viewModel.crearCuentaEnFirebaseAuthYFirestore(
            PreDataUsuarioEnFirestore(foto, nombreCompleto, rut, telefono, email, password, perfil)
        )

    }

    private fun validarInputsYFoto(nombreCompleto: String, rut: String, email: String, password: String, password2: String, rol: String): Boolean {
        if (imageBitmap == null) {
            Snackbar.make(
                _binding!!.root,
                "Debes de tomar una foto para poder guardar un usuario",
                Snackbar.LENGTH_LONG
            ).show()
            return true
        }
        if (nombreCompleto.isEmpty() || rut.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() || rol.isEmpty()) {
            Snackbar.make(
                _binding!!.root,
                "Debes completar todos los campos antes de crear una cuenta.",
                Snackbar.LENGTH_LONG
            ).show()
            return true
        }
        if (nombreCompleto.split(" ").size != 3) {
            Snackbar.make(
                _binding!!.root,
                "Debe ingresar su nombre y sus 2 apellidos.",
                Snackbar.LENGTH_LONG
            ).show()
            return true
        }
        if (!isValidRut(rut)){
            Snackbar.make(
                _binding!!.root,
                "El rut que ingresaste no es valido.",
                Snackbar.LENGTH_LONG
            ).show()
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(
                _binding!!.root,
                "El email que ingresaste no es valido",
                Snackbar.LENGTH_LONG
            ).show()
            return true
        }
        if (password != password2) {
            Snackbar.make(
                _binding!!.root,
                "Las contraseñas no coincide.",
                Snackbar.LENGTH_LONG
            ).show()
            return true
        }
        if (password.length < 6) {
            Snackbar.make(
                _binding!!.root,
                "La contraseña debe tener a lo menos 6 caracteres.",
                Snackbar.LENGTH_LONG
            ).show()
            return true
        }

        return false
    }

    private fun isValidRut(rut: String): Boolean {
        var rutClean = rut.replace(".", "").replace("-", "")
        if (rutClean.length != 9) return false
        var dv = rutClean.last().toUpperCase()
        if (!dv.isDigit() && dv != 'K') return false
        rutClean = rutClean.dropLast(1)
        var sum = 0
        var factor = 2
        for (i in rutClean.reversed()) {
            sum += (i.toString().toInt() * factor)
            factor = if (factor == 7) 2 else factor + 1
        }
        val dvCalc = 11 - (sum % 11)
        val dvExpected = when (dvCalc) {
            11 -> "0"
            10 -> "K"
            else -> dvCalc.toString()
        }
        return dv.toString() == dvExpected
    }

}