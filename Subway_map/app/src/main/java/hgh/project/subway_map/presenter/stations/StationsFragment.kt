package hgh.project.subway_map.presenter.stations

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hgh.project.subway_map.databinding.FragmentStationsBinding
import hgh.project.subway_map.domain.Station
import hgh.project.subway_map.extension.toGone
import hgh.project.subway_map.extension.toVisible
import org.koin.android.scope.ScopeFragment

class StationsFragment: ScopeFragment(), StationsContract.View {

    override val presenter: StationsContract.Presenter by inject()

    private var binding: FragmentStationsBinding? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentStationsBinding.inflate(inflater, container, false)
        .also { binding =it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        bindViews()
        presenter.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        presenter.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showLoadingIndicator() {
        binding?.progressBar?.toVisible()
    }

    override fun hideLoadingIndicator() {
        binding?.progressBar?.toGone()
    }

    override fun showStations(stations: List<Station>) {
        (binding?.recyclerView?.adapter as? StationsAdapter)?.run {
            this.data = stations
            notifyDataSetChanged()
        }
    }

    private fun initViews() {
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = StationsAdapter()
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))    //recyclerView 구분선 만들기
        }
    }

    private fun bindViews() {
        //검색
        binding?.searchEditText?.addTextChangedListener { editable ->
            presenter.filterStations(editable.toString())
        }

        (binding?.recyclerView?.adapter as? StationsAdapter)?.apply {
            onItemClickListener = { station ->       //navigation 액션 실행(stationArrivals 창으로 이동)
                val action = StationsFragmentDirections.toStationArrivalsAction(station) //intent 없이 데이터 전달
                findNavController().navigate(action)
            }
            onFavoriteClickListener = { station ->
                presenter.toggleStationFavorite(station)
            }
        }
    }

    private fun hideKeyboard(){
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }
}