package com.volio.coloringbook2.fragment


import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.gson.Gson
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.AppConst.TEMP_FOLDER
import com.volio.coloringbook2.common.gg
import com.volio.coloringbook2.common.gone
import com.volio.coloringbook2.common.setImageTint
import com.volio.coloringbook2.customview.ColourImageView
import com.volio.coloringbook2.customview.photoview.PhotoViewAttacher
import com.volio.coloringbook2.database.CalendarDao
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.database.SaveStoryDao
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.interfaces.ColorInterfaces
import com.volio.coloringbook2.interfaces.ImageInterface
import com.volio.coloringbook2.java.*
import com.volio.coloringbook2.java.util.OnCustomClickListener
import com.volio.coloringbook2.model.storybook.StoryBook
import com.volio.coloringbook2.model.storybook.saveLocal.ImageStorySave
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave
import com.volio.coloringbook2.models.ImageModel
import com.volio.coloringbook2.models.UltraPagerAdapter
import com.zxy.tiny.Tiny
import io.github.hyuwah.draggableviewlib.makeDraggable
import kotlinx.android.synthetic.main.dialog_chose_color.*
import kotlinx.android.synthetic.main.fragment_edit.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "url"
private const val ARG_PARAM2 = "isFromMain"
private const val ARG_PARAM3 = "isRestart"

class EditFragment : BaseFragment(), OnCustomClickListener, SaveInterface, ImageInterface, ColorInterfaces {
    var storyBook: List<StoryBookSave> = ArrayList()
    private var edit = -1
    private var idList = -1
    private var idImage = -1
    private var imageUrl: String? = null
    private var isFromMain = false
    private var imageName: String? = null
    private var isRestart = false
    private var isStoryBook = false
    private var isMywork = false
    private var dao: CalendarDao? = null
    private var storyBookdao: SaveStoryDao? = null
    private var idBook: String? = null
    var imageSave: List<ImageModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(ARG_PARAM1)
            isFromMain = it.getBoolean(ARG_PARAM2)
            isRestart = it.getBoolean(ARG_PARAM3)
            idList = it.getInt("idList", -1)
            idImage = it.getInt("idImg", -1)
            edit = it.getInt("edit", -1)
            isStoryBook = it.getBoolean("story", false)
            isMywork = it.getBoolean("mywork", false)
            idBook = it.getString("idStorybook")

        }

        setBackPress()
