package hgh.project.ott_intro

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout
import hgh.project.ott_intro.databinding.ActivityMainBinding
import java.lang.Math.abs

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var isGateringMotionAnimating: Boolean = false
    private var isCurationMotionAnimating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //상태바 투명하게
        makeStatusBarTransparent()

        //AppBar 투명하게 (스크롤에 따라)
        initAppBar()

        //toolBar 의 마진
        initInsetMargin()

        binding.scrollView.smoothScrollTo(0, 0)
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {

            val scrolledValue = binding.scrollView.scrollY

            if (binding.scrollView.scrollY > 150f.dpToPx(this).toInt()) {
                if (isGateringMotionAnimating.not()) {
                    //end 로 진행
                    binding.gatheringDigitalThingsLayout.transitionToEnd()
                    binding.buttonShownMotionLayout.transitionToEnd()
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToEnd()
                }
            } else {
                if (isGateringMotionAnimating.not()) {
                    //start 로 진행
                    binding.gatheringDigitalThingsLayout.transitionToStart()
                    binding.buttonShownMotionLayout.transitionToStart()
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToStart()
                }
            }

            if (scrolledValue > binding.scrollView.height) {
                if (isCurationMotionAnimating.not()) {
                    binding.curationAnimationMotionLayout.setTransition(R.id.start1, R.id.end1)
                    binding.curationAnimationMotionLayout.transitionToEnd()
                    isCurationMotionAnimating = true
                }
            }

        }

        binding.gatheringDigitalThingsLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                //motion 이 시작하면 true 로 바꿔서 끝까지 motion 하게
                isGateringMotionAnimating = true
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                isGateringMotionAnimating = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })
        binding.curationAnimationMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                when (currentId) {
                    //end1이 끝나면 start2로 연결해서 transition 되도록 한다 (이중 모션)
                    R.id.end1 -> {
                        binding.curationAnimationMotionLayout.setTransition(R.id.start2, R.id.end2)
                        binding.curationAnimationMotionLayout.transitionToEnd()
                    }
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })


    }

    private fun initAppBar() {
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.dpToPx(this)
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)

            val realAlphaVerticalOffset =
                if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            if (abstractOffset < topPadding) {
                binding.toolbarBackgroundView.alpha = 0f
                return@OnOffsetChangedListener
            }
            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            binding.toolbarBackgroundView.alpha =
                1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })
        initActionBar()
    }

    private fun initInsetMargin() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(coordinator) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            toolbarContainer.layoutParams =
                (toolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    //마진이여도 background 의 색은 들어간다?
                    setMargins(0, insets.systemWindowInsetTop, 0, 0)
                }
            collapsingToolbarContainer.layoutParams =
                (collapsingToolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0, 0, 0, 0)
                }

            insets.consumeSystemWindowInsets()
        }
    }

    private fun initActionBar() = with(binding) {
        toolbar.navigationIcon = null
        toolbar.setContentInsetsAbsolute(0, 0)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }

    fun Activity.makeStatusBarTransparent() {
        with(window) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    fun Float.dpToPx(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        )
    }
}