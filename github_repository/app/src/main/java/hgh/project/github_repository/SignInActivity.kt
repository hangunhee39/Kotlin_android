package hgh.project.github_repository

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import hgh.project.github_repository.databinding.ActivitySignInBinding
import hgh.project.github_repository.utillity.AuthTokenProvider
import hgh.project.github_repository.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignInActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivitySignInBinding

    private val authTokenProvider by lazy {
        AuthTokenProvider(this)
    }

    var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //토큰 받은게 있으면 main 으로
        if (checkAuthCodeExist()){
            launchMainActivity()
        }else {
            initViews()
        }
    }

    private fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            loginGithub()
        }
    }

    private fun launchMainActivity() {
        startActivity(Intent(this,MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) //로그인창 끄기
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    //preferences 에 토큰이 있으면 true
    private fun checkAuthCodeExist():Boolean {
        return authTokenProvider.token.isNullOrEmpty().not()
    }

    //github 토큰 받아서 승인하기
    private fun loginGithub() {
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .build()

        CustomTabsIntent.Builder().build().also {
            it.launchUrl(this, loginUri)
        }
    }

    //intent 를 받으면
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.data?.getQueryParameter("code")?.let {
            //AccessToken 얻었으면
            launch(coroutineContext) {
                showProgress()
                getAccessToken(it)
                dismissProgress()
                if (checkAuthCodeExist()) {
                    launchMainActivity()
                }
            }
        }
    }

    private suspend fun showProgress() = withContext(Dispatchers.Main) {
        with(binding) {
            loginButton.isGone = true
            progressBar.isGone = false
            progressTextView.isGone = false
        }
    }

    private suspend fun dismissProgress() = withContext(Dispatchers.Main) {
        with(binding) {
            loginButton.isGone = false
            progressBar.isGone = true
            progressTextView.isGone = true
        }
    }

    //토큰 받기 (api)
    private suspend fun getAccessToken(code: String) = withContext(Dispatchers.IO) {
        val response = RetrofitUtil.authApiService.getAccessToken(
            clientId = BuildConfig.GITHUB_CLIENT_ID,
            clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
            code = code
        )
        if (response.isSuccessful) {
            val accessToken = response.body()?.accessToken ?: ""
            Log.e("accessToken", accessToken)
            if (accessToken.isNotEmpty()) {
                //preferences 에 토큰 넣기
                authTokenProvider.updateToken(accessToken)
            } else {
                Toast.makeText(this@SignInActivity, "accessToken이 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}