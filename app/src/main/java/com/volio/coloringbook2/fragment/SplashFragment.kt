package com.volio.coloringbook2.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.adconfigonline.AdHolderOnline
import com.adconfigonline.admob.ads.NewAdmobInterstitial
import com.adconfigonline.untils.CheckInternetConnection
import com.adconfigonline.untils.CheckInternetConnection.InternetCallback
import com.adconfigonline.untils.SplashProgress
import com.volio.coloringbook2.R
import com.volio.coloringbook2.java.util.To

class SplashFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_splash, container, false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Handler().postDelayed({
//            changeFragment()
//        }, 1000)
    }


    private var splashProgress: SplashProgress? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPress()

//        splashProgress = SplashProgress(context, layout_ads)
//        splashProgress?.showProgress(
//            "#ffffff", "#ffffff",
//            "#ffffff", "#000000")
//        loadAD()
    }

    override fun onClickBack() {
        super.onClickBack()
        To.show(R.string.please_wait_)
    }

    private val id = "ca-app-pub-2222869408518511/4310292903"
    private var isShowAD = false

    private fun loadAD() {
        CheckInternetConnection().runToCheckInternet(activity, object : InternetCallback {
            override fun onInernetAvailable() {
                val newAdmobInterstitial = NewAdmobInterstitial()
                newAdmobInterstitial.showAds(activity, id, object : AdHolderOnline.AdHolderCallback {
                    override fun onAdShow(network: String, adtype: String) {
                        splashProgress?.removeSplashProgress()
                        isShowAD = true
                        changeFragment()
//                        val handler = Handler()
//                        handler.postDelayed({ this@SplashFragment.changeFragment() }, 300)
                    }

                    override fun onAdClose(adType: String) {}
                    override fun onAdFailToLoad(messageError: String) {
                        changeFragment()
                    }

                    override fun onAdOff() {}
                })
            }

            override fun onInternetDisconnected(messageError: String) {
                val handler = Handler()
                handler.postDelayed({ changeFragment() }, 1000)
            }
        })

//        AdHolderOnline(requireActivity()).showAds(
//            "Splash",
//            layout_ads,
//            "",
//            object : AdHolderOnline.AdHolderCallback {
//                override fun onAdShow(@AdDef.NETWORK adType: String?, @AdDef.AD_TYPE adtype: String?) {
//                    //WHEN ADS SHOW
//                    splashProgress?.removeSplashProgress()
//                    if (adtype == AdDef.AD_TYPE.NATIVE) {
//                        CountDown().countDownTimer(
//                            requireContext(),
//                            layout_counter
//                        ) { startMenuFragment2() }
//                    } else {
//                        Handler().postDelayed({
//                            startMenuFragment2()
//                        }, 500)
//                    }
//                }
//
//                override fun onAdClose(adType: String) {
//                    //WHEN ADS CLOSE
//                }
//
//                override fun onAdFailToLoad(messageError: String) {
//                    //WHEN AD FAIL TO SHOW: NO INTERNET, NO ADS AVAILABLE, SERVER ERROR
//                    startMenuFragment2()
//                }
//
//                override fun onAdOff() {
//                    //NOTHING TO DO HERE
//                }
//            })
    }

    override fun onResume() {
        super.onResume()
        if (isShowAD) {
            changeFragment()
        }
        Handler().postDelayed({
            changeFragment()
        }, 1000)
    }

    private fun changeFragment() {
        if (view != null) {
            Navigation.findNavController(view!!).navigate(R.id.action_splashFragment_to_menuFragment)
        }
    }


    private fun startMenuFragment2() {
        Navigation.findNavController(view!!).navigate(R.id.action_splashFragment_to_startFragment)
    }


}

