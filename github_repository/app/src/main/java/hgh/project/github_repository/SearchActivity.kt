package hgh.project.github_repository

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import hgh.project.github_repository.data.entity.GithubRepoEntity
import hgh.project.github_repository.databinding.ActivitySearchBinding
import hgh.project.github_repository.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main +job

    private val job = Job()

    private lateinit var binding: ActivitySearchBinding
    private lateinit var repositoryAdapter: RepositoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()
    }

    private fun initAdapter(){
        repositoryAdapter= RepositoryListAdapter {
            startActivity(
                Intent(this@SearchActivity,RepositoryActivity::class.java ).apply {
                    putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                    putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                })
        }
    }

    private fun initViews() =with(binding){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = repositoryAdapter
        }
        emptyResultTextView.isGone = true
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun searchKeyword(keyword:String){
        launch(coroutineContext) {
            withContext(Dispatchers.IO){
                //keyword 로 githubApi 에 검색하기
                val response = RetrofitUtil.githubApiService.searchRepositories(keyword)
                if (response.isSuccessful){
                    val body = response.body()
                    withContext(Dispatchers.Main){
                        if (body != null) {
                            repositoryAdapter.submitList(body.items)
                        }
                    }
                }
            }
        }
    }

}