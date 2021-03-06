package org.techtown.tinder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import org.techtown.tinder.DBKey.Companion.DIS_LIKE
import org.techtown.tinder.DBKey.Companion.LIKE
import org.techtown.tinder.DBKey.Companion.LIKED_BY
import org.techtown.tinder.DBKey.Companion.MATCH
import org.techtown.tinder.DBKey.Companion.NAME
import org.techtown.tinder.DBKey.Companion.USER
import org.techtown.tinder.DBKey.Companion.USER_ID
import org.techtown.tinder.databinding.ActivityLikeBinding

class LikeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLikeBinding

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    private val adapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>()

    private val manager: CardStackLayoutManager by lazy {
        CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction?) {
                when (direction) {
                    Direction.Right -> {
                        like()
                    }
                    Direction.Left -> {
                        dislike()
                    }
                    else -> {}
                }
            }

            override fun onCardRewound() {}

            override fun onCardCanceled() {}

            override fun onCardAppeared(view: View?, position: Int) {}

            override fun onCardDisappeared(view: View?, position: Int) {}

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(USER)

        val currentUserDB = userDB.child(getCurrentUserID())
        //DB????????? ???????????? ?????? ??????
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {         //???????????? ?????????
                if (snapshot.child(NAME).value == null) {
                    //?????? ?????? dialog ??????
                    showNameInputPopup()
                    return
                }
                //?????? ?????? ??????
                getUnSelectedUsers()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        //cardStackView ??????
        initCardStackView()
        //???????????? ??????
        initSignOutButton()
        //??????????????? ??????
        initMatchedListButton()
    }

    private fun initCardStackView() {
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
    }

    private fun initSignOutButton(){
        binding.signOutButton.setOnClickListener {
            //?????? ????????????
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initMatchedListButton(){
        binding.matchListButton.setOnClickListener {
            startActivity(Intent(this, MatchedUserActivity::class.java))
        }
    }

    private fun getUnSelectedUsers() {
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.child(USER_ID).value != getCurrentUserID()
                    && snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserID()).not()
                    && snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserID()).not()
                ) {
                    //????????? ?????????, like??? dislike??? ????????? ?????? ?????? ??????
                    val userId = snapshot.child(USER_ID).value.toString()
                    var name = "undecided"
                    if (snapshot.child(NAME).value != null) {
                        name = snapshot.child(NAME).value.toString()
                    }
                    cardItems.add(CardItem(userId, name))
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //????????? ????????? ????????????
                cardItems.find {
                    it.userId == snapshot.key
                }?.let {
                    it.name = snapshot.child(NAME).value.toString()
                }

                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged()

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun saveUserName(name: String) {

        val userId = getCurrentUserID()
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        user["name"] = name
        currentUserDB.updateChildren(user)

        //?????? ?????? ????????????
        getUnSelectedUsers()
    }


    private fun showNameInputPopup() {
        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("????????? ??????????????????")
            .setView(editText)
            .setPositiveButton("??????", { _, _ ->
                if (editText.text.isEmpty()) {
                    showNameInputPopup()
                } else {
                    saveUserName(editText.text.toString())
                }
            })
            .setCancelable(false)
            .show()
    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "???????????? ?????? ?????? ????????????", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun like() {
        //????????? ????????? like?????? ??????
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId).child(LIKED_BY).child(LIKE).child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(this, "${card.name}?????? like ???????????????.", Toast.LENGTH_SHORT).show()

        //?????? like??? ????????? ???????????? ????????????
        saveMatchIfOtherUserLikedMe(card.userId)
    }

    private fun dislike() {
        //????????? ????????? dislike?????? ??????
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId).child(LIKED_BY).child(DIS_LIKE).child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(this, "${card.name}?????? dislike ???????????????.", Toast.LENGTH_SHORT).show()
    }

    private fun saveMatchIfOtherUserLikedMe(otherUserId: String) {
        val otherUserDB =
            userDB.child(getCurrentUserID()).child(LIKED_BY).child(LIKE).child(otherUserId)
        otherUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == true) {
                    userDB.child(getCurrentUserID()).child(LIKED_BY).child(MATCH)
                        .child(otherUserId)
                        .setValue(true)

                    userDB.child(otherUserId).child(LIKED_BY).child(MATCH)
                        .child(getCurrentUserID())
                        .setValue(true)
                }

            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }
}