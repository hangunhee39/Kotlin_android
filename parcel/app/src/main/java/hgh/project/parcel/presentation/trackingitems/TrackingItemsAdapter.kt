package hgh.project.parcel.presentation.trackingitems

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hgh.project.parcel.R
import hgh.project.parcel.data.entity.Level
import hgh.project.parcel.data.entity.ShippingCompany
import hgh.project.parcel.data.entity.TrackingInformation
import hgh.project.parcel.data.entity.TrackingItem
import hgh.project.parcel.databinding.ItemTrackingBinding
import hgh.project.parcel.extension.setTextColorRes
import hgh.project.parcel.extension.toReadableDateString
import java.util.*

class TrackingItemsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<Pair<TrackingItem, TrackingInformation>> = emptyList()
    var onClickItemListener: ((TrackingItem, TrackingInformation) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemTrackingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                data.getOrNull(adapterPosition)?.let { (item, information) ->
                    onClickItemListener?.invoke(item, information)
                }
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(company: ShippingCompany, information: TrackingInformation) {
            binding.updatedAtTextView.text =
                Date(
                    information.lastDetail?.time ?: System.currentTimeMillis()
                ).toReadableDateString()

            binding.levelLabelTextView.text = information.level?.label
            when (information.level) {
                Level.COMPLETE -> {
                    binding.levelLabelTextView.setTextColorRes(R.color.gray)
                    binding.root.alpha = 0.5f
                }
                Level.PREPARE -> {
                    binding.levelLabelTextView.setTextColorRes(R.color.orange)
                    binding.root.alpha = 1f
                }
                else -> {
                    binding.levelLabelTextView.setTextColorRes(R.color.green)
                    binding.root.alpha = 1f
                }
            }

            binding.invoiceTextView.text = information.invoiceNo

            if (information.itemName.isNullOrBlank()) {
                binding.itemNameTextView.text = "이름 없음"
                binding.itemNameTextView.setTextColorRes(R.color.gray)
            } else {
                binding.itemNameTextView.text = information.itemName
                binding.itemNameTextView.setTextColorRes(R.color.black)
            }

            binding.lastStateTextView.text =
                information.lastDetail?.let { it.kind + " @${it.where}" }

            binding.companyNameTextView.text = company.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemTrackingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (item, trackingInformation) = data[position]    //pair 일때 데이터 할당

        (holder as ViewHolder).bind(item.company, trackingInformation) //viewHolder 인수가 2개여서
    }

    override fun getItemCount(): Int = data.size
}