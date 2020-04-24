package com.volio.coloringbook2.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.adconfigonline.AdHolderOnline
import com.adconfigonline.server.AdsChild
import com.adconfigonline.untils.AdDef
import com.bumptech.glide.Glide
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.SharePhotoUntils
import com.volio.coloringbook2.java.util.OnCustomClickListener
import kotlinx.android.synthetic.main.fragment_save.btn_cancel_save
import kotlinx.android.synthetic.main.fragment_save.img_save
import kotlinx.android.synthetic.main.fragment_save.layout_restart_save
import kotlinx.android.synthetic.main.fragment_save.layout_share_save
import kotlinx.android.synthetic.main.fragment_save_share.*
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SaveFragment : BaseFragment(), OnCustomClickListener {


    private var imageUrl: String? = null

    //private var param2: String? = null


//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String) =
//            SaveFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                }
//            }
//    }

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
        return inflater.inflate(R.layout.fragment_save_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConst.isRestartImage = false
        if (imageUrl != null && imageUrl!!.isNotEmpty()) {
            Glide.with(context!!).load(imageUrl).into(img_save)
        }
        initEvent()
    }

    private fun initEvent() {
        PhotorTool.clickScaleView(btn_cancel_save, this)
        PhotorTool.clickScaleView(layout_share_save, this)
        PhotorTool.clickScaleView(layout_restart_save, this)
        PhotorTool.clickScaleView(txt_home, this)
    }


    private fun clickShare() {
        SharePhotoUntils.getInstance().sendShareMore(context, imageUrl)
    }


    private fun clickRestart() {
        AppConst.isRestartImage = true
        findNavController().popBackStack()
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.btn_cancel_save -> findNavController().popBackStack()
            R.id.layout_share_save -> clickShare()
            R.id.layout_restart_save -> {
                val list = Stack<AdsChild>()
                val admob = AdsChild("ca-app-pub-2222869408518511/7472719890",
                    AdDef.NETWORK.GOOGLE,
                    AdDef.GOOGLE_AD_TYPE.INTERSTITIAL,
                    "Default",
                    "Default"
                )
                list.add(admob)
                AdHolderOnline(activity!!).showAdsWithOfflineId(list, "", null, getString(R.string.please_wait_to_load_image), object : AdHolderOnline.AdHolderCallback {
                    override fun onAdFailToLoad(messageError: String?) {
                        clickRestart()
                    }

                    override fun onAdOff() {

                    }

                    override fun onAdShow(network: String?, adtype: String?) {
                        clickRestart()
                    }

                    override fun onAdClose(adType: String?) {

                    }
                })
            }
            R.id.txt_home -> {
                val list = Stack<AdsChild>()
                val admob = AdsChild("ca-app-pub-2222869408518511/9505601888",
                    AdDef.NETWORK.GOOGLE,
                    AdDef.GOOGLE_AD_TYPE.INTERSTITIAL,
                    "Default",
                    "Default"
                )
                list.add(admob)
                AdHolderOnline(activity!!).showAdsWithOfflineId(list, "", null, getString(R.string.please_wait_to_load_image), object : AdHolderOnline.AdHolderCallback {
                    override fun onAdFailToLoad(messageError: String?) {
                        findNavController().popBackStack(R.id.menuFragment, false)
                    }

                    override fun onAdOff() {

                    }

                    override fun onAdShow(network: String?, adtype: String?) {
                        findNavController().popBackStack(R.id.menuFragment, false)
                    }

                    override fun onAdClose(adType: String?) {

                    }
                })

            }
        }
    }
}

interface SaveInterface {
    fun restart()
}

