package com.volio.coloringbook2.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.volio.alarmoclock.eventbus.EventBus
import com.volio.alarmoclock.eventbus.MessageEvent2
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.AppConst.listCats
import com.volio.coloringbook2.common.gg
import com.volio.coloringbook2.customview.recyclical.emptyDataSource
import com.volio.coloringbook2.customview.recyclical.setup
import com.volio.coloringbook2.customview.recyclical.withItem
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.holder.ImageHolder
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.model.ColorBook
import com.volio.coloringbook2.model.Image
import com.volio.coloringbook2.model.Type
import com.volio.coloringbook2.models.ImageModel
import com.volio.coloringbook2.models.TypeModel
import kotlinx.android.synthetic.main.fragment_new_image.*

private const val ARG_PARAM1 = "param1"

class ImageFragment : BaseFragment() {
    var check = 0
    var url = ""
    var baseUrl="http://mycat.asia/volio_colorbook/"
    var idType = ""
    private var listImage = mutableListOf<ImageModel>()
    private var typeImage: TypeModel? = null
    private var dataSourceImage = emptyDataSource()
    private var listImageServer: List<Image>? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: TypeModel) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }

    }


    var newImageInterface: NewImageInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val bundle = arguments
            url = bundle!!.getString("url")!!
            gg("hihihihihihihihi", "$url")
            typeImage = it.getParcelable(ARG_PARAM1)
            if (url == "mandalaa_1") {
                typeImage = TypeModel(context!!.resources.getString(R.string.manda), 10003)
            } else if (url == "simple_mandalas_1") {
                typeImage = TypeModel(context!!.resources.getString(R.string.manda), 10004)
            } else {
                   idType = bundle.getString("id")!!
                val apiJson = context!!.config.category
                val gson = Gson()
                val category = gson.fromJson(apiJson, ColorBook::class.java)
                val position = category.size - 1
                for (i in 0..position){
                    val listType = category[i].listType
                    for(j in 0..listType.size-1){
                        if(listType[j].type_id ==idType) {
                            listImageServer = listType[j].listImage
                        }
                    }
                }
            }

        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_fragment, container, false)
    }

    //private var isFirst = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()
    }

    private fun initRecycleView() {
        listImage.clear()
        dataSourceImage.clear()
        var idType2 =1
        if(idType != "") {
            idType2 = idType.toInt()
        }
        when (typeImage?.id) {

//            0 -> {
//                listImage.addAll(listCats)
//                listImage.addAll(listFantasy)
//                listImage.addAll(AppConst.listFloral)
//                listImage.addAll(AppConst.listMandala)
//                listImage.addAll(AppConst.listPeople)
//                listImage.addAll(AppConst.listUnicorn)
//            }
            idType2 ->  listImage.addAll(AppConst.listHardMandalas)
            10003 -> listImage.addAll(AppConst.listHardMandalas)
            10004 -> listImage.addAll(AppConst.listSimpleMandalas)
//            1 -> listImage.addAll(listCats)
//            2 -> listImage.addAll(listFantasy)
//            3 -> listImage.addAll(AppConst.listFloral)
//            4 -> listImage.addAll(AppConst.listMandala)
//            5 -> listImage.addAll(AppConst.listPeople)
//            6 -> listImage.addAll(AppConst.listUnicorn)
        }

        if(typeImage?.id == null){
           var url2 =""
            dataSourceImage.clear()
            for (item in listImageServer!!) {
                dataSourceImage.add(item)
            }

            recycle_list_image.setup {
                withDataSource(dataSourceImage)
                withLayoutManager(
                    GridLayoutManager(context, 2)
                )
                withItem<Image>(R.layout.item_list_image) {
                    onBind(::ImageHolder) { index, item ->
                        url2 = "${baseUrl}${listImageServer!![index].image_url}"
                        nameCategory.visibility = View.GONE
                        Glide.with(context!!).load(url2).placeholder(R.drawable.ic_splash)
                            .into(img)
                        page.visibility = View.GONE
                        val type = item.tag_image
//                        page.text = "$size pics"
//                    Lo.d("type $type")
                        when (type) {


                            "2" -> Glide.with(context!!).load(R.drawable.ic_new).into(imgType)
                            "2" -> Glide.with(context!!).load(R.drawable.a).into(imgType)
                            "2" -> imgType.setImageResource(R.color.tranparent)
                            "2" -> {
                                page.text = "10 pics"
                                nameCategory.text = "Hard Mandalas"
                            }
                            "2" -> {
                                nameCategory.text = "Simple Mandalas"
                                page.text = "6 pics"
                            }

                        }
                    }
                    onClick { index, item ->

                        if (!canTouch()) return@onClick
                        url2 = "${baseUrl}${listImageServer!![index].image_url}"
                        openPictureToColor(url2)
                    }
                }
            }
        } else{

            dataSourceImage.clear()
            for (item in listImage) {
                dataSourceImage.add(item)
            }

            recycle_list_image.setup {
                withDataSource(dataSourceImage)
                withLayoutManager(
                    GridLayoutManager(context, 2)
                )
                withItem<ImageModel>(R.layout.item_list_image) {
                    onBind(::ImageHolder) { index, item ->
                        Glide.with(context!!).load(context!!.resources.getIdentifier(item.name, "raw", context?.packageName)
                        ).into(img)
                        page.visibility = View.GONE
                        nameCategory.visibility = View.GONE

                        val type = item.type
                        when (type) {


                            2 -> Glide.with(context!!).load(R.drawable.ic_new).into(imgType)
                            1 -> Glide.with(context!!).load(R.drawable.a).into(imgType)
                            0 -> imgType.setImageResource(R.color.tranparent)
//                        3 -> {
//                            page.text = "10 pics"
//                            nameCategory.text = "Hard Mandalas"
//                        }
//                        4 -> {
//                            nameCategory.text = "Simple Mandalas"
//                            page.text = "6 pics"
//                        }

                        }
                    }
                    onClick { index, item ->
                        if (!canTouch()) return@onClick
                        openPictureToColor(item.name)

                    }
                }
            }
        }

    }

    private fun openPictureToColor(url: String) {
        newImageInterface?.onGotoEdit(url, true, false)
        EventBus.bus?.post(MessageEvent2("edit", url, true, false))
    }
}
