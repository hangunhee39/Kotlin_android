package hgh.project.shopping.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hgh.project.shopping.data.entity.product.ProductEntity
import hgh.project.shopping.databinding.ProductItemBinding
import hgh.project.shopping.extensions.loadCenterCrop

class ProductListAdapter(private val onClickListener: (ProductEntity) -> Unit) :
    ListAdapter<ProductEntity, ProductListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductEntity) {
            binding.productNameTextView.text = item.productName
            binding.productPriceTextView.text ="${item.productPrice}원"
            binding.productImageView.loadCenterCrop(item.productImage, 8F)
            binding.root.setOnClickListener {
                onClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ProductEntity>() {
            override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ProductEntity,
                newItem: ProductEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}