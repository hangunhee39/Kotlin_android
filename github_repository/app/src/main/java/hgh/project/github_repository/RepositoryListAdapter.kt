package hgh.project.github_repository

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hgh.project.github_repository.data.entity.GithubRepoEntity
import hgh.project.github_repository.databinding.ViewholderRepositoryItemBinding
import hgh.project.github_repository.extensions.loadCenterInside

class RepositoryListAdapter(private val searchResultClickListener: (GithubRepoEntity) -> Unit) :
    ListAdapter<GithubRepoEntity, RepositoryListAdapter.ViewHolder>(differ) {

    inner class ViewHolder(private val binding: ViewholderRepositoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GithubRepoEntity) = with(binding) {
            ownerProfileImageView.loadCenterInside(data.owner.avatarUrl, 24f)
            ownerNameTextView.text = data.owner.login
            nameTextView.text = data.fullName
            subtextTextView.text = data.description
            stargazersCountText.text = data.stargazersCount.toString()
            data.language?.let { language ->
                languageText.isGone = false
                languageText.text = language
            } ?: kotlin.run {
                languageText.isGone = true
                languageText.text = ""
            }
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderRepositoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<GithubRepoEntity>() {
            override fun areItemsTheSame(
                oldItem: GithubRepoEntity,
                newItem: GithubRepoEntity
            ): Boolean {
                return oldItem== newItem
            }

            override fun areContentsTheSame(
                oldItem: GithubRepoEntity,
                newItem: GithubRepoEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}