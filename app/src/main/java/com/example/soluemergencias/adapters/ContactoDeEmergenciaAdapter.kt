package com.example.soluemergencias.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.example.soluemergencias.utils.fotoBomberos
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import kotlinx.coroutines.*
import java.util.*


class ContactoDeEmergenciaAdapter(viewModel: VistaGeneralViewModel, dataSource: AppDataSource, val onClickListener: OnClickListener)
    : ListAdapter<ContactoDeEmergencia, ContactoDeEmergenciaAdapter.ContactoDeEmergenciaViewHolder>(DiffCallBack) {

    val dataSourcee = dataSource

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
            if ((fotoBomberos.last().toString() == "=") || ((fotoBomberos.first().toString() == "/") && (fotoBomberos[1].toString() == "9"))) {
                val decodedString = android.util.Base64.decode(fotoBomberos, android.util.Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                findViewById<ImageView>(R.id.imageView_itemContactoDeEmergencia_foto).setImageBitmap(decodedByte)
            } else {
                val aux2 = fotoBomberos.indexOf("=") + 1
                val aux3 = fotoBomberos.substring(0, aux2)
                val decodedString = android.util.Base64.decode(aux3, android.util.Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                findViewById<ImageView>(R.id.imageView_itemContactoDeEmergencia_foto).setImageBitmap(decodedByte)
            }
            /*findViewById<ImageView>(R.id.imageView_itemSolicitudDeVinculo_rechazar).setOnClickListener {
                llamarAlContactoDeEmergencia()
            }*/
        }
        holder.bind(contactoDeEmergencia)
    }
    private fun llamarAlContactoDeEmergencia() {
        CoroutineScope(Dispatchers.IO).launch {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoDeEmergenciaViewHolder {
        return ContactoDeEmergenciaViewHolder(ItemContactoDeEmergenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class OnClickListener(val clickListener: (contactoDeEmergencia : ContactoDeEmergencia) -> Unit) {
        fun onClick(contactoDeEmergencia : ContactoDeEmergencia) = clickListener(contactoDeEmergencia)
    }

}
