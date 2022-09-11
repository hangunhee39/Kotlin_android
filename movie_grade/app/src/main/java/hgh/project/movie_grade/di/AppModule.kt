package hgh.project.movie_grade.di

import android.app.Activity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hgh.project.movie_grade.data.api.*
import hgh.project.movie_grade.data.preference.PreferenceManager
import hgh.project.movie_grade.data.preference.SharedPreferenceManager
import hgh.project.movie_grade.data.repository.*
import hgh.project.movie_grade.domain.model.Movie
import hgh.project.movie_grade.domain.usecase.*
import hgh.project.movie_grade.presentation.home.HomeContract
import hgh.project.movie_grade.presentation.home.HomeFragment
import hgh.project.movie_grade.presentation.home.HomePresenter
import hgh.project.movie_grade.presentation.mypage.MyPageContract
import hgh.project.movie_grade.presentation.mypage.MyPageFragment
import hgh.project.movie_grade.presentation.mypage.MyPagePresenter
import hgh.project.movie_grade.presentation.review.MovieReviewContract
import hgh.project.movie_grade.presentation.review.MovieReviewFragment
import hgh.project.movie_grade.presentation.review.MovieReviewPresenter
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { Dispatchers.IO }
}

val dataModule = module {
    single { Firebase.firestore }

    single<MovieApi> { MovieFirestoreApi(get()) }
    single<ReviewApi> { ReviewFirestoreApi(get()) }
    single<UserApi> { UserFireStoreApi(get()) }

    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get()) }

    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }
}

val domainModule = module {
    factory { GetRandomFeaturedMovieUseCase(get(), get()) }
    factory { GetAllMoviesUseCase(get()) }
    factory { GetAllMovieReviewsUseCase(get(), get()) }
    factory { GetMyReviewedMoviesUseCase(get(), get(), get()) }
    factory { SubmitReviewUseCase(get(), get()) }
    factory { DeleteReviewUseCase(get()) }
}

val presenterModule = module {
    scope<HomeFragment> {
        scoped<HomeContract.Presenter> { HomePresenter(getSource(), get(), get()) }
    }
    scope<MovieReviewFragment> {
        scoped<MovieReviewContract.Presenter> { (movie: Movie) ->
            MovieReviewPresenter(movie, getSource(), get(), get(), get())
        }
    }
    scope<MyPageFragment> {
        scoped<MyPageContract.Presenter> { MyPagePresenter(getSource(), get()) }
    }
}