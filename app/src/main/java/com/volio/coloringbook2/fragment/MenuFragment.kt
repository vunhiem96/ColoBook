package com.volio.coloringbook2.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.adconfigonline.AdHolderOnline
import com.adconfigonline.server.AdsChild
import com.adconfigonline.untils.AdDef
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.fragment.tab.HomeFragment
import com.volio.coloringbook2.fragment.tab.MyWorkFragment
import com.volio.coloringbook2.fragment.tab.Tab2Fragment
import com.volio.coloringbook2.fragment.tab.Tab4Fragment
import com.volio.coloringbook2.interfaces.APIService
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.java.Lo
import com.volio.coloringbook2.java.PhotorSharePreUtils
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import com.volio.coloringbook2.java.util.To
import com.volio.coloringbook2.model.ColorBook
import com.volio.coloringbook2.model.ColorBookItem
import com.volio.coloringbook2.viewmodel.ColorBookViewModel
import kotlinx.android.synthetic.main.fragment_menu.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MenuFragment : BaseFragment(), BottomNavigationView.OnNavigationItemSelectedListener, View.OnTouchListener
    , HomeFragment.Tab1Interface, NewImageInterface, OnCustomClickListener {

//    lateinit var viewmodel:ColorBookViewModel
    lateinit var listFragment: ArrayList<BaseFragment>
    lateinit var mAdapter: PagerAdapter
    lateinit var layoutContainer: LinearLayout

    override fun clickDiscovery() {
        view_pager.setCurrentItem(2, true)
//        bottomNavigationView.selectedItemId = R.id.tab3
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v?.id) {
            R.id.one_star -> {
                isChoseStar = true
                isFiveStar = false
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.ic_star_off)
                three_star?.setImageResource(R.drawable.ic_star_off)
                four_star?.setImageResource(R.drawable.ic_star_off)
                five_star?.setImageResource(R.drawable.ic_star_off)
            }
            R.id.two_star -> {
                isChoseStar = true
                isFiveStar = false
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_checked)
                three_star?.setImageResource(R.drawable.ic_star_off)
                four_star?.setImageResource(R.drawable.ic_star_off)
                five_star?.setImageResource(R.drawable.ic_star_off)
            }
            R.id.three_star -> {
                isChoseStar = true
                isFiveStar = false
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_checked)
                three_star?.setImageResource(R.drawable.star_checked)
                four_star?.setImageResource(R.drawable.ic_star_off)
                five_star?.setImageResource(R.drawable.ic_star_off)
            }
            R.id.four_star -> {
                isChoseStar = true
                isFiveStar = false
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_checked)
                three_star?.setImageResource(R.drawable.star_checked)
                four_star?.setImageResource(R.drawable.star_checked)
                five_star?.setImageResource(R.drawable.ic_star_off)

            }
            R.id.five_star -> {
                isChoseStar = true
                isFiveStar = true
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_checked)
                three_star?.setImageResource(R.drawable.star_checked)
                four_star?.setImageResource(R.drawable.star_checked)
                five_star?.setImageResource(R.drawable.star_checked)

            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBackPress()

    }


    private fun actionExitApp() {
        val list = Stack<AdsChild>()
        val admob = AdsChild("ca-app-pub-2222869408518511/9245398733",
            AdDef.NETWORK.GOOGLE,
            AdDef.GOOGLE_AD_TYPE.INTERSTITIAL,
            "Default",
            "Default"
        )
        list.add(admob)
        AdHolderOnline(activity!!).showAdsWithOfflineId(list, "", null, getString(R.string.please_wait_to_save), object : AdHolderOnline.AdHolderCallback {
            override fun onAdFailToLoad(messageError: String?) {
                showExitFragment()
            }

            override fun onAdOff() {

            }

            override fun onAdShow(network: String?, adtype: String?) {
                isShowAd = true
            }

            override fun onAdClose(adType: String?) {
//                showExitFragment()
            }
        })
    }

    private var isShowAd = false

    override fun onResume() {
        super.onResume()

        if (isShowAd) {
            showExitFragment()
        }

    }



    private fun showExitFragment() {
        findNavController().navigate(R.id.action_menuFragment_to_exitFragment)
    }


    @SuppressLint("StringFormatInvalid")
    private fun clickFeedback() {
        val builder: AlertDialog.Builder?
        val inflater = this.layoutInflater
        builder = AlertDialog.Builder(context!!)
        val logoutDialog = inflater.inflate(R.layout.dialog_feedback, null)
        val btnCancel: ImageView = logoutDialog.findViewById(R.id.btn_close_dialog)
        val edText: EditText = logoutDialog.findViewById(R.id.ed_text_feedback)
        val layoutSend: RelativeLayout = logoutDialog.findViewById(R.id.layout_send_feedback)

        builder.setView(logoutDialog)
        builder.setCancelable(true)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        PhotorTool.clickScaleView(layoutSend) { _, _ ->
            val text = edText.text.toString()
            try {
                PhotorTool.sendEmailMoree(context, arrayOf(AppConst.EMAIL_FEEDBACK),
                    getString(R.string.feed_back_color_book), text)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onGotoEdit(url: String, isFromMain: Boolean, isRestart: Boolean) {
        goToMainFragment(url, isFromMain, isRestart)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tab1 -> {
                if (HomeFragment.isAdShown) {
                    Toast.makeText(context, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
                } else {
                    view_pager.setCurrentItem(0, false)
                }
            }
//            R.id.tab2 -> {
//                if (Tab1Fragment.isAdShown) {
//                    Toast.makeText(context, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
//                } else {
//                    view_pager.setCurrentItem(1, false)
//                }
//            }

            R.id.tab3 -> {
                if (HomeFragment.isAdShown) {
                    Toast.makeText(context, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
                } else {
                    tab3.updateListImage()
                    view_pager.setCurrentItem(2, false)
                }
            }
            R.id.tab4 -> {
                if (HomeFragment.isAdShown) {
                    Toast.makeText(context, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
                } else {
                    view_pager.setCurrentItem(3, false)
                }
            }
        }
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (view1 == null)
            view1 = inflater.inflate(R.layout.fragment_menu, container, false)
        return view1
    }

    private lateinit var tab3: MyWorkFragment
    private var tab2: BaseFragment? = null
    private lateinit var tab4: BaseFragment

    private var isInit = false
    private var load = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PhotorTool.clickScaleView(rl_setting, this)
        PhotorTool.clickScaleView(rl_mywork, this)
        PhotorTool.clickScaleView(rl_story_book, this)
//        viewmodel = ViewModelProviders.of(this).get(ColorBookViewModel::class.java)

//        viewmodel.labels.value = "haha"
//        viewmodel.labels.observe(viewLifecycleOwner, object : Observer<String> {
//            override fun onChanged(t: String?) {
//                Log.i("fgfgfgfgfgfgfgfgvc","$t")
//            }
//        })
        if (!isInit) {
            isInit = true
            layoutContainer = LinearLayout(context)
            loadMenuFragment()
        }
    }



    var doubleBackToExitPressedOnce = false
    override fun onClickBack() {
        super.onClickBack()
        val firstTime = PhotorSharePreUtils.getBoolean("hasRate", false)
        if (firstTime) {
            if (doubleBackToExitPressedOnce) {
                activity!!.finish()

                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(context!!, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else {
            clickRate()

        }


    }

//    private fun showPopupOrExit() {
//        val firstTime = PhotorSharePreUtils.getBoolean("hasRate", false)
//        if (!firstTime) {
//            activity!!.finish()
//        } else {
//            clickRate()
//
//        }
//    }

    private fun goToMainFragment(url: String, isFromMain: Boolean, isRestart: Boolean) {
        val bundle = Bundle()
        bundle.putString("url", url)
        bundle.putBoolean("isFromMain", isFromMain)
        bundle.putBoolean("isRestart", isRestart)
//        Navigation.findNavController(view1!!).navigate(R.id.action_menuFragment_to_editFragment, bundle)
//        Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_editFragment, bundle)

        val id1 = Navigation.findNavController(view1!!).currentDestination?.id
        Lo.d("id1 $id1")
        Navigation.findNavController(view!!).navigate(R.id.action_menuFragment_to_editFragment, bundle)

//        Navigation.findNavController(view1!!).
    }


    private fun loadMenuFragment() {

        load = false
//        bottomNavigationView.itemIconTintList = null
        tab3 = MyWorkFragment()
        tab3.newImageInterface = this
        tab4 = Tab4Fragment()
        val tab1 = HomeFragment()
        tab1.newImageInterface = this
        tab1.setListener(this)

        val tab2 = Tab2Fragment()
        tab2.newImageInterface = this
        listFragment = arrayListOf(tab1, tab2,
            tab3, tab4)

        mAdapter = MyPagerAdapter(fragmentManager!!, listFragment)
        view_pager.adapter = mAdapter

        view_pager.setCurrentItem(0, false)
//        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        try {
            view_pager.offscreenPageLimit = 4
        } catch (e: IllegalStateException) {
        }
    }

//    override fun showRateApp() {
//        super.showRateApp()
//        clickRate()
//    }

    private var isFiveStar = false
    private var isChoseStar = false
    private var one_star: ImageView? = null
    private var two_star: ImageView? = null
    private var three_star: ImageView? = null
    private var four_star: ImageView? = null
    private var five_star: ImageView? = null
    private var dialog: AlertDialog? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun clickRate() {

        isFiveStar = false
        isChoseStar = false
        val builder: AlertDialog.Builder?
        val inflater = this.layoutInflater
        builder = AlertDialog.Builder(context!!)
        val logoutDialog = inflater.inflate(R.layout.dialog_rate_app, null)
        val btnCancel: ImageView = logoutDialog.findViewById(R.id.btn_close_dialog)
        val btnLater: RelativeLayout = logoutDialog.findViewById(R.id.btn_later)

        val layoutSend: RelativeLayout = logoutDialog.findViewById(R.id.layout_send_rate)
        one_star = logoutDialog.findViewById(R.id.one_star)
        two_star = logoutDialog.findViewById(R.id.two_star)
        three_star = logoutDialog.findViewById(R.id.three_star)
        four_star = logoutDialog.findViewById(R.id.four_star)
        five_star = logoutDialog.findViewById(R.id.five_star)
        one_star?.setOnTouchListener(this)
        two_star?.setOnTouchListener(this)
        three_star?.setOnTouchListener(this)
        four_star?.setOnTouchListener(this)
        five_star?.setOnTouchListener(this)


        builder.setView(logoutDialog)
        builder.setCancelable(true)
        dialog = builder.create()
        dialog?.show()
        dialog?.setCancelable(true)
        btnCancel.setOnClickListener {
            dialog?.dismiss()

//            PhotorSharePreUtils.putBoolean("hasRate", false)
//            exitProcess(0)
        }
        layoutSend.setOnClickListener {
            if (isChoseStar) {
                PhotorSharePreUtils.putBoolean("hasRate", true)
                if (isFiveStar) {
                    PhotorTool.openMarket(context, context?.packageName)
                    To.show("Please rate us 5 start")
                } else {
                    dialog?.dismiss()
                    clickFeedback()
                }
            } else {
                To.show("Please select the star above to rate the application")
            }
        }
        btnLater.setOnClickListener {
            dialog?.dismiss()
            activity!!.finish()
//            actionExitApp()
        }
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun rateApp() {
        PhotorSharePreUtils.putBoolean("hasRate", true)
        PhotorTool.openMarket(context, context?.packageName)
        dialog?.dismiss()
    }

    private class MyPagerAdapter(fm: FragmentManager, val mFragments: List<BaseFragment>) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getItem(position: Int): BaseFragment {
            return mFragments[position]
        }
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.rl_setting -> findNavController().navigate(R.id.action_menuFragment_to_tab4Fragment)
            R.id.rl_mywork -> findNavController().navigate(R.id.action_menuFragment_to_tab3Fragment)
            R.id.rl_story_book -> findNavController().navigate(R.id.action_menuFragment_to_storyBookFragment)


        }
    }


}

