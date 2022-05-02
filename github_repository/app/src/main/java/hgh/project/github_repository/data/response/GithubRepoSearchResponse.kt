package hgh.project.github_repository.data.response

import hgh.project.github_repository.data.entity.GithubRepoEntity

data class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepoEntity>
)