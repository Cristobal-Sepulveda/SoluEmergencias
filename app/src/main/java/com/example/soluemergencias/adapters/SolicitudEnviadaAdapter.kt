package com.example.soluemergencias.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soluemergencias.R
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.databinding.ItemSolicitudDeVinculoBinding
import com.example.soluemergencias.databinding.ItemSolicitudEnviadaBinding
import com.example.soluemergencias.ui.vincularcuentas.VincularCuentasViewModel
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import kotlinx.coroutines.*


class SolicitudEnviadaAdapter()
    : ListAdapter<SolicitudDeVinculo, SolicitudEnviadaAdapter.SolicitudEnviadaViewHolder>(DiffCallBack) {

    class SolicitudEnviadaViewHolder(private var binding: ItemSolicitudEnviadaBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(solicitudDeVinculo: SolicitudDeVinculo) {
            binding.solicitudItem = solicitudDeVinculo
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudEnviadaViewHolder {
        return SolicitudEnviadaViewHolder(ItemSolicitudEnviadaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: SolicitudEnviadaViewHolder, position: Int) {
        val solicitudDeVinculo = getItem(position)
        holder.bind(solicitudDeVinculo)
    }

    object DiffCallBack: DiffUtil.ItemCallback<SolicitudDeVinculo>(){
        override fun areItemsTheSame(oldItem: SolicitudDeVinculo, newItem: SolicitudDeVinculo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SolicitudDeVinculo, newItem: SolicitudDeVinculo): Boolean {
            return oldItem.rutDelSolicitante == newItem.rutDelSolicitante
        }
    }

}
