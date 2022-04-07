package org.techtown.secondhand.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.secondhand.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(val onItemClicked: (ArticleModel) ->Unit): ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val bindding: ItemArticleBinding): RecyclerView.ViewHolder(bindding.root){

        fun bind(articleModel: ArticleModel){
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.creatdAt)  //long 타입으로 date 타입으로

            if(articleModel.imageUrl.isNotEmpty()) {
                Glide.with(bindding.thumbnailImageView)
                    .load(articleModel.imageUrl)
                    .into(bindding.thumbnailImageView)
            }

            bindding.root.setOnClickListener {
                onItemClicked(articleModel)
            }

            bindding.titleTextView.text = articleModel.title
            bindding.dateTextView.text = format.format(date).toString()
            bindding.priceTextView.text = articleModel.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArticleBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object :DiffUtil.ItemCallback<ArticleModel>(){
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.creatdAt == newItem.creatdAt
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem==newItem
            }

        }
    }
}