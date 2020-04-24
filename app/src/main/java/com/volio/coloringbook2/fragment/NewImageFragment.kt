package com.volio.coloringbook2.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.volio.alarmoclock.eventbus.EventBus
import com.volio.alarmoclock.eventbus.MessageEvent2
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.AppConst.listCats
import com.volio.coloringbook2.common.AppConst.listFantasy
import com.volio.coloringbook2.customview.recyclical.emptyDataSource
import com.volio.coloringbook2.customview.recyclical.setup
import com.volio.coloringbook2.customview.recyclical.withItem
import com.volio.coloringbook2.holder.ImageHolder
import com.volio.coloringbook2.interfaces.NewImageInterface
import com.volio.coloringbook2.models.ImageModel
import com.volio.coloringbook2.models.TypeModel
import kotlinx.android.synthetic.main.fragment_new_image.*

private const val ARG_PARAM1 = "param1"

class NewImageFragment : BaseFragment() {
   var check = 0
    private var listImage = mutableListOf<ImageModel>()
    private var typeImage: TypeModel? = null
    private var dataSourceImage = emptyDataSource()

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
        initRecycleView()
    }

    private fun initRecycleView() {
        listImage.clear()
        dataSourceImage.clear()
        when (typeImage?.id) {
            0 -> {
                listImage.addAll(listCats)
                listImage.addAll(listFantasy)
                listImage.addAll(AppConst.listFloral)
                listImage.addAll(AppConst.listMandala)
                listImage.addAll(AppConst.listPeople)
                listImage.addAll(AppConst.listUnicorn)
            }
            1 -> listImage.addAll(listCats)
            2 -> listImage.addAll(listFantasy)
            3 -> listImage.addAll(AppConst.listFloral)
            4 -> listImage.addAll(AppConst.listMandala)
            5 -> listImage.addAll(AppConst.listPeople)
            6 -> listImage.addAll(AppConst.listUnicorn)
        }
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
                    if (check == 1){
                        page.visibility = View.GONE
                    }

                    val type = item.type
//                    Lo.d("type $type")
                    when (type) {
                        2 -> Glide.with(context!!).load(R.drawable.ic_new).into(imgType)
                        1 -> Glide.with(context!!).load(R.drawable.a).into(imgType)
                        0 -> imgType.setImageResource(R.color.tranparent)
                    }
                }
                onClick { index, item ->
                    if (!canTouch()) return@onClick
//                    openPictureToColor(item.name)
                    if (arguments != null) {
                        findNavController().navigate(R.id.action_menuFragment_to_categoryFragment)

                    } else {
                        Log.i("vcvcvcvcvcvcvc","${item.name}")
                        openPictureToColor(item.name)
                    }

//                    loadIntertital("ClickPicture", R.string.please_wait_to_load_image, object : OnAdCallback {
//                        override fun actionShow() {
//
//                        }
//                    })
                }
            }
        }
    }

    private fun openPictureToColor(url: String) {
        newImageInterface?.onGotoEdit(url, true, false)
        EventBus.bus?.post(MessageEvent2("edit", url,true,false))
    }
}
