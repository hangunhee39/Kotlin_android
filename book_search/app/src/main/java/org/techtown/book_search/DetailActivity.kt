package org.techtown.book_search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import org.techtown.book_search.databinding.ActivityDetailBinding
import org.techtown.book_search.model.Book
import org.techtown.book_search.model.Review

class DetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //db 빌드
        db = getAppDatabase(this)


        val model = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTextView.text = model?.title.orEmpty()
        binding.descriptionTextView.text = model?.description.orEmpty()

        Glide.with(binding.coverImageView.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        Thread{
            val review =db.reviewDao().getOneReview(model?.id?.toInt() ?: 0) ?: null
            runOnUiThread {
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()

        binding.saveButton.setOnClickListener {
            Thread{
                db.reviewDao().saveReview(
                    Review(model?.id?.toInt() ?:0,
                    binding.reviewEditText.text.toString())
                )
                finish()
            }.start()
        }
    }
}