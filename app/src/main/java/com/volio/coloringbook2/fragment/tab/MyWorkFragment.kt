package com.volio.coloringbook2.fragment.tab


import android.Manifest
import android.os.Bundle
import android.os.FileObserver
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.customview.recyclical.emptyDataSource
import com.volio.coloringbook2.customview.recyclical.setup
import com.volio.coloringbook2.customview.recyclical.withItem
import com.volio.coloringbook2.database.CalendarDao
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.fragment.BaseFragment
import com.volio.coloringbook2.holder.ImageOnWorkHolder
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.java.Lo
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import com.volio.coloringbook2.models.ImageOnWorkModel
import kotlinx.android.synthetic.main.fragment_mywork.*
import org.jetbrains.anko.doAsync
import java.io.File


class MyWorkFragment : BaseFragment(), OnCustomClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mywork, container, false)
    }


    private var dao: CalendarDao? = null

    var newImageInterface: NewImageInterface? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PhotorTool.clickScaleView(back_my_work, this)
        dao = CalendarDatabase.invoke(context!!).calendarDao()
    }

    private var dataSourceImage = emptyDataSource()


    private var observer: FileObserver = object : FileObserver(AppConst.TEMP_FOLDER) {
        override fun onEvent(event: Int, file: String?) {
            if (event == CREATE) {
                activity?.runOnUiThread {
                    dataSourceImage.insert(0, ImageOnWorkModel(url = AppConst.TEMP_FOLDER + file, percent = 1))
                }
            } else if (event == FileObserver.MODIFY) {
            }
        }
    }

    private var observerSaveImage: FileObserver = object : FileObserver(AppConst.FOLDER_TEXT_TO_PHOTO) {
        override fun onEvent(event: Int, file: String?) {
            if (event == CREATE) {
                activity?.runOnUiThread {
                    dataSourceImage.insert(0, ImageOnWorkModel(url = AppConst.FOLDER_TEXT_TO_PHOTO + file, percent = 100))
                }
            }
        }
    }


    private fun getImage() {
        PhotorTool.createFolder(AppConst.TEMP_FOLDER);
        PhotorTool.createFolder(AppConst.FOLDER_TEXT_TO_PHOTO);


        recycle_my_work.setup {
            withDataSource(dataSourceImage)
            withLayoutManager(
                GridLayoutManager(context, 2)
            )
            withItem<ImageOnWorkModel>(R.layout.item_list_image_on_work) {
                onBind(::ImageOnWorkHolder) { index, item ->
                    Glide.with(context!!).load(item.url).diskCacheStrategy(DiskCacheStrategy.NONE).into(img1)
                    var percent = item.percent
                    Lo.d("dsk", "percent $percent")
                    if (percent == 0) percent = 1
                    txtPercent.text = "$percent/100"
                    if (percent != null) {
                        if (percent > 100) percent = 100
                        progressBar.progress = percent
                        if (percent == 100) {
                            imgDone.setImageResource(R.drawable.ic_tick)
                        } else {
                            imgDone.setImageResource(R.color.tranparent)
                        }
                    }
                }

                onClick { index, item ->
                    val bundle = Bundle()
                    bundle.putString("param1", item.url)
                    Navigation.findNavController(view!!).navigate(R.id.action_menuFragment_to_myWorkFragment, bundle)

//                    val fragment = MyWorkFragment.newInstance(item.url)
//                    mChangeFragmentListener?.addFragment(fragment, 1)
                }
            }
        }


        observer.startWatching()
        observerSaveImage.startWatching()

//        val thread = Thread {
//            val path = AppConst.TEMP_FOLDER
//            val directory = File(path)
//            val files = directory.listFiles()
//            if (directory.isDirectory) {
//                if (files.isEmpty()) {
//                    getCompleteImage()
//                } else {
//                    for (file in files) {
//                        var s = file.absolutePath
//                        s = s?.substring(s.lastIndexOf("/") + 1)
//                        s = s?.substring(0, s.lastIndexOf("."))
//                        val percent = dao?.getPercentImage(s)
//                        Lo.d("percent  $percent")
//                        activity?.runOnUiThread {
//                            dataSourceImage.add(ImageOnWorkModel(url = file.absolutePath, percent = percent))
//                        }
//                    }
//                    getCompleteImage()
//                }
//
//            }
//        }
//        thread.start()

        doAsync {
            val path = AppConst.TEMP_FOLDER
            val directory = File(path)
            val files = directory.listFiles()
            if (directory.isDirectory) {
                if (files.isEmpty()) {
                    getCompleteImage()
                } else {
                    for (file in files) {
                        var s = file.absolutePath
                        s = s?.substring(s.lastIndexOf("/") + 1)
                        s = s?.substring(0, s.lastIndexOf("."))
                        val percent = dao?.getPercentImage(s)
                        Lo.d("percent  $percent")
                        activity?.runOnUiThread {
                            dataSourceImage.add(ImageOnWorkModel(url = file.absolutePath, percent = percent))
                        }
                    }
                    getCompleteImage()
                }

            }
        }
    }


    private fun getCompleteImage() {
        val path = AppConst.FOLDER_TEXT_TO_PHOTO
        val directory = File(path)
        val files = directory.listFiles()
        if (directory.isDirectory) {
            if (files.isEmpty()) return
            for (file in files) {
                val s = file.absolutePath
                if (s.contains("png") || s.contains("jpg")) {
                    activity?.runOnUiThread {
                        dataSourceImage.add(ImageOnWorkModel(url = file.absolutePath, percent = 100))
                    }
                }
            }
        }
    }


    var first = true

    fun updateListImage() {
        if (first) {
            first = false
            if (activity != null) {
                if (PhotorTool.checkHasPermission(activity!!)) {
                    getImage()
                } else {
                    askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        if (it.isAccepted) {
                            getImage()
                        }
                    }.onDeclined { e ->
                        //                    To.show("k chap nhan")
                    }
                }
            }


        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (HomeFragment.isAdShown) {
            Toast.makeText(context, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
        } else {
            activity?.finish()
        }
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.back_my_work -> findNavController().popBackStack()

        }
    }

}
