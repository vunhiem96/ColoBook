package com.volio.coloringbook2.fragment

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import com.adconfigonline.AdHolderOnline
import com.adconfigonline.untils.AdDef
import com.volio.coloringbook2.common.gone
import com.volio.coloringbook2.interfaces.OnAdCallback
import com.volio.coloringbook2.java.PhotorDialog
import me.yokeyword.swipebackfragment.SwipeBackFragment


open class BaseFragment : SwipeBackFragment() {

    var view1: View? = null
    var isNeedInit = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setBackPress()
    }

    fun setBackPress() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onClickBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    open fun onClickBack() {


    }

    open fun isSafe(): Boolean {
        return !(this.isRemoving || this.activity == null || this.isDetached || !this.isAdded || this.view == null)
    }



    open fun loadBannerNative(space: String, layout_ads: LinearLayout?) {
        AdHolderOnline(requireActivity()).showAds(
            space,
            layout_ads,
            "",
            object : AdHolderOnline.AdHolderCallback {
                override fun onAdShow(@AdDef.NETWORK adType: String?, @AdDef.AD_TYPE adtype: String?) {
                    //WHEN ADS SHOW

                }

                override fun onAdClose(adType: String) {
                    //WHEN ADS CLOSE
                }

                override fun onAdFailToLoad(messageError: String) {
                    //WHEN AD FAIL TO SHOW: NO INTERNET, NO ADS AVAILABLE, SERVER ERROR
                    layout_ads?.gone()
                }

                override fun onAdOff() {
                    //NOTHING TO DO HERE
                }
            })
    }


    open fun loadIntertital(space: String, idString: Int, callback: OnAdCallback?) {
        AdHolderOnline(requireActivity()).showAds(
            space,
            null, getString(idString),
            object : AdHolderOnline.AdHolderCallback {
                override fun onAdShow(@AdDef.NETWORK adType: String?, @AdDef.AD_TYPE adtype: String?) {
                    //WHEN ADS SHOW
//                    Handler().postDelayed({
//                        callback?.actionShow()
//                    }, 500)
                    callback?.actionShow()
                }

                override fun onAdClose(adType: String) {
                    //WHEN ADS CLOSE
                }

                override fun onAdFailToLoad(messageError: String) {
                    //WHEN AD FAIL TO SHOW: NO INTERNET, NO ADS AVAILABLE, SERVER ERROR
                    callback?.actionShow()
                }

                override fun onAdOff() {
                    //NOTHING TO DO HERE
                }
            })
    }


    private var mLastClickTime = 0L
    open fun canTouch(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }


    open fun showDialog(id: Int) {
        PhotorDialog.getInstance().showLoadingWithMessage(activity, getString(id))
    }

    open fun hideDialog() {
        PhotorDialog.getInstance().hideLoading()
    }


    var mChangeFragmentListener: ChangeFragmentListener? = null

    val FRAGMENT_TAG: String = javaClass.simpleName

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ChangeFragmentListener)
            mChangeFragmentListener = context
    }

    override fun onDetach() {
        super.onDetach()
        mChangeFragmentListener = null
    }

    open fun onBackPressed() {

    }

    open fun setTextColor(color: Int) {

    }

    open fun backInMain() {


    }

    open fun showRateApp() {

    }

    override fun onDestroy() {
        try {
            val mSwipeBackLayout = SwipeBackFragment::class.java.getDeclaredField("mSwipeBackLayout")
            mSwipeBackLayout.isAccessible = true
            mSwipeBackLayout.set(this, null)
        } catch (e: NoSuchFieldException) {

            e.printStackTrace()
        } catch (e: IllegalAccessException) {

            e.printStackTrace()
        }

        try {
            val mNoAnim = SwipeBackFragment::class.java.getDeclaredField("mNoAnim")
            mNoAnim.isAccessible = true
            mNoAnim.set(this, null)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    open interface ChangeFragmentListener {
        fun addFragment(fragment: BaseFragment, animation: Int)

        fun addFragmentWithTarget(parent: BaseFragment, child: BaseFragment, animation: Int, fragmentCode: Int)

        fun addFragment(fragment: BaseFragment, animation: Int, resId: Int)

        fun changeFragment(fragment: BaseFragment, animation: Int)

        fun changeFragment(fragment: BaseFragment, animation: Int, resId: Int)

        fun replaceChildFragment(fragment: BaseFragment, animation: Int, parentFragment: BaseFragment)

        fun replaceChildFragment(fragment: BaseFragment, animation: Int, parentFragment: BaseFragment, resId: Int)

        fun popBackStackInclusiveFragment(stackName: String)

        fun popBackStackFragment(stackName: String)

        fun popToRootFragments()

        fun popBackStackFragments()

        fun existForegroundFragment(tag: String): Boolean

        fun preAddFragment(fragment: BaseFragment, animation: Int)

        fun loadFragment(animation: Int)
    }
}
