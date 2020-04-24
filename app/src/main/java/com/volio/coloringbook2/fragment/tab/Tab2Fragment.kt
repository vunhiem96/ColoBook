package com.volio.coloringbook2.fragment.tab


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.RibbonFactory
import com.volio.coloringbook2.database.CalendarDao
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.fragment.BaseFragment
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.java.Lo
import com.volio.coloringbook2.java.PhotorThread
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.models.CalendarEntry
import kotlinx.android.synthetic.main.fragment_tab2.*
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import java.io.File


class Tab2Fragment : BaseFragment(), OnDateSelectedListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab2, container, false)
    }

    var instance: LocalDate? = null

    private var nameImage1 = "a"
    private var nameImage2 = "c"
    private var nameImage3 = "f"

    var newImageInterface: NewImageInterface? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCalendar()


        layout_image_1.setOnClickListener {
            goToEdit(nameImage1)
        }

        layout_image_2.setOnClickListener {
            goToEdit(nameImage2)
        }

        layout_image_3.setOnClickListener {
            goToEdit(nameImage3)
        }


        ribbonLayout.ribbonHeader = RibbonFactory.getChristmasRibbonHeader(context!!)
    }

    private fun initCalendar() {
        calendar_view.setOnDateChangedListener(this)
        instance = LocalDate.now()
        calendar_view.setSelectedDate(instance)
        val min = LocalDate.of(instance!!.year, Month.JANUARY, 1)
        val max = LocalDate.of(instance!!.year, Month.DECEMBER, 31)
        calendar_view.state().edit().setMinimumDate(min).setMaximumDate(max).commit()
        eventForDate(instance)
        loadAllImage()
    }

    private var listAllImages = arrayListOf<String>()
    private fun loadAllImage() {
        listAllImages = AppConst.listAllImages

        if (PhotorTool.checkHasPermission(activity)) {
            val path = AppConst.TEMP_FOLDER
            val directory = File(path)
            val files = directory.listFiles()
            if (directory.isDirectory) {
                if (files.isEmpty()) {
                } else {
                    for (file in files) {
                        val url = file.absolutePath
                        var s = url
                        s = s?.substring(s.lastIndexOf("/") + 1)
                        val index = s.lastIndexOf(".")
                        if (index != -1) {
                            s = s?.substring(0, index)
                            listAllImages.remove(s)
                        }
                    }
                }
            }
        }
    }

    private fun eventForDate(localDate: LocalDate?) {
        val dao: CalendarDao = CalendarDatabase.invoke(context!!).calendarDao()
        var date2: CalendarEntry? = null
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                date2 = dao.getCalendarFromDate(localDate?.toString())
            }

            override fun onCompleted() {
                if (date2 == null) {
                    //todo database chua co du lieu random 3 image
                    listAllImages.shuffle()
                    val randomSeriesLength = 3
                    val listImageRandom = listAllImages.subList(0, randomSeriesLength)
                    Lo.d("randomsize ${listImageRandom.size}  -  allsize ${listAllImages.size}")
                    Lo.d("random image ${listImageRandom[0]} - ${listImageRandom[1]} - ${listImageRandom[2]} ")
                    if (listImageRandom.size < 3) return
                    nameImage1 = listImageRandom[0]
                    nameImage2 = listImageRandom[1]
                    nameImage3 = listImageRandom[2]
                    val date = CalendarEntry(localDate.toString(), nameImage1, nameImage2, nameImage3)
                    showImage(date)
                    PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
                        override fun doingBackground() {
                            dao.upsertCalendar(date)
                        }

                        override fun onCompleted() {
                        }

                        override fun onCancel() {
                        }

                    })
                } else {
                    showImage(date2!!)
                }

            }

            override fun onCancel() {

            }
        })
    }

    private fun showImage(model: CalendarEntry) {
        nameImage1 = model.img1
        nameImage2 = model.img2
        nameImage3 = model.img3
        if (context != null) {
            Glide.with(context!!).load(AppConst.getIdRawFromName(context!!, nameImage1)).into(img_one_tab_2)
            Glide.with(context!!).load(AppConst.getIdRawFromName(context!!, nameImage2)).into(img_two_tab_2)
            Glide.with(context!!).load(AppConst.getIdRawFromName(context!!, nameImage3)).into(img_three_tab_2)
        }

    }

    private fun goToEdit(name: String) {

        newImageInterface?.onGotoEdit(name, true, false)
//        val fragment = EditFragment.newInstance(name, true, false)
//        mChangeFragmentListener?.addFragment(fragment, 1)
    }

    override fun onDateSelected(p0: MaterialCalendarView, p1: CalendarDay, p2: Boolean) {
        eventForDate(p1.date)
    }

//    private fun clickAdGift() {
//        Tab1Fragment.isAdShown = true
//
//        layout_gift_tab2.visibility = View.VISIBLE
//        Glide.with(context!!).load(R.drawable.giphy).into(ic_gift_gif);
//
//        val mInterstitialAd = InterstitialAd(context!!)
//        mInterstitialAd.adUnitId = context!!.getString(R.string.Admob_HomeGift_Interstitial)
//        mInterstitialAd.loadAd(AdRequest.Builder().build())
//        val handler = Handler()
//        mInterstitialAd.adListener = object : AdListener() {
//            override fun onAdFailedToLoad(p0: Int) {
//                super.onAdFailedToLoad(p0)
//                handler.removeCallbacksAndMessages(null)
//                handler.postDelayed({
//                    layout_gift_tab2.visibility = View.GONE
//                    Tab1Fragment.isAdShown = false
//                    Toast.makeText(context!!, R.string.no_ads, Toast.LENGTH_SHORT).show()
//                }, 2000)
//
//            }
//
//            override fun onAdClosed() {
//                super.onAdClosed()
//                Tab1Fragment.isAdShown = false
//            }
//
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//                mInterstitialAd.show()
//                layout_gift_tab2.visibility = View.GONE
//                handler.removeCallbacksAndMessages(null)
//            }
//        }
//
//        handler.postDelayed({
//            mInterstitialAd.adListener = null
//            Tab1Fragment.isAdShown = false
//            layout_gift_tab2.visibility = View.GONE
//            Toast.makeText(context!!, R.string.no_ads, Toast.LENGTH_SHORT).show()
//        }, 5000
//        )
//    }



    override fun onBackPressed() {
        super.onBackPressed()
        if (HomeFragment.isAdShown) {
            Toast.makeText(context, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
        } else {
            activity?.finish()
        }
    }



}
