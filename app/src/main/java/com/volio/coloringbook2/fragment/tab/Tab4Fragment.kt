package com.volio.coloringbook2.fragment.tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.AppConst
import com.volio.coloringbook2.common.toast
import com.volio.coloringbook2.fragment.BaseFragment
import com.volio.coloringbook2.java.PhotorTool
import com.volio.coloringbook2.java.util.OnCustomClickListener
import com.volio.coloringbook2.java.util.To
import com.volio.coloringbook2.viewmodel.ColorBookViewModel
import kotlinx.android.synthetic.main.fragment_tab4.*
import java.util.*


class Tab4Fragment : BaseFragment(), OnCustomClickListener, View.OnTouchListener {
    lateinit var viewmodel: ColorBookViewModel
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v?.id) {
            R.id.one_star -> {
                isChoseStar = true
                isFiveStar = false
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_uncheck)
                three_star?.setImageResource(R.drawable.star_uncheck)
                four_star?.setImageResource(R.drawable.star_uncheck)
                five_star?.setImageResource(R.drawable.star_uncheck)
            }
            R.id.two_star -> {
                isChoseStar = true
                isFiveStar = false
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_checked)
                three_star?.setImageResource(R.drawable.star_uncheck)
                four_star?.setImageResource(R.drawable.star_uncheck)
                five_star?.setImageResource(R.drawable.star_uncheck)
            }
            R.id.three_star -> {
                isChoseStar = true
                isFiveStar = false
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_checked)
                three_star?.setImageResource(R.drawable.star_checked)
                four_star?.setImageResource(R.drawable.star_uncheck)
                five_star?.setImageResource(R.drawable.star_uncheck)
            }
            R.id.four_star -> {
                isChoseStar = true
                isFiveStar = false
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_checked)
                three_star?.setImageResource(R.drawable.star_checked)
                four_star?.setImageResource(R.drawable.star_checked)
                five_star?.setImageResource(R.drawable.star_uncheck)
            }
            R.id.five_star -> {
                isChoseStar = true
                isFiveStar = true
                one_star?.setImageResource(R.drawable.star_checked)
                two_star?.setImageResource(R.drawable.star_checked)
                three_star?.setImageResource(R.drawable.star_checked)
                four_star?.setImageResource(R.drawable.star_checked)
                five_star?.setImageResource(R.drawable.star_checked)
            }
        }
        return false

    }

    private var isFiveStar = false
    private var isChoseStar = false
    private var one_star: ImageView? = null
    private var two_star: ImageView? = null
    private var three_star: ImageView? = null
    private var four_star: ImageView? = null
    private var five_star: ImageView? = null

    override fun onBackPressed() {
        super.onBackPressed()
        if (HomeFragment.isAdShown) {
            Toast.makeText(context, getString(R.string.please_wait_), Toast.LENGTH_SHORT).show()
        } else {
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PhotorTool.clickScaleView(txt_checkupdate, this)
        PhotorTool.clickScaleView(back_setting, this)
        PhotorTool.clickScaleView(txt_rate, this)
//        PhotorTool.clickScaleView(txt_feedback, this)
//        PhotorTool.clickScaleView(txt_policy, this)
        PhotorTool.clickScaleView(txt_share, this)
//        PhotorTool.clickScaleView(img_ad, this)
//        val font = Typeface.createFromAsset(activity?.assets,
//            "fonts/ChalkboardSE-Regular.ttf")
//        txt_checkupdate.typeface = font
//        txt_rate.typeface = font
//        txt_feedback.typeface = font
//        txt_policy.typeface = font
//        txt_share.typeface = font
        viewmodel = ViewModelProviders.of(activity!!).get(ColorBookViewModel::class.java)

        getVersionName()
//        viewmodel.url.observe(viewLifecycleOwner, object : Observer<String> {
//            override fun onChanged(t: String?) {
//                toast(context!!,"$t")
//            }
//        })
    }

    override fun OnCustomClick(v: View?, event: MotionEvent?) {
        when (v?.id) {
            R.id.txt_checkupdate -> {
                clickUpdate()
                viewmodel.url.value="vfvfvfvfvfvfvfvfv"
            }
            R.id.back_setting -> findNavController().popBackStack()

            R.id.txt_rate -> clickRate()
            R.id.txt_share -> clickShare()
//            R.id.txt_feedback -> clickFeedback()
//            R.id.txt_policy -> clickPolicy()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab4, container, false)
    }

    private fun clickUpdate() {
        PhotorTool.openMarket(context!!, context?.packageName)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickRate() {
        isFiveStar = false
        isChoseStar = false
        val builder: AlertDialog.Builder?
        val inflater = this.layoutInflater
        builder = AlertDialog.Builder(context!!)
        val logoutDialog = inflater.inflate(R.layout.dialog_rate_app, null)
        val btnCancel: ImageView = logoutDialog.findViewById(R.id.btn_close_dialog)
        val layoutSend: RelativeLayout = logoutDialog.findViewById(R.id.layout_send_rate)
        val btn_later: View = logoutDialog.findViewById(R.id.btn_later)

        one_star = logoutDialog.findViewById(R.id.one_star)
        two_star = logoutDialog.findViewById(R.id.two_star)
        three_star = logoutDialog.findViewById(R.id.three_star)
        four_star = logoutDialog.findViewById(R.id.four_star)
        five_star = logoutDialog.findViewById(R.id.five_star)
        one_star?.setOnTouchListener(this)
        two_star?.setOnTouchListener(this)
        three_star?.setOnTouchListener(this)
        four_star?.setOnTouchListener(this)
        five_star?.setOnTouchListener(this)

        builder.setView(logoutDialog)
        builder.setCancelable(true)

        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(true)
        dialog.show()

        btn_later.setOnClickListener {
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        layoutSend.setOnClickListener {
            if (isChoseStar) {
                if (isFiveStar) {
                    dialog.dismiss()
                    PhotorTool.openMarket(context, context?.packageName)
                } else {
                    dialog.dismiss()
                    clickFeedback()
                }
            } else {
                To.show("Please select a star to rate us")
            }
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    @SuppressLint("StringFormatInvalid")
    private fun clickFeedback() {
        val builder: AlertDialog.Builder?
        val inflater = this.layoutInflater
        builder = AlertDialog.Builder(context!!)
        val logoutDialog = inflater.inflate(R.layout.dialog_feedback, null)
        val btnCancel: ImageView = logoutDialog.findViewById(R.id.btn_close_dialog)
        val edText: EditText = logoutDialog.findViewById(R.id.ed_text_feedback)
        val layoutSend: RelativeLayout = logoutDialog.findViewById(R.id.layout_send_feedback)

        builder.setView(logoutDialog)
        builder.setCancelable(true)
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(true)
        dialog.show()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        PhotorTool.clickScaleView(layoutSend) { _, _ ->
            dialog.dismiss()
            val text = edText.text.toString()
            try {
                PhotorTool.sendEmailMoree(context, arrayOf(AppConst.EMAIL_FEEDBACK),
                    getString(R.string.feed_back_color_book), text)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun clickShare() {
        PhotorTool.shareApp(context!!)
    }

    private fun clickPolicy() {
        PhotorTool.openBrowser(context!!, AppConst.PRIVATE_POLICY)
    }

    private fun getVersionName() {
        try {
            val pInfo: PackageInfo
            pInfo = Objects.requireNonNull<Context>(context).getPackageManager().getPackageInfo(context!!.packageName, 0)
            val version = pInfo.versionName
//            versionname.setText("Version $version")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }
//    }
}
