package com.example.soluemergencias.adapters

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soluemergencias.R
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergencia
import com.example.soluemergencias.databinding.ItemContactoDeEmergenciaBinding
import com.example.soluemergencias.ui.vistageneral.VistaGeneralViewModel
import com.example.soluemergencias.utils.*
import kotlinx.coroutines.*


class ContactoDeEmergenciaAdapter(viewModel: VistaGeneralViewModel, sharedPreferences: SharedPreferences, activity: Activity)
    : ListAdapter<ContactoDeEmergencia, ContactoDeEmergenciaAdapter.ContactoDeEmergenciaViewHolder>(DiffCallBack) {

    private val _viewModel = viewModel
    private val _activity = activity
    private val _sharedPreferences = sharedPreferences

    class ContactoDeEmergenciaViewHolder(private var binding: ItemContactoDeEmergenciaBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(contactoDeEmergencia: ContactoDeEmergencia) {
            binding.contactoDeEmergenciaItem = contactoDeEmergencia
            binding.executePendingBindings()
        }
    }
    object DiffCallBack: DiffUtil.ItemCallback<ContactoDeEmergencia>(){
        override fun areItemsTheSame(oldItem: ContactoDeEmergencia, newItem: ContactoDeEmergencia): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ContactoDeEmergencia, newItem: ContactoDeEmergencia): Boolean {
            return oldItem.id == newItem.id
        }
    }
    override fun onBindViewHolder(holder: ContactoDeEmergenciaViewHolder, position: Int) {
        val contactoDeEmergencia = getItem(position)
        holder.itemView.apply {
            when(contactoDeEmergencia.nombre){
                "CONAF" -> bindearElItemSegunElNombre(fotoCONAF, this, contactoDeEmergencia)
                "SAMU" -> bindearElItemSegunElNombre(fotoSAMU, this, contactoDeEmergencia)
                "Bomberos" -> bindearElItemSegunElNombre(fotoBomberos, this, contactoDeEmergencia)
                "Carabineros" -> bindearElItemSegunElNombre(fotoCarabineros, this, contactoDeEmergencia)
                "PDI" -> bindearElItemSegunElNombre(fotoPDI, this, contactoDeEmergencia)
                else -> bindearElItemSegunElNombre(contactoDeEmergencia.foto, this, contactoDeEmergencia)
            }
        }
        holder.bind(contactoDeEmergencia)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoDeEmergenciaViewHolder {
        return ContactoDeEmergenciaViewHolder(ItemContactoDeEmergenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    private fun bindearElItemSegunElNombre(
        foto: String,
        view: View,
        contactoDeEmergencia: ContactoDeEmergencia) {

        val fotoContacto = view.findViewById<ImageView>(
            R.id.imageView_itemContactoDeEmergencia_foto
        )
        val nombreContacto = view.findViewById<TextView>(
            R.id.textView_itemContactoDeEmergencia_nombre
        )
        val telefonoContacto = view.findViewById<TextView>(
            R.id.textView_itemContactoDeEmergencia_telefono
        )
        val botonLlamarContacto= view.findViewById<ImageView>(
            R.id.imageView_itemContactoDeEmergencia_llamarContacto
        )

        fotoContacto.setImageBitmap(parsingBase64ImageToBitMap(foto))
        nombreContacto.text = "Contacto: "+contactoDeEmergencia.nombre
        telefonoContacto.text = "Teléfono: "+contactoDeEmergencia.telefono

        botonLlamarContacto.setOnClickListener {
            val aux =  _sharedPreferences.getBoolean("llamadaRealizada", false)
            Log.e("TAG", aux.toString())
            if(aux){
                showToastInMainThreadWithStringResource(_activity,
                    R.string.faltaCompletarDatosDeLaUltimaEmergencia
                )
            }else{
                _viewModel.viewModelScope.launch(Dispatchers.IO){
                    guardarLlamadoDeEmergencia()
                    llamarContacto(contactoDeEmergencia.telefono, view)
                }
            }

        }

    }
    private suspend fun guardarLlamadoDeEmergencia(){
        val (date, hour) = gettingLocalCurrentDateAndHour()
        val llamadoDeEmergencia = LlamadoDeEmergencia(
            _viewModel.obtenerUsuarioDesdeRoom().rut,
            date,
            hour,
            getCurrentLocationAsGeoPoint(_activity),
            "",
            "Sin gestionar"
        )
        _viewModel.registrarLlamadoDeEmergencia(llamadoDeEmergencia)
    }
    private fun llamarContacto(telefono: String, view: View){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$telefono"))
        view.context.startActivity(intent)
    }
}
