package org.techtown.secondhand.chatdetail

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.secondhand.DBKey.Companion.DB_CHATS
import org.techtown.secondhand.R

class ChatRoomActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()

    private lateinit var chatDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        //시간(key) 값 반아오기
        val chatKey = intent.getLongExtra("chatKey", -1)

        //DB
        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")
        chatDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem =snapshot.getValue(ChatItem::class.java)
                chatItem ?:return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })

        //recycleView
        findViewById<RecyclerView>(R.id.chatRecyclerView).adapter = adapter
        findViewById<RecyclerView>(R.id.chatRecyclerView).layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.sendButton).setOnClickListener {
            val chatItem = ChatItem(
                sendId = auth.currentUser?.uid!!,
                message = findViewById<EditText>(R.id.messageEditText).text.toString()
            )
            findViewById<EditText>(R.id.messageEditText).text.clear()

            chatDB?.push()?.setValue(chatItem)
        }
    }
}