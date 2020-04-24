package com.volio.coloringbook2.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.squareup.otto.Subscribe
import com.volio.alarmoclock.eventbus.EventBus
import com.volio.alarmoclock.eventbus.MessageEvent2
import com.volio.coloringbook2.R
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_mywork.back_category

/**
 * A simple [Fragment] subclass.
 */
class CategoryFragment : Fragment(), OnCustomClickListener, NewImageInterface {

    var newImageInterface: NewImageInterface? = null
    var view1: View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (view1 == null)
            view1 = inflater.inflate(R.layout.fragment_category, container, false)
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            EventBus.bus?.register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        PhotorTool.clickScaleView(tv_name_category, this)
        PhotorTool.clickScaleView(back_category, this)
        PhotorTool.clickScaleView(img_back, this)
        val newImageFragment = NewImageFragment()
        insertFragment(newImageFragment)
//        val categoryFragment =CategoryFragment()
//        categoryFragment.newImageInterface = this
//
//        val fragment = NewImageFragment.newInstance(TypeModel(context!!.resources.getString(R.string.manda), 1))
//        fragment.newImageInterface = this


    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {

            R.id.back_category -> findNavController().popBackStack()
            R.id.img_back -> findNavController().popBackStack()
            R.id.tv_name_category -> findNavController().popBackStack()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.bus?.unregister(this)
    }

    private fun insertFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()
            ?.replace(
                R.id.container2,
                fragment,
                fragment.javaClass.getSimpleName()
            )
            ?.commit()
    }

    @Subscribe
    fun onEvent(event: MessageEvent2) {
        val text = event.message
        if (text == "edit") {
            val url = event.url
            val isFromMain = event.isFromMain
            val isRestart = event.isRestarts
            val bundle = Bundle()
            bundle.putString("url", url)
            bundle.putBoolean("isFromMain", isFromMain)
            bundle.putBoolean("isRestart", isRestart)
            Navigation.findNavController(view!!).navigate(R.id.action_categoryFragment_to_editFragment, bundle)
        }


    }


    private fun goToMainFragment(url: String, isFromMain: Boolean, isRestart: Boolean) {

        val bundle = Bundle()
        bundle.putString("url", url)
        bundle.putBoolean("isFromMain", isFromMain)
        bundle.putBoolean("isRestart", isRestart)
//        Navigation.findNavController(view1!!).navigate(R.id.action_menuFragment_to_editFragment, bundle)
//        Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_editFragment, bundle)


        Navigation.findNavController(view!!).navigate(R.id.action_categoryFragment_to_editFragment, bundle)

//        Navigation.findNavController(view1!!).
    }

    override fun onGotoEdit(url: String, isFromMain: Boolean, isRestart: Boolean) {
        goToMainFragment(url, isFromMain, isRestart)
        Log.i("vcvcvcvcvcvcvcvc", "vao")
    }

}
