package com.volio.coloringbook2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.volio.coloringbook2.R
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPress()
//        preloadBanner(layout_ads)

//        loadBannerNative("Start", layout_ads)


        btn_start.setOnClickListener {
            //            mChangeFragmentListener?.changeFragment(MenuFragment(), 0)
            Navigation.findNavController(view).navigate(R.id.action_startFragment_to_menuFragment)

        }
    }

    override fun onClickBack() {
        super.onClickBack()
        Navigation.findNavController(view!!).navigate(R.id.action_startFragment_to_menuFragment)
    }


}

