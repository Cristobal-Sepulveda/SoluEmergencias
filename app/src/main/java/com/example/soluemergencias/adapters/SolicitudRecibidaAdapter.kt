package com.example.soluemergencias.adapters

import android.app.AlertDialog
import android.content.Context
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
import com.example.soluemergencias.utils.showToastInMainThread
import kotlinx.coroutines.*


class SolicitudRecibidaAdapter(viewModel: VincularCuentasViewModel, dataSource: AppDataSource, val onClickListener: OnClickListener)
    : ListAdapter<SolicitudDeVinculo, SolicitudRecibidaAdapter.SolicitudRecibidaViewHolder>(DiffCallBack) {
    val dataSourcee = dataSource
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudRecibidaViewHolder {
        return SolicitudRecibidaViewHolder(ItemSolicitudDeVinculoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: SolicitudRecibidaViewHolder, position: Int) {
        val solicitudDeVinculo = getItem(position)
        holder.itemView.apply {
            findViewById<ImageView>(R.id.imageView_itemSolicitudDeVinculo_aprobar).setOnClickListener {
                showConfirmationDialog(true, solicitudDeVinculo.rutDelSolicitante, it.context, it)
            }
            findViewById<ImageView>(R.id.imageView_itemSolicitudDeVinculo_rechazar).setOnClickListener {
                showConfirmationDialog(false, solicitudDeVinculo.rutDelSolicitante, it.context, it)
            }
        }
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
    class SolicitudRecibidaViewHolder(private var binding: ItemSolicitudDeVinculoBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(solicitudDeVinculo: SolicitudDeVinculo) {
            binding.solicitudItem = solicitudDeVinculo
            binding.executePendingBindings()
        }
    }

    private fun showConfirmationDialog(aprobar: Boolean, rutDelSolicitante: String, context: Context, view: View) {
        AlertDialog.Builder(context)
            .setTitle(R.string.atencion)
            .setMessage(if (aprobar) "¿Quieres confirmar esta solicitud?" else "¿Quieres rechazar esta solicitud?")
            .setPositiveButton("Si") { dialog, _ ->
                onAprobarRechazarClicked(aprobar, rutDelSolicitante, view)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }
    private fun onAprobarRechazarClicked(aprobar: Boolean, rutDelSolicitante: String, itemView: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = dataSourcee.aprobarORechazarSolicitudDeVinculo(rutDelSolicitante, aprobar)
            showToastInMainThread(itemView.context, task.second)
        }
    }
    class OnClickListener(val clickListener: (solicitudDeVinculo: SolicitudDeVinculo) -> Unit) {
        fun onClick(solicitudDeVinculo: SolicitudDeVinculo) = clickListener(solicitudDeVinculo)
    }

}
