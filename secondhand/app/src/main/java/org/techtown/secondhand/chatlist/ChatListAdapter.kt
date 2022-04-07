package org.techtown.secondhand.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.techtown.secondhand.databinding.ItemChatListBinding

class ChatListAdapter(val onItemClicked: (ChatListItem) ->Unit): ListAdapter<ChatListItem, ChatListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val bindding: ItemChatListBinding): RecyclerView.ViewHolder(bindding.root){

        fun bind(chatListItem: ChatListItem){

            bindding.root.setOnClickListener {
                onItemClicked(chatListItem)
            }
            bindding.chatRoomTitleTextView.text= chatListItem.itemTitle

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object :DiffUtil.ItemCallback<ChatListItem>(){
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem==newItem
            }

        }
    }
}