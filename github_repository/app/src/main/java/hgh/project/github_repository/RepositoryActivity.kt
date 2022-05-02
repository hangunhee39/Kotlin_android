package hgh.project.github_repository

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import hgh.project.github_repository.data.database.DataBaseProvider
import hgh.project.github_repository.data.entity.GithubRepoEntity
import hgh.project.github_repository.databinding.ActivityRepositoryBinding
import hgh.project.github_repository.extensions.loadCenterInside
import hgh.project.github_repository.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityRepositoryBinding

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    companion object {
        const val REPOSITORY_OWNER_KEY = "REPOSITORY_OWNER_KEY"
        const val REPOSITORY_NAME_KEY = "REPOSITORY_NAME_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repositoryOwner = intent.getStringExtra(REPOSITORY_OWNER_KEY) ?: kotlin.run {
            Toast.makeText(this, "owner 가 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val repositoryName = intent.getStringExtra(REPOSITORY_NAME_KEY) ?: kotlin.run {
            Toast.makeText(this, "name 이 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        launch {
            //keyword 로 받은 repo 정보 로딩하고 layout 세팅
            loadRepository(repositoryOwner, repositoryName)?.let {
                setData(it)
            } ?: run {
                Toast.makeText(this@RepositoryActivity, "repository 정보가 없습니다", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private suspend fun loadRepository(
        repositoryOwner: String,
        repositoryName: String
    ): GithubRepoEntity? =
        withContext(coroutineContext) {
            var repositoryEntity: GithubRepoEntity? = null
            withContext(Dispatchers.IO) {
                //repo 에 정보 가져오기 (Api)
                val response = RetrofitUtil.githubApiService.getRepository(
                    ownerLogin = repositoryOwner,
                    repoName = repositoryName
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        body?.let { repo ->
                            repositoryEntity = repo
                        }
                    }
                }
            }
            repositoryEntity
        }

    private fun setData(githubRepoEntity: GithubRepoEntity) = with(binding){
        showLoading(false)
        ownerProfileImageView.loadCenterInside(githubRepoEntity.owner.avatarUrl, 42f)
        ownerNameAndRepoNameTextView.text= "${githubRepoEntity.owner.login}/${githubRepoEntity.name}"
        stargazersCountText.text = githubRepoEntity.stargazersCount.toString()
        githubRepoEntity.language?.let { language ->
            languageText.isGone = false
            languageText.text = language
        } ?: kotlin.run {
            languageText.isGone = true
            languageText.text = ""
        }
        descriptionTextView.text = githubRepoEntity.description
        updateTimeTextView.text = githubRepoEntity.updatedAt

        setLikeState(githubRepoEntity)
    }

    private fun setLikeState(githubRepoEntity: GithubRepoEntity) = launch {
        withContext(Dispatchers.IO){
            //RoomDB 에서 Like 목록에 있으면 isLike 를 true 없으면 false
            val repository = DataBaseProvider.provideDB(this@RepositoryActivity).searchHistoryDao().getRepository(githubRepoEntity.fullName)
            val isLike = repository !=null
            withContext(Dispatchers.Main){
                setLikeImage(isLike)
                binding.likeButton.setOnClickListener {
                    likeGithubRepository(githubRepoEntity, isLike)
                }
            }
        }
    }

    //like 눌렀을때 이미지 바꾸기
    private fun setLikeImage(isLike: Boolean) = with(binding) {
        likeButton.setImageDrawable(
            ContextCompat.getDrawable(
                this@RepositoryActivity,
                if (isLike){
                    R.drawable.ic_baseline_favorite_24
                }else{
                    R.drawable.ic_baseline_favorite_border_24
                }
            )
        )
    }

    //like 한 repo RoomDB 에 넣기 dislike 하면 빼기
    private fun likeGithubRepository(githubRepoEntity: GithubRepoEntity, isLike: Boolean)= launch{
        withContext(Dispatchers.IO){
            if (isLike){
                DataBaseProvider.provideDB(this@RepositoryActivity).searchHistoryDao().remove(githubRepoEntity.fullName)
            }else{
                DataBaseProvider.provideDB(this@RepositoryActivity).searchHistoryDao().insert(githubRepoEntity)
            }
            withContext(Dispatchers.Main){
                setLikeImage(isLike.not())
            }
        }
    }

    private fun showLoading(isShown: Boolean) = with(binding){
        progressBar.isGone = isShown.not()
    }
}