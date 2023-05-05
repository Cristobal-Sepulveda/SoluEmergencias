package com.example.soluemergencias.adapters

import android.view.LayoutInflater
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


class SolicitudDeVinculoAdapter(viewModel: VincularCuentasViewModel, dataSource: AppDataSource, val onClickListener: OnClickListener)
    : ListAdapter<SolicitudDeVinculo, SolicitudDeVinculoAdapter.SolicitudDeVinculoViewHolder>(DiffCallBack) {

    val dataSourcee = dataSource
    val viewModell = viewModel

    class SolicitudDeVinculoViewHolder(private var binding: ItemSolicitudDeVinculoBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(solicitudDeVinculo: SolicitudDeVinculo){
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
        val SolicitudDeVinculo = getItem(position)
        holder.itemView.let {
            it.findViewById<ImageView>(R.id.imageView_itemSolicitudDeVinculo_aprobar)
                .setOnClickListener { view ->

                }
            it.findViewById<ImageView>(R.id.imageView_itemSolicitudDeVinculo_rechazar)
                .setOnClickListener { view ->
                }
        }
        holder.bind(SolicitudDeVinculo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudDeVinculoViewHolder {
        return SolicitudDeVinculoViewHolder(ItemSolicitudDeVinculoBinding.inflate(LayoutInflater.from(parent.context)))
    }

    class OnClickListener(val clickListener: (solicitudDeVinculo: SolicitudDeVinculo) -> Unit) {
        fun onClick(solicitudDeVinculo: SolicitudDeVinculo) = clickListener(solicitudDeVinculo)
    }

}
