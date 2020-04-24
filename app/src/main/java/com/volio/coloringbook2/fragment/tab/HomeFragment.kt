package com.volio.coloringbook2.fragment.tab


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.flyco.tablayout.listener.OnTabSelectListener
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.fragment.BaseFragment
import com.volio.coloringbook2.fragment.NewImageFragment
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(), OnCustomClickListener, NewImageInterface {


    var newImageInterface: NewImageInterface? = null

    override fun onGotoEdit(url: String, isFromMain: Boolean, isRestart: Boolean) {
        newImageInterface?.onGotoEdit(url, isFromMain, isRestart)
    }

    private var interfaces: Tab1Interface? = null

    private val mFragments = mutableListOf<NewImageFragment>()

    fun setListener(interfaces: Tab1Interface?) {
        this.interfaces = interfaces
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (view1 == null) {
            view1 = inflater.inflate(R.layout.fragment_home, container, false)
        }
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabViewPager()
        initClick()
    }

    private fun initClick() {
//        PhotorTool.clickScaleView(img_ad, this)
//        PhotorTool.clickScaleView(txt_discover, this)
    }

    private fun initTabViewPager() {
        mFragments.clear()
        for (type in AppConst.getAllType(context!!)) {
            val fragment = NewImageFragment.newInstance(type)
            fragment.newImageInterface = this
            mFragments.add(fragment)
        }
        val mAdapter = MyPagerAdapter(fragmentManager!!, mFragments, context!!)
        vp.adapter = mAdapter
        tabLayout_10.setViewPager(vp)
        vp.currentItem = 3
        tabLayout_10.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {

            }

            override fun onTabReselect(position: Int) {

            }

        })
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
//            R.id.txt_discover -> interfaces?.clickDiscovery()

        }
    }

    private class MyPagerAdapter(fm: FragmentManager, val mFragments: List<NewImageFragment>, val context: Context) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return AppConst.getAllType(context = context)[position].name
        }

        override fun getItem(position: Int): BaseFragment {
            return mFragments[position]
        }
    }

    interface Tab1Interface {
        fun clickDiscovery()
    }

    companion object {
        var isAdShown: Boolean = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isAdShown) {
            Toast.makeText(context, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
        } else {
            activity?.finish()
        }
    }
}
