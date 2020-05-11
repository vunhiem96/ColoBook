package com.volio.coloringbook2.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.Gson
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer
import com.volio.coloringbook2.R
import com.volio.coloringbook2.adapter.PageMyWorkAdapter
import com.volio.coloringbook2.adapter.PageStoryAdapter
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.gg
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.database.SaveStoryDao
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.java.PhotorThread
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import com.volio.coloringbook2.model.storybook.ImageStory
import com.volio.coloringbook2.model.storybook.StoryBook
import com.volio.coloringbook2.model.storybook.saveLocal.ImageStorySave
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave
import com.volio.coloringbook2.models.ImageModel
import kotlinx.android.synthetic.main.fragment_page_story.*

/**
 * A simple [Fragment] subclass.
 */
class PageStoryFragment : Fragment(), OnCustomClickListener {
    var idImage = ""
    var name = ""
    var postionList = 0
    private var listImage: List<ImageStory> = ArrayList()
    var listImage2: List<StoryBookSave> = ArrayList()
    private var storyBookdao: SaveStoryDao? = null
    var mywork = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PhotorTool.clickScaleView(back, this)
        PhotorTool.clickScaleView(btn_to_edit, this)
        storyBookdao = CalendarDatabase.invoke(context!!).saveStoryDao()

        mywork = arguments!!.getBoolean("mywork")
        postionList = arguments!!.getInt("id")
        name = arguments!!.getString("name")!!
        name_page.text = name
        if (mywork == false) {

            initViewPager()
        } else {
            idImage = arguments!!.getString("idImage")!!
            getListImage(idImage)

            gg("vcvcvcgjgjgjgjgjg", "$listImage")
            gg("vcvcvcgjgjgjgjgjg", "$idImage")

            Handler().postDelayed({
                initViewPagerMywork()
            },200)

        }


    }

    private fun initViewPagerMywork() {
        ultraViewPager_story.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        val listSave = listImage2[0].list
        adapter = PageMyWorkAdapter(context!!, listSave)
        ultraViewPager_story.adapter = adapter
        ultraViewPager_story.setMultiScreen(0.75f)
        ultraViewPager_story.setAutoMeasureHeight(false)
        ultraViewPager_story.setPageTransformer(false, UltraDepthScaleTransformer())
        ultraViewPager_story.setInfiniteLoop(false)
    }

    private var adapter: PagerAdapter? = null
    private fun initViewPager() {
        ultraViewPager_story.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        val apiJson = context!!.config.storyBook
        val gson = Gson()
        val listStoryBook = gson.fromJson(apiJson, StoryBook::class.java)
        listImage = listStoryBook[postionList].list
        adapter = PageStoryAdapter(context!!, listImage)
        ultraViewPager_story.adapter = adapter
        ultraViewPager_story.setMultiScreen(0.75f)
        ultraViewPager_story.setAutoMeasureHeight(false)
        ultraViewPager_story.setPageTransformer(false, UltraDepthScaleTransformer())
        ultraViewPager_story.setInfiniteLoop(false)
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.back -> findNavController().popBackStack()

            R.id.btn_to_edit -> {
                if (mywork == false) {
                    val bundle = Bundle()
                    AppConst.positionChoose = ultraViewPager_story.currentItem
                    gg("vcvcvcvchghghghgh", "${AppConst.positionChoose}")
                    bundle.putInt("idImg", AppConst.positionChoose)
                    bundle.putInt("idList", postionList)
                    bundle.putBoolean("story", true)
                    findNavController().navigate(R.id.action_pageStoryFragment_to_editFragment, bundle)
                } else {
                    val bundle = Bundle()
                    AppConst.positionChoose = ultraViewPager_story.currentItem
                    gg("vcvcvcvchghghghgh", "${AppConst.positionChoose}")
                    bundle.putInt("idImg", AppConst.positionChoose)
                    bundle.putString("idStorybook", idImage)
                    bundle.putInt("idList", postionList)
                    bundle.putBoolean("story", true)
                    bundle.putBoolean("mywork", true)
                    findNavController().navigate(R.id.action_pageStoryFragment_to_editFragment, bundle)
                }
            }


        }
    }
    fun getListImage(id:String): List<StoryBookSave> {

        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                listImage2 = storyBookdao!!.getbookId(id)

            }

            override fun onCompleted() {
//                imageModel.add(imageModel1!!)
//                imageModel2 = imageModel

            }

            override fun onCancel() {
            }

        })
        return listImage2
    }

}
