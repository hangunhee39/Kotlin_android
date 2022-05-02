package hgh.project.github_repository.data.response

data class GitBubAccessTokenResponse(
    val accessToken: String,
    val scope: String,
    val tokenType: String
)