package com.volio.coloringbook2.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.volio.coloringbook2.R
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.SharePhotoUntils
import com.volio.coloringbook2.java.util.OnCustomClickListener
import kotlinx.android.synthetic.main.fragment_mywork2.*
import kotlinx.android.synthetic.main.fragment_save.btn_cancel_save
import kotlinx.android.synthetic.main.fragment_save.img_save

private const val ARG_PARAM1 = "param1"


class MyWorkFragment : BaseFragment(), OnCustomClickListener {

    private var imageUrl: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            MyWorkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(ARG_PARAM1)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (view1 == null) {
            isNeedInit = true
            view1 = inflater.inflate(R.layout.fragment_mywork2, container, false)
        }
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNeedInit) {
            if (imageUrl != null && imageUrl!!.isNotEmpty()) {
                Glide.with(context!!).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).into(img_save)
            }
            initEvent()
        }
    }

    private fun initEvent() {
        PhotorTool.clickScaleView(btn_cancel_save, this)
        PhotorTool.clickScaleView(btn_share, this)
        PhotorTool.clickScaleView(btn_restart, this)
        PhotorTool.clickScaleView(btn_save, this)
        PhotorTool.clickScaleView(btn_continue, this)
        PhotorTool.clickScaleView(img_ad, this)

    }


    private fun clickShare() {
        SharePhotoUntils.getInstance().sendShareMore(context, imageUrl)
    }

    override fun onClickBack() {
        super.onClickBack()
        findNavController().popBackStack()
    }


    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.btn_cancel_save -> findNavController().popBackStack()
            R.id.btn_share -> clickShare()
            R.id.btn_restart -> {
                if (imageUrl != null) {

                    if (imageUrl!!.contains("temp")) {

                        val bundle = Bundle()
                        bundle.putString("url", imageUrl)
                        bundle.putBoolean("isFromMain", false)
                        bundle.putBoolean("isRestart", true)
                        findNavController().navigate(R.id.action_myWorkFragment_to_editFragment, bundle)

                        //todo
//                        val fragment = EditFragment.newInstance(imageUrl!!, isFromMain = false, isRestart = true)
//                        mChangeFragmentListener?.addFragment(fragment, 1)


                    } else {
                        Toast.makeText(context!!, getString(R.string.image_success), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.btn_continue -> {
                if (imageUrl != null) {
                    if (imageUrl!!.contains("temp")) {
                        //todo

                        val bundle = Bundle()
                        bundle.putString("url", imageUrl)
                        bundle.putBoolean("isFromMain", false)
                        bundle.putBoolean("isRestart", false)
                        findNavController().navigate(R.id.action_myWorkFragment_to_editFragment, bundle)

//                        val fragment = EditFragment.newInstance(imageUrl!!, isFromMain = false, isRestart = false)
//                        mChangeFragmentListener?.addFragment(fragment, 1)
                    } else {
                        Toast.makeText(context!!, getString(R.string.image_success), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.btn_save -> {
            }


        }
    }
}


