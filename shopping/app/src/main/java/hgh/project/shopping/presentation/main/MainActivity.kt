package hgh.project.shopping.presentation.main

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import hgh.project.shopping.R
import hgh.project.shopping.databinding.ActivityMainBinding
import hgh.project.shopping.presentation.BaseActivity
import hgh.project.shopping.presentation.BaseFragment
import hgh.project.shopping.presentation.list.ProductListFragment
import hgh.project.shopping.presentation.profile.ProfileFragment
import org.koin.android.ext.android.inject

internal class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val viewModel by inject<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun observeData() =viewModel.mainStateLiveData.observe(this) {
        when(it){
            is MainState.RefreshOrderList ->{
                binding.bottomNav.selectedItemId = R.id.menu_profile
                val fragment =supportFragmentManager.findFragmentByTag(ProfileFragment.TAG)
                (fragment as? BaseFragment<*,*>)?.viewModel?.fetchData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        //bottomNavigation 설정
        bottomNav.setOnNavigationItemSelectedListener(
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.menu_products -> {
                            showFragment(ProductListFragment(), ProductListFragment.TAG)
                            true
                        }
                        R.id.menu_profile -> {
                            showFragment(ProfileFragment(), ProfileFragment.TAG)
                            true
                        }
                        else -> false
                    }
                }

            }
        )
        showFragment(ProductListFragment(), ProductListFragment.TAG)
    }

    //Fragment 보여주기
    private fun showFragment(fragment: Fragment, tag: String) {
        //fragment 태그로 찾기
        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        //기존 fragment 모두 숨기기
        supportFragmentManager.fragments.forEach { fm ->
            supportFragmentManager.beginTransaction().hide(fm).commit()
        }
        findFragment?.let {
            //fragmentManger 에 해당 fragment 있으면 보이게 하기
            supportFragmentManager.beginTransaction().show(it).commit()
        } ?: kotlin.run {
            //없으면 fragmentManger 에 등록
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, fragment, tag)
                .commitAllowingStateLoss()   //화면 전환시 저장이 안되있으면 버리기(에러 안걸림)
        }

    }

}