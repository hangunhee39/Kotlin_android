package org.techtown.secondhand.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.secondhand.DBKey.Companion.CHILD_CHAT
import org.techtown.secondhand.DBKey.Companion.DB_USER
import org.techtown.secondhand.R
import org.techtown.secondhand.chatdetail.ChatRoomActivity
import org.techtown.secondhand.databinding.FragmentChatlistBinding

class ChatListFragment: Fragment(R.layout.fragment_chatlist) {

    private var binding: FragmentChatlistBinding ?=null
    private lateinit var chatListAdapter : ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()

    private val auth :FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)
        binding =fragmentChatlistBinding

        chatListAdapter =ChatListAdapter(onItemClicked ={chatRoom->
            //채팅방으로 이동 하는 코드
            context?.let {
                val intent =Intent(it, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatRoom.key)
                startActivity(intent)
            }
        })

        //recycleView
        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatlistBinding.chatListRecyclerView.layoutManager=LinearLayoutManager(context)

        chatRoomList.clear()

        //로그인 상태가 아니면
        if (auth.currentUser ==null){
            return
        }

        val chatDB = Firebase.database.reference.child(DB_USER).child(auth.currentUser?.uid!!).child(CHILD_CHAT)
        chatDB.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val model =it.getValue(ChatListItem::class.java)
                    model ?:return

                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }
}