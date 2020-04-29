package com.volio.coloringbook2.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.volio.alarmoclock.eventbus.EventBus
import com.volio.alarmoclock.eventbus.MessageEvent2
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.AppConst.listCats
import com.volio.coloringbook2.common.gg
import com.volio.coloringbook2.common.toast
import com.volio.coloringbook2.customview.recyclical.emptyDataSource
import com.volio.coloringbook2.customview.recyclical.setup
import com.volio.coloringbook2.customview.recyclical.withItem
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.holder.ImageHolder
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.model.ColorBook
import com.volio.coloringbook2.model.Type
import com.volio.coloringbook2.models.ImageModel
import com.volio.coloringbook2.models.TypeModel
import com.volio.coloringbook2.viewmodel.ColorBookViewModel
import kotlinx.android.synthetic.main.fragment_new_image.*

private const val ARG_PARAM1 = "param1"

class NewImageFragment : BaseFragment() {
    lateinit var viewModel: ColorBookViewModel
    var check = 0
    private var listImage = mutableListOf<ImageModel>()
    private var typeImage: TypeModel? = null
    private var dataSourceImage = emptyDataSource()
    private var listImageServer: List<Type>? = null


    companion object {
        @JvmStatic
        fun newInstance(param1: TypeModel) =
            NewImageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }

    }


    var newImageInterface: NewImageInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            arguments?.let {
                typeImage = it.getParcelable(ARG_PARAM1)

            }
        } else {
            check = 1
            typeImage = TypeModel(context!!.resources.getString(R.string.manda), 1)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_image, container, false)
    }

    //private var isFirst = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ColorBookViewModel::class.java)
        initRecycleView()
    }

    private fun initRecycleView() {
        listImage.clear()
        dataSourceImage.clear()

        val list = AppConst.getAllType2(context!!)
        val position = list.size - 1
        for (i in 0..position) {
            val id = list[i].id
            when (typeImage?.id) {

                id -> {
                    if (id != 10002) {
                        val apiJson = context!!.config.category
                        val gson = Gson()
                        val category = gson.fromJson(apiJson, ColorBook::class.java)
//                        listImageServer = category[id].listType as ArrayList<Type>
                        val positionss = category.size - 1
                        for (i in 0..positionss) {
                            if (category[i].cate_id == "$id") {
                                listImageServer = category[i].listType

                            }


                        }



                    }

                }
//                1 -> listImage.addAll(listCats)
//                2 -> listImage.addAll(listFantasy)
//                3 -> listImage.addAll(AppConst.listFloral)
//                4 -> listImage.addAll(AppConst.listMandala)
//                5 -> listImage.addAll(AppConst.listPeople)
//                6 -> listImage.addAll(AppConst.listUnicorn)
            }

        }
        when (typeImage?.id) {
            10002 -> listImage.addAll(AppConst.listMandala)
        }
        if (typeImage?.id == 10002) {
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
                        if (check == 1) {
                            page.visibility = View.GONE
                            nameCategory.visibility = View.GONE
                        }

                        if (typeImage?.id == 10002) {
                            nameCategory.text = "Simple Mandalas"
                        }
                        val type = item.type
//                    Lo.d("type $type")
                        when (type) {


                            2 -> Glide.with(context!!).load(R.drawable.ic_new).into(imgType)
                            1 -> Glide.with(context!!).load(R.drawable.a).into(imgType)
                            0 -> imgType.setImageResource(R.color.tranparent)
                            3 -> {
                                page.text = "10 pics"
                                nameCategory.text = "Hard Mandalas"
                            }
                            4 -> {
                                nameCategory.text = "Simple Mandalas"
                                page.text = "6 pics"
                            }

                        }
                    }
                    onClick { index, item ->
                        if (!canTouch()) return@onClick
//                    openPictureToColor(item.name)

                            val  bundle = bundleOf("url" to item.name)


                        findNavController().navigate(R.id.action_menuFragment_to_categoryFragment, bundle)

//                    loadIntertital("ClickPicture", R.string.please_wait_to_load_image, object : OnAdCallback {
//                        override fun actionShow() {
//
//                        }
//                    })
                    }
                }
            }
        } else {
            dataSourceImage.clear()
            for (item in listImageServer!!) {
                dataSourceImage.add(item)
            }

            recycle_list_image.setup {
                withDataSource(dataSourceImage)
                withLayoutManager(
                    GridLayoutManager(context, 2)
                )
                withItem<Type>(R.layout.item_list_image) {
                    onBind(::ImageHolder) { index, item ->

                        val url = "http://mycat.asia/volio_colorbook/"
                        val url2 = "${url}${listImageServer!![index].image_type_url}"
                        nameCategory.text = "${listImageServer!![index].name}"
                        Glide.with(context!!).load(url2).placeholder(R.drawable.ic_splash)
                            .into(img)
                        val listImage2 = listImageServer!![index].listImage
                        val size = listImage2.size
                        page.text = "$size pics"
                        val type = item.tag_type
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
//                    openPictureToColor(item.name)
                            val bundle = Bundle()
                            bundle.putString("url", item.name)
                            bundle.putString("id", item.type_id)
                            findNavController().navigate(R.id.action_menuFragment_to_categoryFragment, bundle)



//                    loadIntertital("ClickPicture", R.string.please_wait_to_load_image, object : OnAdCallback {
//                        override fun actionShow() {
//
//                        }
//                    })
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
