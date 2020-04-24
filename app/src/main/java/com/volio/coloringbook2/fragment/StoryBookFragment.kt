package com.volio.coloringbook2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import com.volio.coloringbook2.R
import com.volio.coloringbook2.java.Lo
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import kotlinx.android.synthetic.main.fragment_story_book.*


/**
 * A simple [Fragment] subclass.
 */
class StoryBookFragment : BaseFragment(), OnCustomClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_story_book, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PhotorTool.clickScaleView(back_storybook, this)
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.back_storybook -> findNavController().popBackStack()



        }
    }
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
}
