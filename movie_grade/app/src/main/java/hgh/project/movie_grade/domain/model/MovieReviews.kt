package hgh.project.movie_grade.domain.model

data class MovieReviews(
    val myReview: Review?,
    val othersReview: List<Review>
)