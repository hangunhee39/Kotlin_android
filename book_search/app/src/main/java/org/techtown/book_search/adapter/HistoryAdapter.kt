package org.techtown.book_search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.book_search.databinding.ItemBookBinding
import org.techtown.book_search.databinding.ItmeHistoryBinding
import org.techtown.book_search.model.Book
import org.techtown.book_search.model.History

class HistoryAdapter(val historyDeleteClickedListener: (String) ->Unit, val itemClickListener: (String) ->Unit) : ListAdapter<History, HistoryAdapter.HistoryItemViewHolder>(diffUtil) {

    inner class HistoryItemViewHolder(private val binding: ItmeHistoryBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(historyModel: History){
            binding.historyKeywordTextView.text =historyModel.keyword

            binding.historyKeywordDeleteButton.setOnClickListener {
                historyDeleteClickedListener(historyModel.keyword.orEmpty())
            }

            binding.root.setOnClickListener {
                itemClickListener(historyModel.keyword.orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        //처음 만들때
        return HistoryItemViewHolder(ItmeHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        //현재 아이템일때
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem ==newItem
            }
            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem.keyword == newItem.keyword
            }
        }
    }
}