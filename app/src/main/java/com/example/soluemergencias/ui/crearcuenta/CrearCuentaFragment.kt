package com.example.soluemergencias.ui.crearcuenta

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.soluemergencias.R
import com.example.soluemergencias.data.data_objects.domainObjects.DataUsuarioEnFirestore
import com.example.soluemergencias.databinding.FragmentCrearCuentaBinding
import com.example.soluemergencias.utils.isThisRutValid
import com.example.soluemergencias.utils.showToastInMainThread
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream

class CrearCuentaFragment: Fragment() {

    private var _binding: FragmentCrearCuentaBinding? = null
    private val _viewModel: CrearCuentaViewModel by inject()
    private var imageBitmap: Bitmap? = null
    private val requestTakePicture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            imageBitmap = result.data?.extras?.get("data") as? Bitmap
            val foto = parseandoImagenParaSubirlaAFirestore(imageBitmap!!)
            decodeAndSetImageString(foto)
        }
    }

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

        _binding!!.imageViewCrearCuentaTomarFoto.setOnClickListener{
            dispatchTakePictureIntent()
        }

        _binding!!.buttonDataUsuarioVolver.setOnClickListener{
            findNavController().navigate(
                CrearCuentaFragmentDirections
                    .actionNavigationCrearcuentafragmentToNavigationLoginfragment()
            )
        }

        return _binding!!.root
    }


    private fun decodeAndSetImageString(fotoPerfil: String){
        val decodedString = Base64.decode(fotoPerfil, Base64.NO_PADDING)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        _binding!!.imageViewCrearCuentaFotoPerfil.setImageBitmap(decodedByte)
    }
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                requestTakePicture.launch(takePictureIntent)
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

        val task = _viewModel.crearCuentaEnFirebaseAuthYFirestore(
            DataUsuarioEnFirestore(foto, nombreCompleto, rut, telefono,
                email, password, perfil,false)
        )
        showToastInMainThread(requireActivity(), task.second)
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
        if (!isThisRutValid(rut)){
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

}