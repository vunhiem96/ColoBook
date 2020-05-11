package com.volio.coloringbook2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.Gson
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer

import com.volio.coloringbook2.R
import com.volio.coloringbook2.adapter.PageStoryAdapter
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.gg
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import com.volio.coloringbook2.model.storybook.ImageStory
import com.volio.coloringbook2.model.storybook.StoryBook
import com.volio.coloringbook2.models.UltraPagerAdapter
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_mywork.*
import kotlinx.android.synthetic.main.fragment_page_story.*

/**
 * A simple [Fragment] subclass.
 */
class PageStoryFragment : Fragment(), OnCustomClickListener {
   var name =""
    var postionList = 0
    var listImage:List<ImageStory> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PhotorTool.clickScaleView(back, this)
        PhotorTool.clickScaleView(btn_to_edit, this)

        name = arguments!!.getString("name")!!
        postionList = arguments!!.getInt("id")
        name_page.text = name
        initViewPager()
        val url = context!!.config.urlImage


    }
    private var adapter: PagerAdapter? = null
    private fun initViewPager() {
        ultraViewPager_story.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        val apiJson = context!!.config.storyBook
        val gson = Gson()
        val listStoryBook = gson.fromJson(apiJson, StoryBook::class.java)
        listImage = listStoryBook[postionList].list
        gg("vcvcvcgjgjgjgjgjg","$listImage")
        adapter = PageStoryAdapter(context!!,listImage)

        ultraViewPager_story.adapter = adapter

        ultraViewPager_story.setMultiScreen(0.75f)
        ultraViewPager_story.setAutoMeasureHeight(false)
        ultraViewPager_story.setPageTransformer(false, UltraDepthScaleTransformer())
        ultraViewPager_story.setInfiniteLoop(false)
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.back -> findNavController().popBackStack()

            R.id.btn_to_edit ->{
                val bundle = Bundle()
                AppConst.positionChoose = ultraViewPager_story.currentItem
                gg("vcvcvcvchghghghgh","${AppConst.positionChoose}")
                bundle.putInt("idImg", AppConst.positionChoose)
                bundle.putInt("idList", postionList)
                bundle.putBoolean("story", true)
                findNavController().navigate(R.id.action_pageStoryFragment_to_editFragment, bundle)
            }



        }
    }

}
