package hgh.project.shopping.presentation.profile

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import hgh.project.shopping.R
import hgh.project.shopping.databinding.FragmentProfileBinding
import hgh.project.shopping.extensions.loadCenterCrop
import hgh.project.shopping.presentation.BaseFragment
import hgh.project.shopping.presentation.adapter.ProductListAdapter
import hgh.project.shopping.presentation.datail.ProductDetailActivity
import org.koin.android.ext.android.inject
import java.lang.Exception

internal class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>() {

    companion object {
        const val TAG = "ProfileFragment"
    }

    override val viewModel by inject<ProfileViewModel>()

    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    //구글 로그인옵션
    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    //구글 로그인 창
    private val gsc by lazy { GoogleSignIn.getClient(requireActivity(), gso) }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    task.getResult(ApiException::class.java)?.let { account ->
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                        //토큰 저장해서 다음부터 바로 로그인 가능하게
                        viewModel.saveToken(account.idToken ?: throw Exception())
                    } ?: throw Exception()
                } catch (e: Exception) {
                    e.printStackTrace()
                    handleErrorState()
                }
            }
        }

    private val adapter = ProductListAdapter {
        startActivity(
            ProductDetailActivity.newIntent(requireContext(), it.id)
        )
    }

    override fun observeData() = viewModel.profileStateLiveData.observe(this) {
        when (it) {
            is ProfileState.Uninitialized -> initViews()
            is ProfileState.Loading -> handleLoadingState()
            is ProfileState.Login -> handleLoginState(it)
            is ProfileState.Success -> handleSuccessState(it)
            is ProfileState.Error -> handleErrorState()
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = adapter
        loginButton.setOnClickListener {
            signInGoogle()
        }
        logoutButton.setOnClickListener {
            viewModel.signOut()
        }
    }

    private fun handleLoadingState() = with(binding) {
        progressBar.isVisible = true
        loginRequiredGroup.isGone = true
    }

    private fun handleSuccessState(state: ProfileState.Success) = with(binding) {
        progressBar.isGone = true
        when (state) {
            is ProfileState.Success.Registered -> {
                handleRegisteredState(state)
            }
            is ProfileState.Success.NotRegistered -> {
                profileGroup.isGone = true
                loginRequiredGroup.isVisible = true
            }
        }
    }

    private fun handleLoginState(state: ProfileState.Login) = with(binding) {
        progressBar.isVisible =true
        //파이어베이스 로그인
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    //유저 정보 가져오기 (success 으로 이동)
                    viewModel.setUserInfo(user)
                } else {
                    viewModel.setUserInfo(null)
                }
            }

    }

    //로그인 성공시 유저 정보 보여줌
    private fun handleRegisteredState(state: ProfileState.Success.Registered) = with(binding) {
        profileGroup.isGone = false
        loginRequiredGroup.isGone = true
        profileImageView.loadCenterCrop(state.profileImage.toString(), 60f)
        userNameTextView.text = state.userName

        if (state.productList.isEmpty()) {
            emptyResultTextView.isVisible = true
            recyclerView.isGone = true
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isVisible= true
            adapter.submitList(state.productList)
        }
    }

    private fun handleErrorState() {
        Toast.makeText(context, "에러 발생", Toast.LENGTH_SHORT).show()
    }

    private fun signInGoogle() {
        val signInIntent: Intent = gsc.signInIntent
        launcher.launch(signInIntent)
    }
}