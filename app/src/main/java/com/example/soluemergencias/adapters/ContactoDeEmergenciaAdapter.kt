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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soluemergencias.BuildConfig
import com.example.soluemergencias.R
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergencia
import com.example.soluemergencias.databinding.ItemContactoDeEmergenciaBinding
import com.example.soluemergencias.ui.vistageneral.VistaGeneralViewModel
import com.example.soluemergencias.utils.*
import com.google.maps.model.LatLng
import kotlinx.coroutines.*


class ContactoDeEmergenciaAdapter(sharedPreferences: SharedPreferences)
    : ListAdapter<ContactoDeEmergencia, ContactoDeEmergenciaAdapter.ContactoDeEmergenciaViewHolder>(DiffCallBack) {

    private val _sharedPreferences = sharedPreferences

    class ContactoDeEmergenciaViewHolder(private var binding: ItemContactoDeEmergenciaBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(contactoDeEmergencia: ContactoDeEmergencia) {
            binding.contactoDeEmergenciaItem = contactoDeEmergencia
            binding.executePendingBindings()
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
        return ContactoDeEmergenciaViewHolder(
            ItemContactoDeEmergenciaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    object DiffCallBack: DiffUtil.ItemCallback<ContactoDeEmergencia>(){
        override fun areItemsTheSame(oldItem: ContactoDeEmergencia, newItem: ContactoDeEmergencia): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ContactoDeEmergencia, newItem: ContactoDeEmergencia): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private fun bindearElItemSegunElNombre(foto: String, view: View, contactoDeEmergencia: ContactoDeEmergencia) {
        val fotoContacto = view.findViewById<ImageView>(R.id.imageView_itemContactoDeEmergencia_foto)
        val nombreContacto = view.findViewById<TextView>(R.id.textView_itemContactoDeEmergencia_nombre)
        val telefonoContacto = view.findViewById<TextView>(R.id.textView_itemContactoDeEmergencia_telefono)
        val botonLlamarContacto= view.findViewById<ImageView>(R.id.imageView_itemContactoDeEmergencia_llamarContacto)

        fotoContacto.setImageBitmap(parsingBase64ImageToBitMap(foto))
        nombreContacto.text = "Contacto: "+contactoDeEmergencia.nombre
        telefonoContacto.text = "Teléfono: "+contactoDeEmergencia.telefono

        botonLlamarContacto.setOnClickListener {
            llamarContacto(contactoDeEmergencia.telefono, view)
        }

    }

    private fun llamarContacto(telefono: String, view: View){
        _sharedPreferences.edit().putBoolean("llamadaRealizada", true).apply()
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$telefono"))
        view.context.startActivity(intent)
    }
}
