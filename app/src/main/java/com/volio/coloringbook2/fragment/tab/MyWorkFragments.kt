package com.volio.coloringbook2.fragment.tab


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.FileObserver
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.volio.coloringbook2.R
import com.volio.coloringbook2.adapter.StorySaveAdapter
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.gg
import com.volio.coloringbook2.customview.recyclical.emptyDataSource
import com.volio.coloringbook2.customview.recyclical.setup
import com.volio.coloringbook2.customview.recyclical.withItem
import com.volio.coloringbook2.database.CalendarDao
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.database.SaveStoryDao
import com.volio.coloringbook2.fragment.BaseFragment
import com.volio.coloringbook2.holder.ImageOnWorkHolder
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.java.Lo
import com.volio.coloringbook2.java.PhotorThread
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.SharePhotoUntils
import com.volio.coloringbook2.java.util.OnCustomClickListener
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave
import com.volio.coloringbook2.models.ImageModel
import com.volio.coloringbook2.models.ImageOnWorkModel
import kotlinx.android.synthetic.main.fragment_mywork.*
import org.jetbrains.anko.doAsync
import java.io.File


class MyWorkFragments : BaseFragment(), OnCustomClickListener {
    var imageModel2: List<ImageModel> = ArrayList()
    var storyBook: List<StoryBookSave> = ArrayList()
    lateinit var storyBookSaveAdapter: StorySaveAdapter
    var count = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mywork, container, false)

    }


    private var dao: CalendarDao? = null
    private var daoSaveStory: SaveStoryDao? = null

    var newImageInterface: NewImageInterface? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PhotorTool.clickScaleView(back_my_work, this)
        PhotorTool.clickScaleView(btn_new, this)
        dao = CalendarDatabase.invoke(context!!).calendarDao()
        daoSaveStory = CalendarDatabase.invoke(context!!).saveStoryDao()
        getPercent()
        updateListImage()
        chooseList()
        getList()
        recycle_story.visibility = View.GONE
    }

    fun emptyList(){
        Handler().postDelayed({
            val check =  dataSourceImage.isEmpty()
            if(check == true){
                empty.visibility = View.VISIBLE
            } else{
                empty.visibility = View.GONE
            }
        },200)
    }
    fun emptyStory(){
        Handler().postDelayed({
            val check =  storyBook.size
            if(check == 0){
                empty.visibility = View.VISIBLE
            } else{
                empty.visibility = View.GONE
            }
        },200)
    }

    private fun chooseList() {
        emptyList()
        initRv()
        rdbtn_pick.isChecked = true
        rdbtn_pick.setOnClickListener {
            count =1
            dataSourceImage = emptyDataSource()
            getImage()
            emptyList()
            recycle_my_work.visibility = View.VISIBLE
            recycle_story.visibility = View.GONE
        }
        rdbtn_story.setOnClickListener {
            count =2
            emptyStory()
            initRv()
            recycle_my_work.visibility = View.GONE
            recycle_story.visibility = View.VISIBLE
        }
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

    private fun initRv() {
        recycle_story.layoutManager = GridLayoutManager(context, 3)
        storyBookSaveAdapter = StorySaveAdapter(context!!, storyBook as java.util.ArrayList<StoryBookSave>, object : StorySaveAdapter.ItemClickListener {
            override fun onClick(pos: Int) {
                val bundle = Bundle()
                bundle.putString("idImage", "${storyBook[pos].book_id}")
                bundle.putString("name", "${storyBook[pos].book_name}")
                bundle.putBoolean("mywork", true)
                bundle.putBoolean("isFromMain", true)
                bundle.putBoolean("isRestart", true)
                findNavController().navigate(R.id.action_tab3Fragment_to_pageStoryFragment, bundle)
            }

        }, object : StorySaveAdapter.ItemClickListener2 {

            override fun onClick(pos: Int) {
                storyBookSaveAdapter.notifyItemRemoved(pos)
                DeleteList(storyBook[pos].book_id)
                getList()
                Handler().postDelayed({
                    initRv()
                }, 100)

            }

        })
        recycle_story.adapter = storyBookSaveAdapter
    }

    private fun getImage() {
        PhotorTool.createFolder(AppConst.TEMP_FOLDER);
        PhotorTool.createFolder(AppConst.FOLDER_TEXT_TO_PHOTO);
        recycle_my_work.setup {

            withDataSource(dataSourceImage)
            withLayoutManager(GridLayoutManager(context, 2))

            withItem<ImageOnWorkModel>(R.layout.item_list_image_on_work) {
                onBind(::ImageOnWorkHolder) { index, item ->
                    Glide.with(context!!).load(item.url).diskCacheStrategy(DiskCacheStrategy.NONE).into(img1)
                    val positon = imageModel2.size
                    if (positon != 0) {
                        for (i in 0..positon - 1) {
                            val name = imageModel2[i].name
                            if (name == item.url) {
                                val percent = imageModel2[i].percent
                                val date = imageModel2[i].date
                                val time = imageModel2[i].time
                                txtDate.text = "Date: $date"
                                txtTime.text = "Time: $time"
                                if (percent == 100) {
                                    tv_finish.text = "Finish"
                                } else {
                                    tv_finish.text = "$percent %"
                                }
                            }
                        }

                    }

//                    val percent = items.percent


                    share.setOnClickListener {
                        SharePhotoUntils.getInstance().sendShareMore(context, item.url)
                    }
                    delete.setOnClickListener {
                        deleteImage(item.url)
                        val file = File(item.url)
                        file.delete()
                        context!!.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(item.url))))
                        try {
                            dataSourceImage.removeAt(index)
                            dataSourceImage = emptyDataSource()
                            getImage()
                        }  catch (e: java.lang.Exception) {
                        }
                        emptyList()

                    }
                }

                onClick { index, item ->

                    val bundle = Bundle()
                    bundle.putInt("edit", 1001)
                    bundle.putString("url", item.url)
                    bundle.putBoolean("isFromMain", true)
                    bundle.putBoolean("isRestart", true)
                    findNavController().navigate(R.id.action_tab3Fragment_to_editFragment, bundle)


                }
            }
        }
        recycle_my_work.scrollToPosition(dataSourceImage.size() - 1)

        observer.startWatching()
        observerSaveImage.startWatching()


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

    fun getPercent(): List<ImageModel> {
        var imageModel: ArrayList<ImageModel> = ArrayList()
        var imageModel1: ImageModel? = null
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                imageModel2 = dao!!.getImage()

            }

            override fun onCompleted() {
//                imageModel.add(imageModel1!!)
//                imageModel2 = imageModel

            }

            override fun onCancel() {
            }

        })
        return imageModel2
    }

    fun deleteImage(name: String) {
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                dao!!.deleteImage(name)


            }

            override fun onCompleted() {
            }

            override fun onCancel() {
            }

        })

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
            R.id.btn_new -> {
                if(count == 1) {
                    val bundle = bundleOf("url" to "mandalaa_1")
                    findNavController().navigate(R.id.action_tab3Fragment_to_categoryFragment, bundle)
                } else{
                    findNavController().navigate(R.id.action_tab3Fragment_to_storyBookFragment)
                }
            }

        }
    }

    fun getList(): List<StoryBookSave> {

        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                val x = daoSaveStory!!.getAllStory()
                storyBook = x

            }

            override fun onCompleted() {
//                imageModel.add(imageModel1!!)
//                imageModel2 = imageModel

            }

            override fun onCancel() {
            }

        })
        return storyBook
    }

    fun DeleteList(id: String) {

        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                daoSaveStory!!.deleteById(id)
            }

            override fun onCompleted() {
//                imageModel.add(imageModel1!!)
//                imageModel2 = imageModel

            }

            override fun onCancel() {
            }

        })

    }
}


