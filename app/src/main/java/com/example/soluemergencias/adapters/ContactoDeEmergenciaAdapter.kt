package com.example.soluemergencias.adapters

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soluemergencias.R
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.databinding.ItemContactoDeEmergenciaBinding
import com.example.soluemergencias.databinding.ItemSolicitudDeVinculoBinding
import com.example.soluemergencias.ui.vincularcuentas.VincularCuentasViewModel
import com.example.soluemergencias.ui.vistageneral.VistaGeneralViewModel
import com.example.soluemergencias.utils.*
import kotlinx.coroutines.*
import java.util.*


class ContactoDeEmergenciaAdapter(viewModel: VistaGeneralViewModel, dataSource: AppDataSource, val onClickListener: OnClickListener)
    : ListAdapter<ContactoDeEmergencia, ContactoDeEmergenciaAdapter.ContactoDeEmergenciaViewHolder>(DiffCallBack) {

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

    private fun bindearElItemSegunElNombre(foto: String, view: View, contactoDeEmergencia: ContactoDeEmergencia) {
        if ((foto.last().toString() == "=") || ((foto.first().toString() == "/") && (foto[1].toString() == "9"))) {
            val decodedString = android.util.Base64.decode(foto, android.util.Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            view.findViewById<ImageView>(R.id.imageView_itemContactoDeEmergencia_foto).setImageBitmap(decodedByte)
        } else {
            val aux2 = foto.indexOf("=") + 1
            val aux3 = foto.substring(0, aux2)
            val decodedString = android.util.Base64.decode(aux3, android.util.Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            view.findViewById<ImageView>(R.id.imageView_itemContactoDeEmergencia_foto).setImageBitmap(decodedByte)
        }
        view.findViewById<TextView>(R.id.textView_itemContactoDeEmergencia_nombre).text = "Contacto: "+contactoDeEmergencia.nombre
        view.findViewById<TextView>(R.id.textView_itemContactoDeEmergencia_telefono).text = "Teléfono: "+contactoDeEmergencia.telefono
        view.findViewById<ImageView>(R.id.imageView_itemContactoDeEmergencia_llamarContacto)
            .setOnClickListener { llamarContacto(contactoDeEmergencia.telefono, view) }
    }

    private fun llamarContacto(telefono: String, view: View){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$telefono"))
        view.context.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoDeEmergenciaViewHolder {
        return ContactoDeEmergenciaViewHolder(ItemContactoDeEmergenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class OnClickListener(val clickListener: (contactoDeEmergencia : ContactoDeEmergencia) -> Unit) {
        fun onClick(contactoDeEmergencia : ContactoDeEmergencia) = clickListener(contactoDeEmergencia)
    }

}
