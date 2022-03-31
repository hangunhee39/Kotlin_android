package org.techtown.tinder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.tinder.DBKey.Companion.USER
import org.techtown.tinder.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var callbackManger: CallbackManager

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //firebase
        auth = Firebase.auth

        //facebook 로그인용 callback 초기화
        callbackManger= CallbackManager.Factory.create()

        //로그인 (이메일)
        initLoginButton()

        //회원가입
        initSignUpButton()

        //로그인 회원가입 활성화
        initEmailAndPasswordEditText()

        //페이스북 로그인
        initFaceBookLoginButton()
    }

    private fun initLoginButton() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        handlesuccessLogin()
                    } else {
                        Toast.makeText(
                            this,
                            "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun initSignUpButton() {
        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인 해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "이미가입한 이메일이거나, 회원가입에 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }

    private fun initEmailAndPasswordEditText(){
        binding.emailEditText.addTextChangedListener {
            val enable = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
            binding.loginButton.isEnabled =enable
            binding.signUpButton.isEnabled=enable
        }
        binding.passwordEditText.addTextChangedListener {
            val enable = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
            binding.loginButton.isEnabled =enable
            binding.signUpButton.isEnabled=enable
        }

    }

    private fun initFaceBookLoginButton() {

        binding.facebookLoginButton.setPermissions("email","public_profile")
        binding.facebookLoginButton.registerCallback(callbackManger, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                //로그인 성공
                val credential =FacebookAuthProvider.getCredential(result.accessToken.token)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) {task ->
                        if (task.isSuccessful){
                            handlesuccessLogin()
                        }else{
                            Toast.makeText(this@LoginActivity,"페이스북 로그인이 실패했습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity,"페이스북 로그인이 실패했습니다.",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun handlesuccessLogin(){
        if (auth.currentUser==null){
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        //계정 생성될때 유저id 만들어서 DB에 넣기
        val userId = auth.currentUser?.uid.orEmpty()
        val currentUserDB = Firebase.database.reference.child(USER).child(userId)
        val user = mutableMapOf<String,Any>()
        user["userId"]=userId
        currentUserDB.updateChildren(user)

        finish()
    }

    //facebook버튼 정보 받기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManger.onActivityResult(requestCode, resultCode, data)
    }

}