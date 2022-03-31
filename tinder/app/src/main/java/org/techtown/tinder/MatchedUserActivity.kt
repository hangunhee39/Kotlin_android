package org.techtown.tinder

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.tinder.DBKey.Companion.LIKED_BY
import org.techtown.tinder.DBKey.Companion.MATCH
import org.techtown.tinder.DBKey.Companion.NAME
import org.techtown.tinder.DBKey.Companion.USER
import org.techtown.tinder.databinding.ActivityMatchBinding

class MatchedUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchBinding

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    private val adapter = MatchedUserAdapter()
    private val cardItems = mutableListOf<CardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(USER)

        //리사이클뷰 설정
        initMatchedUserRecyclerView()

        //매치된 유저 가져오기
        getMatchUsers()

    }

    private fun initMatchedUserRecyclerView() {

        binding.matchedUserRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.matchedUserRecyclerView.adapter = adapter
    }

    private fun getMatchUsers() {

        val matchedDB = userDB.child(getCurrentUserID()).child(LIKED_BY).child(MATCH)

        matchedDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key?.isNotEmpty() == true) {
                    //key로 이름 가져오기
                    getUserByKey(snapshot.key.orEmpty())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getUserByKey(userId: String) {

        userDB.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cardItems.add(CardItem(userId, snapshot.child(NAME).value.toString()))
                adapter.submitList(cardItems)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어 있지 않습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }
}