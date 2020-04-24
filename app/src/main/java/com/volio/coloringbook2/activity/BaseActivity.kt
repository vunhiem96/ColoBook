package com.volio.coloringbook2.activity


import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.volio.coloringbook2.R
import com.volio.coloringbook2.fragment.BaseFragment
import com.volio.coloringbook2.java.PhotorSharePreUtils

open class BaseActivity : AppCompatActivity(), BaseFragment.ChangeFragmentListener {

    val ANIMATION_NONE = 0
    val ANIMATION_SLIDE_RIGHT_TO_LEFT = 1
    val ANIMATION_SLIDE_TOP_TO_BOTTOM = 2
    val ANIMATION_SLIDE_BOTTOM_TO_TOP = 3
    val ANIMATION_SLIDE_LEFT_TO_RIGHT = 4


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

//    override fun onBackPressed() {
//        val count = supportFragmentManager.backStackEntryCount
//        val fragment = supportFragmentManager.findFragmentById(R.id.mainContainer) as BaseFragment?
//        val tag = fragment?.tag
//        if (Tab1Fragment.isAdShown) {
//            Toast.makeText(this, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
//        } else {
//            if (count != 1) {
//                if (fragment?.tag.equals("EditFragment")) {
//                    fragment?.onBackPressed()
//                } else {
//                    supportFragmentManager.popBackStack()
//                    fragment?.onBackPressed()
//                }
//                return
//            } else {
//                showPopupOrExit()
//            }
//        }
//    }

    private fun showPopupOrExit() {
        val firstTime = PhotorSharePreUtils.getBoolean("hasRate", false)
        if (!firstTime) {
            val fragment = supportFragmentManager.findFragmentById(R.id.mainContainer) as BaseFragment?
            fragment?.showRateApp()
        } else {
            System.exit(0)
        }
    }

    override fun preAddFragment(fragment: BaseFragment, animation: Int) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.add(R.id.mainContainer, fragment, fragment.FRAGMENT_TAG)
        transaction.addToBackStack(fragment.FRAGMENT_TAG)
        transaction.commitAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
    }

    override fun loadFragment(animation: Int) {
    }


    fun setFragment(fragment: BaseFragment, animation: Int) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.add(R.id.mainContainer, fragment, fragment.tag)
        transaction.commitAllowingStateLoss()
    }

    fun setFragment(fragment: BaseFragment, animation: Int, resId: Int) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.add(resId, fragment, fragment.tag)
        transaction.commitAllowingStateLoss()
    }

    override fun addFragment(fragment: BaseFragment, animation: Int) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.add(R.id.mainContainer, fragment, fragment.FRAGMENT_TAG)
        transaction.addToBackStack(fragment.FRAGMENT_TAG)
        transaction.commitAllowingStateLoss()
    }

    override fun addFragmentWithTarget(parent: BaseFragment, child: BaseFragment, animation: Int, fragmentCode: Int) {
        child.setTargetFragment(parent, fragmentCode)
        val transaction = getCustomFragmentTransaction(animation)
        transaction.addToBackStack(child.tag)
        transaction.add(R.id.mainContainer, child, child.tag)
        transaction.commitAllowingStateLoss()
    }

    override fun addFragment(fragment: BaseFragment, animation: Int, resId: Int) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.add(resId, fragment, fragment.tag)
        transaction.addToBackStack(fragment.tag)
        transaction.commitAllowingStateLoss()
    }

    override fun changeFragment(fragment: BaseFragment, animation: Int) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.replace(R.id.mainContainer, fragment, fragment.FRAGMENT_TAG)
        transaction.commitAllowingStateLoss()
    }

    override fun changeFragment(fragment: BaseFragment, animation: Int, resId: Int) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.replace(resId, fragment, fragment.tag)
        transaction.commitAllowingStateLoss()
    }

    override fun replaceChildFragment(fragment: BaseFragment, animation: Int, parentFragment: BaseFragment) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.remove(parentFragment)
        transaction.add(R.id.mainContainer, fragment, fragment.tag)
        transaction.addToBackStack(fragment.tag)
        transaction.commitAllowingStateLoss()
    }

    override fun replaceChildFragment(
        fragment: BaseFragment,
        animation: Int,
        parentFragment: BaseFragment,
        resId: Int
    ) {
        val transaction = getCustomFragmentTransaction(animation)
        transaction.remove(parentFragment)
        transaction.add(resId, fragment, fragment.tag)
        transaction.addToBackStack(fragment.tag)
        transaction.commitAllowingStateLoss()
    }

    override fun popBackStackInclusiveFragment(stackName: String) {

    }

    override fun popBackStackFragment(stackName: String) {
        val fm = supportFragmentManager
        fm.popBackStack(stackName, 0)
    }

    override fun popToRootFragments() {
        val fm = supportFragmentManager
        while (fm.backStackEntryCount > 1) {
            fm.popBackStackImmediate()
        }
    }

    override fun popBackStackFragments() {
        val count = supportFragmentManager.backStackEntryCount
        if (count != 1) {
            supportFragmentManager.popBackStackImmediate()
//            val fragment = supportFragmentManager.findFragmentById(R.id.mainContainer) as BaseFragment?
//            fragment!!.onBackPressed()
            return
        }
    }

    override fun existForegroundFragment(tag: String): Boolean {
        return true
    }

    private fun getCustomFragmentTransaction(animation: Int): FragmentTransaction {
        val transaction = this.supportFragmentManager.beginTransaction()

        when (animation) {
            ANIMATION_NONE -> {
            }
            ANIMATION_SLIDE_RIGHT_TO_LEFT -> transaction.setCustomAnimations(
                R.anim.slide_in_from_right,
                R.anim.slide_out_to_left,
                R.anim.slide_in_from_left,
                R.anim.slide_out_to_right
            )
            ANIMATION_SLIDE_TOP_TO_BOTTOM -> transaction.setCustomAnimations(
                R.anim.slide_in_from_top,
                R.anim.slide_out_to_bottom,
                R.anim.slide_in_from_bottom,
                R.anim.slide_out_to_top
            )
            ANIMATION_SLIDE_BOTTOM_TO_TOP -> transaction.setCustomAnimations(
                R.anim.slide_in_from_bottom,
                R.anim.slide_out_to_top,
                R.anim.slide_in_from_top,
                R.anim.slide_out_to_bottom
            )
            ANIMATION_SLIDE_LEFT_TO_RIGHT -> transaction.setCustomAnimations(
                R.anim.slide_in_from_left,
                R.anim.slide_out_to_right,
                R.anim.slide_in_from_right,
                R.anim.slide_out_to_left
            )
            else -> {
            }
        }

        return transaction
    }
}
