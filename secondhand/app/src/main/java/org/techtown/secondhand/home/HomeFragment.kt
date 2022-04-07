package org.techtown.secondhand.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.secondhand.DBKey.Companion.CHILD_CHAT
import org.techtown.secondhand.DBKey.Companion.DB_ARTICLES
import org.techtown.secondhand.DBKey.Companion.DB_USER
import org.techtown.secondhand.R
import org.techtown.secondhand.chatlist.ChatListItem
import org.techtown.secondhand.databinding.FragmentHomeBinding

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private val articleList = mutableListOf<ArticleModel>()
    private val listener =object :ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val articleModel =snapshot.getValue(ArticleModel::class.java)
            articleModel?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    private var binding: FragmentHomeBinding? =null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ViewBinding 연결 (Fragment 버전)
        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding= fragmentHomeBinding

        //리사이클뷰에 넣을 리스트 초기화 (안하면 중복으로 생김)
        articleList.clear()

        //DB 연결
        articleDB =Firebase.database.reference.child(DB_ARTICLES)
        userDB= Firebase.database.reference.child(DB_USER)

        //recycleView 어뎁터 연결
        articleAdapter =ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser !=null){
                //로그인 상태인경우
                if (auth.currentUser?.uid != articleModel.sellerId){

                    val chatRoom=ChatListItem(
                        buyerId = auth.currentUser?.uid,
                        sellerId = articleModel.sellerId,
                        itemTitle = articleModel.title,
                        key = System.currentTimeMillis()
                    )

                    userDB.child(auth.currentUser?.uid!!)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(articleModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    Snackbar.make(view,"채팅방이 생성됨.",Snackbar.LENGTH_LONG).show()

                }else{
                    //내가 올린 아이템
                    Snackbar.make(view,"내가 올린 상품입니다.",Snackbar.LENGTH_LONG).show()
                }
            }else{
                //로그인 상태가 아닌경우
                Snackbar.make(view,"로그인 후 사용해주세요",Snackbar.LENGTH_LONG).show()
            }

        })
        fragmentHomeBinding.articleRecyclerView.layoutManager =LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter =articleAdapter

        //float 버튼 설정
        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            if(auth.currentUser !=null) {
                //로그인 상태인경우
                startActivity(Intent(requireContext(), AddArticleActivity::class.java))
            }else{
                //로그인 상태가 아닌경우
                Snackbar.make(view,"로그인 후 사용해주세요",Snackbar.LENGTH_LONG).show()
            }
        }

        //DB 가져오기
        articleDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()

        //다시 들어갈때마다 리사이클뷰 초기화
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        //DB remove
        articleDB.removeEventListener(listener)
    }
}