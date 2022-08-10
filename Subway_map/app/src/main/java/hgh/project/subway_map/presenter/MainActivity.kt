package hgh.project.subway_map.presenter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import hgh.project.subway_map.R
import hgh.project.subway_map.databinding.ActivityMainBinding
import hgh.project.subway_map.extension.toGone
import hgh.project.subway_map.extension.toVisible
import hgh.project.subway_map.presenter.stationarrivals.StationArrivalsFragmentArgs

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //app:defaultNavHost="true" 뒤로가기했을데 fragment 뒤로가기 안하면 앱 종료로 인식

    private val navigationController by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainNavigationHostContainer) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        bindViews()
    }

    //appBar 에서 뒤로가기 활성화
    override fun onSupportNavigateUp(): Boolean {
        return navigationController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun initViews(){
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navigationController)
    }

    private fun bindViews(){
        navigationController.addOnDestinationChangedListener { _,destination, argument ->
            if (destination.id == R.id.station_arrivals_dest){
                title = StationArrivalsFragmentArgs.fromBundle(argument!!).station.name //navArgs 가져오기
                binding.toolbar.toVisible()
            }else{
                binding.toolbar.toGone()
            }
        }
    }
}