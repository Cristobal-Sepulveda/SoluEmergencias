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
import com.example.soluemergencias.ui.vincularcuentas.VincularCuentasViewModel
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import kotlinx.coroutines.*


class SolicitudDeVinculoAdapter(viewModel: VincularCuentasViewModel, dataSource: AppDataSource, val onClickListener: OnClickListener)
    : ListAdapter<SolicitudDeVinculo, SolicitudDeVinculoAdapter.SolicitudDeVinculoViewHolder>(DiffCallBack) {

    val dataSourcee = dataSource

    class SolicitudDeVinculoViewHolder(private var binding: ItemSolicitudDeVinculoBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(solicitudDeVinculo: SolicitudDeVinculo) {
            binding.solicitudItem = solicitudDeVinculo
            binding.executePendingBindings()
        }
    }

    object DiffCallBack: DiffUtil.ItemCallback<SolicitudDeVinculo>(){
        override fun areItemsTheSame(oldItem: SolicitudDeVinculo, newItem: SolicitudDeVinculo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SolicitudDeVinculo, newItem: SolicitudDeVinculo): Boolean {
            return oldItem.rutDelSolicitante == newItem.rutDelSolicitante
        }
    }

    override fun onBindViewHolder(holder: SolicitudDeVinculoViewHolder, position: Int) {
        val solicitudDeVinculo = getItem(position)
        holder.itemView.apply {
            findViewById<ImageView>(R.id.imageView_itemSolicitudDeVinculo_aprobar).setOnClickListener {
                onAprobarRechazarClicked(true, solicitudDeVinculo.rutDelSolicitante,this)
            }
            findViewById<ImageView>(R.id.imageView_itemSolicitudDeVinculo_rechazar).setOnClickListener {
                onAprobarRechazarClicked(false, solicitudDeVinculo.rutDelSolicitante,this)
            }
        }
        holder.bind(solicitudDeVinculo)
    }
    private fun onAprobarRechazarClicked(aprobar: Boolean, rutDelSolicitante: String, itemView: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = dataSourcee.aprobarORechazarSolicitudDeVinculo(rutDelSolicitante, aprobar)
            showToastInMainThreadWithStringResource(itemView.context, task.second)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudDeVinculoViewHolder {
        return SolicitudDeVinculoViewHolder(ItemSolicitudDeVinculoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class OnClickListener(val clickListener: (solicitudDeVinculo: SolicitudDeVinculo) -> Unit) {
        fun onClick(solicitudDeVinculo: SolicitudDeVinculo) = clickListener(solicitudDeVinculo)
    }

}
