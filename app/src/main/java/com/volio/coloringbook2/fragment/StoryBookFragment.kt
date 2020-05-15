package com.volio.coloringbook2.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.volio.coloringbook2.R
import com.volio.coloringbook2.adapter.StoryBookAdapter
import com.volio.coloringbook2.common.gone
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import com.volio.coloringbook2.model.storybook.StoryBook
import kotlinx.android.synthetic.main.fragment_story_book.*


/**
 * A simple [Fragment] subclass.
 */
class StoryBookFragment : BaseFragment(), OnCustomClickListener {
    var storyBookAdapter: StoryBookAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_story_book, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PhotorTool.clickScaleView(back_storybook, this)
        val check = isOnline()
        if(check == true) {
            initRv()
        } else{
            Toast.makeText(context!!,"Check your connection and try again",Toast.LENGTH_LONG).show()
        }

        context!!.config.Count = (context!!.config.Count)!!.toInt() + 1
        if (context!!.config.Count == 1) {
            Handler().postDelayed({
                progrees.gone()
            }, 500)
        } else {
            progrees.gone()
        }
    }

    fun isOnline(): Boolean {
        val connMgr = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
    private fun initRv() {
        rv_story_book.layoutManager = GridLayoutManager(context, 3)
        val apiJson = context!!.config.storyBook
        val gson = Gson()
        val listStoryBook = gson.fromJson(apiJson, StoryBook::class.java)
        storyBookAdapter = StoryBookAdapter(context!!, listStoryBook, object : StoryBookAdapter.ItemClickListener {
            override fun onClick(pos: Int) {
                val bundle = Bundle()
                bundle.putString("name", listStoryBook[pos].book_name)
                bundle.putInt("id", pos)
                findNavController().navigate(R.id.action_storyBookFragment_to_pageStoryFragment, bundle)
            }

        })
        rv_story_book.adapter = storyBookAdapter
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.back_storybook -> findNavController().popBackStack()


        }
    }

}