//        if (isStoryBook == true && isMywork == false) {
//            val apiJson = context!!.config.storyBook
//            val gson = Gson()
//            val listStoryBook = gson.fromJson(apiJson, StoryBook::class.java)
//            val url = listStoryBook[idList].book_id
//
//        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isNeedInit = false
        if (view1 == null) {
            isNeedInit = true
            view1 = inflater.inflate(R.layout.fragment_edit, container, false)
        }
        return view1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Glide.with(context!!).asBitmap().load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgPreView)
        dao = CalendarDatabase.invoke(context!!).calendarDao()
        storyBookdao = CalendarDatabase.invoke(context!!).saveStoryDao()
        if (AppConst.isRestartImage) {
            AppConst.isRestartImage = false
            restart()
        }

        if (idList != -1 && isMywork == false) {
            val apiJson = context!!.config.storyBook
            val gson = Gson()
            val listStoryBook = gson.fromJson(apiJson, StoryBook::class.java)
            val urlBase = "http://mycat.asia/volio_colorbook/"
            val url = listStoryBook[idList].list[idImage].image_url
            imageUrl = "$urlBase$url"
            val id2 = listStoryBook[idList].book_id
            getList2("$id2")

        }
        if (idList != -1 && isMywork == true) {
            getList2(idBook!!)

            Handler().postDelayed({
                imageUrl = storyBook[0].list[idImage].image_url
                gg("vcvcvcvcvcvcvcvc", "$idBook")
                initView()
            }, 100)
        }




        if (!isNeedInit) return
        initEvent()
        initView()
        PhotorTool.createFolder(TEMP_FOLDER)
        initViewPager()
        imageView?.onRedoUndoListener = object : ColourImageView.OnRedoUndoListener {
            override fun onRedoUndo(undoSize: Int, redoSize: Int) {
                if (undoSize != 0) {
                    btn_back_image.alpha = 1F
                    btn_back_image.isEnabled = true
                } else {
                    btn_back_image.alpha = 0.5F
                    btn_back_image.isEnabled = false
                }
                if (redoSize != 0) {
                    btn_forward_image.alpha = 1F
                    btn_forward_image.isEnabled = true
                } else {
                    btn_forward_image.alpha = 0.5F
                    btn_forward_image.isEnabled = false
                }
            }
        }
        if (isStoryBook == true) {

//            Handler().postDelayed({
//             loadImage()
//            },100)

        }

    }


    private var adapter: PagerAdapter? = null
    private fun initViewPager() {
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        adapter = UltraPagerAdapter(true, AppConst.listAllColor, this)
        ultraViewPager.adapter = adapter
        ultraViewPager.setMultiScreen(0.75f)
        ultraViewPager.setAutoMeasureHeight(false)
        ultraViewPager.setPageTransformer(false, UltraDepthScaleTransformer())
        ultraViewPager.setInfiniteLoop(false)
    }

    override fun pickColor(color: String) {

        imageView?.setColor(Color.parseColor(color))
        img_current_color.setImageTint(Color.parseColor(color))
    }

    override fun onClickBack() {
        super.onClickBack()
        Lo.d("onClickBack")
        PhotorDialog.getInstance().showDialogConfirm(activity, R.string.end_edit, R.string.no, R.string.yes, true,
            { dialog, _ ->
                //click no
                dialog.dismiss()
            }, { dialog, _ ->
                //click yes
                dialog.dismiss()
                saveCurrentImage()
            }
        )
//        Navigation.findNavController(view!!).popBackStack()
    }

    private var isSaveImaged = false

    private fun saveImage() {
//        showDialog(R.string.please_wait_to_save)
        if (isStoryBook == false) {
            if (imageView != null) {
                val bitmap = imageView?.getBitmap() ?: return
                ViewToBitmap.of(imageView2).toJPG().setOnSaveResultListener { isSaved, path ->
                    if (isSaved && path != null && path.isNotEmpty()) {
                        isSaveImaged = true
                        if (isSafe()) {
                            var size = imageView?.size()!!.toInt()
                            if (size > 100) {
                                size = 100
                            }
                            val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            val image = ImageModel(path, imageUrl!!, 0, 0, size, 0, currentDate, currentTime)
                            saveImageDao(image)
                            nextFragment(path)
                        }
                    }
                }.toJPG().save(context!!)

//                ViewToBitmap.of(bitmap).setOnSaveResultListener { isSaved, path ->
//                    if (isSaved && path != null && path.isNotEmpty()) {
//                        isSaveImaged = true
//                        if (isSafe()) {
//                            var size = imageView?.size()!!.toInt()
//                            if (size > 100) {
//                                size = 100
//                            }
//                            val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
//                            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
//                            val image = ImageModel(path,imageUrl!!, 0, 0, size, 0, currentDate, currentTime)
//                            saveImageDao(image)
//                            nextFragment(path)
//                        }
//                    }
//                }.toJPG().saveBitmap(context!!)


            } else {
                Toast.makeText(context, getString(R.string.st_went_wrong), Toast.LENGTH_SHORT).show()
            }
        } else {
            if (imageView != null) {
                val bitmap = imageView?.getBitmap() ?: return
                ViewToBitmap.of(imageView2).setOnSaveResultListener { isSaved, path ->
                    if (isSaved && path != null && path.isNotEmpty()) {
                        isSaveImaged = true
                        if (isSafe()) {
                            val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                            val apiJson = context!!.config.storyBook
                            val gson = Gson()
                            val listStoryBook = gson.fromJson(apiJson, StoryBook::class.java)
                            val urlBase = "http://mycat.asia/volio_colorbook/"
                            val url = listStoryBook[idList].list[idImage].image_url
                            imageUrl = "$urlBase$url"

                            var book_image_url = ""
                            var book_name = ""
                            var is_pro = ""
                            var priority = ""
                            var id = "12"
                            if (isMywork == false) {
                                id = listStoryBook[idList].book_id
                                book_image_url = listStoryBook[idList].book_image_url
                                book_name = listStoryBook[idList].book_name
                                is_pro = listStoryBook[idList].is_pro
                                priority = listStoryBook[idList].priority
                            } else {
                                id = idBook!!
                                book_image_url = storyBook[0].book_image_url
                                book_name = storyBook[0].book_name
                                is_pro = storyBook[0].is_pro
                                priority = storyBook[0].priority
                            }


//                            val book_id: String,
//                            val book_image_url: String,
//                            val book_name: String,
//                            val is_pro: String,
//                            val list: List<ImageStorySave>,
//                            val priority: String
                            val listImage: ArrayList<ImageStorySave> = ArrayList()
                            gg("vcvcvcvfgfgfgfgfg", "insert $storyBook")
                            if (storyBook.size == 0) {
                                for (i in 0..listStoryBook[idList].list.size - 1) {
                                    if (i != idImage) {
                                        val imageId = listStoryBook[idList].list[i].image_id
                                        val image_url1 = listStoryBook[idList].list[i].image_url
                                        val image_url = "$urlBase$image_url1"
                                        val priorityImage = listStoryBook[idList].list[i].priority
                                        val thumbnail_url = listStoryBook[idList].list[i].thumbnail_url
                                        listImage.add(ImageStorySave(id, imageId, image_url, priorityImage, thumbnail_url, false))
                                    } else {
                                        val imageId = listStoryBook[idList].list[idImage].image_id
                                        val priorityImage = listStoryBook[idList].list[idImage].priority
                                        val thumbnail_url = listStoryBook[idList].list[idImage].thumbnail_url
                                        listImage.add(ImageStorySave(id, imageId, path, priorityImage, thumbnail_url, true))


                                    }
                                }
                                val storybook = StoryBookSave(id, book_image_url, book_name, is_pro, listImage, priority, currentDate, currentTime)
                                gg("vcvcvcvfgfgfgfgfg", "insert $storybook")
                                saveStory(storybook)
                            } else {
                                for (i in 0..storyBook[0].list.size - 1) {
                                    if (i != idImage) {
                                        val imageId = storyBook[0].list[i].image_id
                                        val image_url = storyBook[0].list[i].image_url
                                        val priorityImage = storyBook[0].list[i].priority
                                        val thumbnail_url = storyBook[0].list[i].thumbnail_url
                                        val trues = storyBook[0].list[i].saveLocal
                                        listImage.add(ImageStorySave(id, imageId, image_url, priorityImage, thumbnail_url, trues))
                                    } else {
                                        val imageId = listStoryBook[0].list[idImage].image_id
                                        val priorityImage = listStoryBook[0].list[idImage].priority
                                        val thumbnail_url = listStoryBook[0].list[idImage].thumbnail_url
                                        listImage.add(ImageStorySave(id, imageId, path, priorityImage, thumbnail_url, true))
                                    }

                                }
                                val storybook = StoryBookSave(id, book_image_url, book_name, is_pro, listImage, priority, currentDate, currentTime)
                                updateStory(storybook)
                                gg("vcvcvcvfgfgfgfgfg", "vao $storybook")
                            }

                            nextFragment(path)
                        }
                    }
                }.toJPG().save2(context!!)


            } else {
                Toast.makeText(context, getString(R.string.st_went_wrong), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun nextFragment(path: String) {
        val bundle = Bundle()
        bundle.putString("param1", path)
        bundle.putString("param1", path)
        findNavController().navigate(R.id.action_editFragment_to_saveFragment, bundle)
    }


    private fun initView() {
        initImage()
    }

    private var imageUrl2 = ""

    private fun initImage() {
        if (imageUrl != null && imageUrl!!.isNotEmpty()) {
            if (isFromMain) {
                imageName = imageUrl
                if (PhotorTool.checkHasPermission(activity!!)) {
                    val path = TEMP_FOLDER
                    val directory = File(path)
                    val files = directory.listFiles()
                    if (files == null) {
                        loadImage()
                        return
                    }
                    if (directory.isDirectory) {
                        if (files.isEmpty()) {
                            gg("vâovaovaov", "câccaa1")
                            loadImage()
                        } else {
                            for (file in files) {
                                val url = file.absolutePath
                                var s = url
                                s = s?.substring(s.lastIndexOf("/") + 1)
                                s = s?.substring(0, s.lastIndexOf("."))
                                if (imageUrl.equals(s)) {
                                    imageUrl2 = url
                                    Glide.with(context!!).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgPreView)
                                    showPopupCreateNew()
                                    gg("vâovaovaov", "câccaa2")
                                    return
                                }
                            }

                            getListImaageSave(imageUrl!!)

                            Handler().postDelayed({
                                gg("vâovaovaov", "$imageSave")
                                if (imageSave.size > 0) {
                                    val date = imageSave[0].date
                                    if (date == "") {
                                        loadImage()
                                    } else {
                                        PhotorDialog.getInstance().showDialogConfirm(activity, R.string.continue_edit_or_new,
                                            R.string.continuee, R.string.rework, false, { _, _ ->
                                                //continue
                                                imageUrl = imageSave[0].name
                                                isFromMain = false
                                                loadImage()
                                            }, { _, _ ->
                                                loadImage()

                                            })
                                    }
                                } else {
                                    loadImage()
                                }
                            }, 100)


                        }
                    }
                } else {
                    gg("vâovaovaov", "câccaa4")
                    loadImage()
                }


            } else {
                var s = imageUrl
                s = s?.substring(s.lastIndexOf("/") + 1)
                s = s?.substring(0, s.lastIndexOf("."))
                imageName = s
                storyBook
                Lo.d("imageurl $imageUrl")
                Lo.d("imageName $imageName")
                Handler().postDelayed({
                    gg("vâovaovaov", "câccaa5 $storyBook")
                    gg("vâovaovaov", "câccaa5 $idImage")
                    if (isMywork == false) {
                        if (storyBook.size > 0) {
                            val check = storyBook[0].list[idImage].saveLocal
                            if (check == true) {
                                PhotorDialog.getInstance().showDialogConfirm(activity, R.string.continue_edit_or_new,
                                    R.string.continuee, R.string.rework, false, { _, _ ->
                                        //continue
                                        imageUrl = storyBook[0].list[idImage].image_url
                                        isFromMain = false
                                        loadImage()
                                    }, { _, _ ->
                                        loadImage()

                                    })
                            } else {
                                loadImage()
                            }
                        } else {
                            loadImage()
                        }
                    } else {
                        loadImage()
                    }


                }, 200)


            }

        } else {
            //todo somethin wrong
        }
    }

    private fun showPopupCreateNew() {
        PhotorDialog.getInstance().showDialogConfirm(activity, R.string.continue_edit_or_new,
            R.string.continuee, R.string.rework, false, { _, _ ->
                //continue
                imageUrl = imageUrl2
                isFromMain = false
                loadImage()
            }, { _, _ ->
                //rework
                loadImage()
            })


    }

    fun dialogCreate(text: String) {
        val builder: AlertDialog.Builder?
        val inflater = this.layoutInflater
        builder = AlertDialog.Builder(context!!)
        val logoutDialog = inflater.inflate(R.layout.dialog_chose_color, null)
//            val btnCancel: ImageView = logoutDialog.findViewById(R.id.btn_close_dialog)
        val tvDone: TextView = logoutDialog.findViewById(R.id.tv_done)
//            val layoutSend: RelativeLayout = logoutDialog.findViewById(R.id.layout_send_feedback)


        builder.setView(logoutDialog)
        builder.setCancelable(true)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        if (text != "") {
            dialog.edt.setText(text)
        }
        dialog.colors_1.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#EF238D"))
            tv2.setTextColor(Color.parseColor("#EF238D"))
            dialog.img_chooses_1.visibility = View.VISIBLE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_2.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#EA3136"))
            tv2.setTextColor(Color.parseColor("#EA3136"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.VISIBLE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_3.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#FE6700"))
            tv2.setTextColor(Color.parseColor("#FE6700"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.VISIBLE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_4.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#FE9900"))
            tv2.setTextColor(Color.parseColor("#FE9900"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.VISIBLE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_5.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#01CC01"))
            tv2.setTextColor(Color.parseColor("#01CC01"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.VISIBLE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_6.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#00AFEE"))
            tv2.setTextColor(Color.parseColor("#00AFEE"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.VISIBLE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_7.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#009899"))
            tv2.setTextColor(Color.parseColor("#009899"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.VISIBLE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_8.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#003399"))
            tv2.setTextColor(Color.parseColor("#003399"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.VISIBLE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_9.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#6600CD"))
            tv2.setTextColor(Color.parseColor("#6600CD"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.VISIBLE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.GONE
        }

        dialog.colors_10.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#FFFFFF"))
            tv2.setTextColor(Color.parseColor("#FFFFFF"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.VISIBLE
            dialog.img_chooses_11.visibility = View.GONE
        }
        dialog.colors_11.setOnClickListener {
            dialog.edt.setTextColor(Color.parseColor("#000000"))
            tv2.setTextColor(Color.parseColor("#000000"))
            dialog.img_chooses_1.visibility = View.GONE
            dialog.img_chooses_2.visibility = View.GONE
            dialog.img_chooses_3.visibility = View.GONE
            dialog.img_chooses_4.visibility = View.GONE
            dialog.img_chooses_5.visibility = View.GONE
            dialog.img_chooses_6.visibility = View.GONE
            dialog.img_chooses_7.visibility = View.GONE
            dialog.img_chooses_8.visibility = View.GONE
            dialog.img_chooses_9.visibility = View.GONE
            dialog.img_chooses_10.visibility = View.GONE
            dialog.img_chooses_11.visibility = View.VISIBLE
        }


        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            btnCancel.setOnClickListener {
//                dialog.dismiss()
//            }
        tvDone.setOnClickListener {
            tv2.setText(dialog.edt.text.toString())
            tv.visibility = View.VISIBLE
            dialog.dismiss()
        }

    }

    private fun loadImage() {
        tv.setOnClickListener {
            dialogCreate(tv2.text.toString())
        }
        tv.makeDraggable()

        tv_settext.setOnClickListener {
            dialogCreate("")
        }
        close.setOnClickListener {
            tv.visibility = View.GONE
        }
        if (isRestart) isFromMain = true

        val id = AppConst.getIdRawFromName(context!!, imageName!!)
//        if (id == 0) {
//            To.show(R.string.try_again)
//            return
//        }

        val compressOptions = Tiny.BitmapCompressOptions()
        compressOptions.config = Bitmap.Config.ARGB_8888
        if (isFromMain) {
            Tiny.getInstance().source(id!!).asBitmap().withOptions(compressOptions).compress { isSuccess, bitmap, t ->
                if (isSuccess) {
                    try {
                        imgPreView?.visibility = View.GONE
                        imageView?.createBitMap(bitmap!!)
                        PhotoViewAttacher(imageView, bitmap)
                    } catch (e: IllegalStateException) {
                    }
                } else {
                    loadImageOld(id)
                }
            }
        } else {
            Tiny.getInstance().source(imageUrl).asBitmap().withOptions(compressOptions).compress { isSuccess, bitmap, t ->
                if (isSuccess) {
                    try {
                        imgPreView?.visibility = View.GONE
                        imageView?.createBitMap(bitmap!!)
                        PhotoViewAttacher(imageView, bitmap)
                    } catch (e: IllegalStateException) {
                    }
                } else {
                    loadImageOld(id!!)
                }
            }
        }


    }

    private fun loadImageOld(id: Int) {
        var bitmap: Bitmap? = null
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                try {
                    bitmap = Glide.with(context!!)
                        .asBitmap()
                        .load(if (isFromMain) {
//                        id
                            imageUrl
                        } else {
                            imageUrl
                        })
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .submit().get()
                } catch (e: IllegalStateException) {
                }
            }

            override fun onCompleted() {
                try {
                    imgPreView?.visibility = View.GONE
                    imageView?.createBitMap(bitmap!!)
                    PhotoViewAttacher(imageView, bitmap)
                } catch (e: IllegalStateException) {
                }
            }

            override fun onCancel() {
            }

        })
    }


    override fun restart() {
        val compressOptions = Tiny.BitmapCompressOptions()
        compressOptions.config = Bitmap.Config.ARGB_8888
        Tiny.getInstance().source(AppConst.getIdRawFromName(context!!, imageName!!)!!).asBitmap().withOptions(compressOptions).compress { isSuccess, bitmap, t ->
            if (isSuccess) {
                imageView?.createBitMap(bitmap!!)
            } else {
                loadBitmapRestart()
            }
        }
    }

    private fun loadBitmapRestart() {
        var bitmap: Bitmap? = null
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                bitmap = Glide.with(context!!)
                    .asBitmap()
                    .load(AppConst.getIdRawFromName(context!!, imageName!!))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .submit().get()
            }

            override fun onCompleted() {
                imageView?.createBitMap(bitmap!!)
            }

            override fun onCancel() {
            }

        })
    }


    private fun changeCurrentColor(color: Int) {
        imageView?.setColor(color)
    }

    fun saveImageDao(image: ImageModel) {
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                dao!!.upsertImage(image)
            }

            override fun onCompleted() {
            }

            override fun onCancel() {
            }

        })

    }

    fun saveStory(story: StoryBookSave) {
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                storyBookdao!!.insertStoty(story)
            }

            override fun onCompleted() {
            }

            override fun onCancel() {
            }

        })

    }

    fun updateStory(story: StoryBookSave) {
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                storyBookdao!!.updateStory(story)
            }

            override fun onCompleted() {
            }

            override fun onCancel() {
            }

        })

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


    fun getList2(name: String): List<StoryBookSave> {
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                val x = storyBookdao!!.getbookId(name)
                gg("huhuhuhuhuhuhuhu", "$x")
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

    private fun initEvent() {
        PhotorTool.clickScaleView(btn_back_main, this)
        PhotorTool.clickScaleView(btn_idea_main, this)
        PhotorTool.clickScaleView(btn_back_image, this)
        PhotorTool.clickScaleView(btn_forward_image, this)
        btn_share.setOnClickListener {
            close.visibility = View.GONE
            tv2.setBackgroundResource(R.drawable.bodertext2)
            if (edit == 1001) {
                val file = File(imageUrl)
                file.delete()
                context!!.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(imageUrl))))
                deleteImage(imageUrl!!)
            }
            Log.e("MEOMEO", "click save")
            if (!canTouch()) return@setOnClickListener
            if (PhotorTool.checkHasPermission(activity!!)) {
                saveImage()
            } else {
                askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    if (it.isAccepted) {
                        saveImage()
                    }
                }
            }
        }

    }

    override fun setTextColor(color: Int) {
        changeCurrentColor(color)
    }

    fun getListImaageSave(url: String): List<ImageModel> {
        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                imageSave = dao!!.getImageFromUrlGoc(url)

            }

            override fun onCompleted() {
//                imageModel.add(imageModel1!!)
//                imageModel2 = imageModel

            }

            override fun onCancel() {
            }

        })
        return imageSave
    }

    private fun saveCurrentImage() {
        if (activity != null) {
            if (PhotorTool.checkHasPermission(activity!!)) {
                cacheImage()
            } else {
                findNavController().popBackStack(R.id.menuFragment, false)
//                findNavController().navigate(R.id.action_editFragment_to_menuFragment)
            }
        }
    }

    private fun cacheImage() {
        val size = imageView.size()
        val imageUrl1: String = if (isFromMain) {

            "$TEMP_FOLDER$imageUrl.jpg"
        } else {
            "$imageUrl"
        }
        val bitmap = imageView?.getBitmap()
        if (bitmap != null) {
            AppConst.saveBitmap(imageUrl1, "", bitmap)
        }
        findNavController().popBackStack(R.id.menuFragment, false)
//        findNavController().navigate(R.id.action_editFragment_to_menuFragment)
//        mChangeFragmentListener?.popToRootFragments()
    }


    private var current_click_idea = 0
    private fun clickIdea() {
        if (current_click_idea < 3) {
            current_click_idea++
            when (current_click_idea) {
                1 -> Glide.with(context!!).load(R.drawable.bt_idea_2).into(btn_idea_main)
                2 -> Glide.with(context!!).load(R.drawable.bt_idea_1).into(btn_idea_main)
                3 -> {
                    Glide.with(context!!).load(R.drawable.bt_idea_ads).into(btn_idea_main)
                    btn_idea_main.isEnabled = true
//                    btn_idea_main.alpha = 0.5F
                }
            }
            val builder: AlertDialog.Builder?
            val inflater = this.layoutInflater
            builder = AlertDialog.Builder(context!!)
            val logoutDialog = inflater.inflate(R.layout.dialog_idea, null)
            val btnCancel: ImageView = logoutDialog.findViewById(R.id.btn_close_dialog)
            val imgIdea: ImageView = logoutDialog.findViewById(R.id.img_idea)
            if (isFromMain) {
                Glide.with(context!!).load(AppConst.getIdRawFromName(context!!, imageUrl + "_mau")).into(imgIdea)
            } else {
                var s = imageUrl
                s = s?.substring(s.lastIndexOf("/") + 1)
                s = s?.substring(0, s.lastIndexOf("."))
                Glide.with(context!!).load(AppConst.getIdRawFromName(context!!, s + "_mau")).into(imgIdea)
            }

            builder.setView(logoutDialog)
            builder.setCancelable(true)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        } else {

        }
    }

    override fun eventStateBack(able: Boolean) {
        Lo.d(able.toString())
        if (able) {
            btn_back_image.alpha = 1F
            btn_back_image.isEnabled = true
        } else {
            btn_back_image.isEnabled = false
            btn_back_image.alpha = 0.5F
        }
    }

    override fun eventStateForward(able: Boolean) {
        if (able) {
            btn_forward_image.alpha = 1F
            btn_forward_image.isEnabled = true
        } else {
            btn_forward_image.alpha = 0.5F
            btn_forward_image.isEnabled = false
        }
    }

    override fun backInMain() {
        super.backInMain()

        PhotorDialog.getInstance().showDialogConfirm(activity, R.string.end_edit, R.string.no, R.string.yes, true,
            { dialog, _ ->
                //click no
                dialog.dismiss()
//                isSaveImage = false
//                mChangeFragmentListener?.popBackStackFragment("MenuFragment")
            }, { dialog, _ ->
                //click yes
                dialog.dismiss()
                saveCurrentImage()
            }
        )
    }


    override fun loadBitmapSuccess() {
        progressBar.gone()
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        if (!canTouch()) return
        when (v?.id) {
            R.id.btn_back_main -> backInMain()
            R.id.btn_idea_main -> clickIdea()
            R.id.btn_back_image -> {
                imageView?.actionBackStep()
            }
            R.id.btn_forward_image -> {
                imageView?.actionForwardStep()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (context != null) {
            Glide.get(context!!).clearMemory()
        }
        PhotorThread.getInstance().removeAllBackgroundThreads()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        PhotorDialog.getInstance().showDialogConfirm(activity, R.string.end_edit, R.string.no, R.string.yes, true,
            { dialog, _ ->
                //click no
                dialog.dismiss()
//                isSaveImage = false
//                mChangeFragmentListener?.popToRootFragments()
            }, { dialog, _ ->
                dialog.dismiss()
                if (!isSaveImaged) {
                    saveCurrentImage()
                } else {
                    mChangeFragmentListener?.popToRootFragments()
                }
            }
        )
    }
}
