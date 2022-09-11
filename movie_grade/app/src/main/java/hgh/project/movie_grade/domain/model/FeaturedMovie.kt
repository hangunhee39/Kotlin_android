package hgh.project.movie_grade.domain.model

data class FeaturedMovie(
    val movie: Movie,
    val latestReview: Review?
)