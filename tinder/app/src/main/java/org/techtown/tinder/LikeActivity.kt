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
        //DB데이터 변경할때 마다 호출
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {         //데이터가 있을때
                if (snapshot.child(NAME).value == null) {
                    //이름 입력 dialog 설정
                    showNameInputPopup()
                    return
                }
                //유저 정보 갱신
                getUnSelectedUsers()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        //cardStackView 설정
        initCardStackView()
        //로그아웃 버튼
        initSignOutButton()
        //매칭페이지 버튼
        initMatchedListButton()
    }

    private fun initCardStackView() {
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
    }

    private fun initSignOutButton(){
        binding.signOutButton.setOnClickListener {
            //계정 로그아웃
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
                    //자신이 아니고, like나 dislike를 한번도 하지 않은 유저
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
                //유저의 정보가 바꿨을때
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

        //유저 정보 가져오기
        getUnSelectedUsers()
    }


    private fun showNameInputPopup() {
        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(editText)
            .setPositiveButton("확인", { _, _ ->
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
            Toast.makeText(this, "로그인이 되어 있지 않습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun like() {
        //카드의 맨위를 like하면 삭제
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId).child(LIKED_BY).child(LIKE).child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(this, "${card.name}님을 like 하셨습니다.", Toast.LENGTH_SHORT).show()

        //서로 like면 매칭이 된시점을 봐야한다
        saveMatchIfOtherUserLikedMe(card.userId)
    }

    private fun dislike() {
        //카드의 맨위를 dislike하면 삭제
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId).child(LIKED_BY).child(DIS_LIKE).child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(this, "${card.name}님을 dislike 하셨습니다.", Toast.LENGTH_SHORT).show()
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