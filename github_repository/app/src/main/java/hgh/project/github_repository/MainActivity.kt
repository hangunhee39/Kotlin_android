package hgh.project.github_repository

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import hgh.project.github_repository.data.database.DataBaseProvider
import hgh.project.github_repository.data.entity.GithubOwner
import hgh.project.github_repository.data.entity.GithubRepoEntity
import hgh.project.github_repository.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main +job

    private lateinit var binding: ActivityMainBinding

    private lateinit var repositoryListAdapter: RepositoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()

    }

    override fun onResume() {
        super.onResume()

        //resume 될때 목록을 가져와서 adapter 에 리스트하기
        launch(coroutineContext) {
            loadLikedRepositoryList()
        }
    }

    private suspend fun loadLikedRepositoryList() = withContext(Dispatchers.IO) {
        //RoomDB 에서 like 목록 가져오기
        val repoList = DataBaseProvider.provideDB(this@MainActivity).searchHistoryDao().getHistory()
        withContext(Dispatchers.Main){
            setData(repoList)
        }
    }

    private fun setData(githubRepositoryList: List<GithubRepoEntity>) = with(binding){
        if (githubRepositoryList.isEmpty()){
            emptyResultTextView.isGone = false
            recyclerView.isGone= true
        }else{
            emptyResultTextView.isGone = true
            recyclerView.isGone= false
            repositoryListAdapter.submitList(githubRepositoryList)
        }
    }



    private fun initViews() = with(binding){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = repositoryListAdapter
        }
        searchButton.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, SearchActivity::class.java))
        }
    }

    private fun initAdapter(){
        repositoryListAdapter= RepositoryListAdapter {
            startActivity(
                Intent(this,RepositoryActivity::class.java ).apply {
                    putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                    putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                })
        }
    }

}