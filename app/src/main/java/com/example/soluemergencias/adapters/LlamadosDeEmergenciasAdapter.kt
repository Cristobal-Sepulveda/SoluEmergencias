package com.example.soluemergencias.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergenciaEnRecyclerView
import com.example.soluemergencias.databinding.ItemLlamadoDeEmergenciaBinding
import kotlinx.coroutines.*


class LlamadosDeEmergenciasAdapter : ListAdapter<
        LlamadoDeEmergenciaEnRecyclerView,
        LlamadosDeEmergenciasAdapter.LlamadosDeEmergenciasViewHolder
        >(DiffCallBack) {

    class LlamadosDeEmergenciasViewHolder(private var binding: ItemLlamadoDeEmergenciaBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(llamadoDeEmergencia: LlamadoDeEmergenciaEnRecyclerView) {
            binding.llamadoDeEmergenciaItem = llamadoDeEmergencia
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LlamadosDeEmergenciasViewHolder {
        return LlamadosDeEmergenciasViewHolder(ItemLlamadoDeEmergenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: LlamadosDeEmergenciasViewHolder, position: Int) {
        val llamadoDeEmergencia = getItem(position)
        holder.bind(llamadoDeEmergencia)
    }

    object DiffCallBack: DiffUtil.ItemCallback<LlamadoDeEmergenciaEnRecyclerView>(){
        override fun areItemsTheSame(oldItem: LlamadoDeEmergenciaEnRecyclerView, newItem: LlamadoDeEmergenciaEnRecyclerView): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LlamadoDeEmergenciaEnRecyclerView, newItem: LlamadoDeEmergenciaEnRecyclerView): Boolean {
            return oldItem.id == newItem.id
        }
    }

}
