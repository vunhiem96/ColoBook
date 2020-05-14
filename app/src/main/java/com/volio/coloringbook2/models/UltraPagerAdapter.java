

package com.volio.coloringbook2.models;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import com.volio.coloringbook2.R;
import com.volio.coloringbook2.common.AppConst;
import com.volio.coloringbook2.interfaces.ColorInterfaces;
import com.volio.coloringbook2.java.PhotorTool;

import java.util.ArrayList;
import java.util.HashMap;


public class UltraPagerAdapter extends PagerAdapter {
    private boolean isMultiScr;
    private ArrayList<ListColorModel> list;
    private ColorInterfaces interfaces;

    public UltraPagerAdapter(boolean isMultiScr, ArrayList<ListColorModel> list, ColorInterfaces interfaces) {
        this.isMultiScr = isMultiScr;
        this.list = list;
        this.interfaces = interfaces;
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }


    HashMap<Integer, LinearLayout> mapLayout = new HashMap<>();


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.layout_child, null);
        TextView textView = (TextView) linearLayout.findViewById(R.id.pager_textview);
        ImageView img1 = (ImageView) linearLayout.findViewById(R.id.color_1);
        ImageView img2 = (ImageView) linearLayout.findViewById(R.id.color_2);
        ImageView img3 = (ImageView) linearLayout.findViewById(R.id.color_3);
        ImageView img4 = (ImageView) linearLayout.findViewById(R.id.color_4);
        ImageView img5 = (ImageView) linearLayout.findViewById(R.id.color_5);
        ImageView img6 = (ImageView) linearLayout.findViewById(R.id.color_6);
        ImageView img7 = (ImageView) linearLayout.findViewById(R.id.color_7);
        ImageView img8 = (ImageView) linearLayout.findViewById(R.id.color_8);
        ImageView img9 = (ImageView) linearLayout.findViewById(R.id.color_9);
        ImageView img10 = (ImageView) linearLayout.findViewById(R.id.color_10);
        ImageView img11 = (ImageView) linearLayout.findViewById(R.id.color_11);
        ImageView img12 = (ImageView) linearLayout.findViewById(R.id.color_12);
        ImageView img13 = (ImageView) linearLayout.findViewById(R.id.color_13);
        ImageView img14 = (ImageView) linearLayout.findViewById(R.id.color_14);

//        final ImageView imgChose1 = (ImageView) linearLayout.findViewById(R.id.img_choose_1);
//        final ImageView imgChose2 = (ImageView) linearLayout.findViewById(R.id.img_choose_2);
//        final ImageView imgChose3 = (ImageView) linearLayout.findViewById(R.id.img_choose_3);
//        final ImageView imgChose4 = (ImageView) linearLayout.findViewById(R.id.img_choose_4);
//        final ImageView imgChose5 = (ImageView) linearLayout.findViewById(R.id.img_choose_5);
//        final ImageView imgChose6 = (ImageView) linearLayout.findViewById(R.id.img_choose_6);
//        final ImageView imgChose7 = (ImageView) linearLayout.findViewById(R.id.img_choose_7);
//        final ImageView imgChose8 = (ImageView) linearLayout.findViewById(R.id.img_choose_8);
//        final ImageView imgChose9 = (ImageView) linearLayout.findViewById(R.id.img_choose_9);
//        final ImageView imgChose10 = (ImageView) linearLayout.findViewById(R.id.img_choose_10);
//        final ImageView imgChose11 = (ImageView) linearLayout.findViewById(R.id.img_choose_11);


        ListColorModel model = list.get(position);
        final ArrayList<String> listColor = model.getList();
        img1.setColorFilter(Color.parseColor(listColor.get(0)));
        img2.setColorFilter(Color.parseColor(listColor.get(1)));
        img3.setColorFilter(Color.parseColor(listColor.get(2)));
        img4.setColorFilter(Color.parseColor(listColor.get(3)));
        img5.setColorFilter(Color.parseColor(listColor.get(4)));
        img6.setColorFilter(Color.parseColor(listColor.get(5)));
        img7.setColorFilter(Color.parseColor(listColor.get(6)));
        img8.setColorFilter(Color.parseColor(listColor.get(7)));
        img9.setColorFilter(Color.parseColor(listColor.get(8)));
        img10.setColorFilter(Color.parseColor(listColor.get(9)));
        img11.setColorFilter(Color.parseColor(listColor.get(10)));
        img12.setColorFilter(Color.parseColor(listColor.get(11)));
        img13.setColorFilter(Color.parseColor(listColor.get(12)));
        img14.setColorFilter(Color.parseColor(listColor.get(13)));

//        hideAll(imgChose1, imgChose2, imgChose3, imgChose4, imgChose5, imgChose6, imgChose7, imgChose8, imgChose9, imgChose10, imgChose11);





//        if (AppConst.INSTANCE.getTabChoose() == position) {
//            switch (AppConst.INSTANCE.getPositionChoose()) {
//                case 1:
//                    imgChose1.setVisibility(View.VISIBLE);
//                    break;
//                case 2:
//                    imgChose2.setVisibility(View.VISIBLE);
//                    break;
//                case 3:
//                    imgChose3.setVisibility(View.VISIBLE);
//                    break;
//                case 4:
//                    imgChose4.setVisibility(View.VISIBLE);
//                    break;
//                case 5:
//                    imgChose5.setVisibility(View.VISIBLE);
//                    break;
//                case 6:
//                    imgChose6.setVisibility(View.VISIBLE);
//                    break;
//                case 7:
//                    imgChose7.setVisibility(View.VISIBLE);
//                    break;
//                case 8:
//                    imgChose8.setVisibility(View.VISIBLE);
//                    break;
//                case 9:
//                    imgChose9.setVisibility(View.VISIBLE);
//                    break;
//                case 10:
//                    imgChose10.setVisibility(View.VISIBLE);
//                    break;
//                case 11:
//                    imgChose11.setVisibility(View.VISIBLE);
//                    break;
//            }
//
//        }


        PhotorTool.clickScaleView(img1, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(0));
            }
        });
        PhotorTool.clickScaleView(img12, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(11));
            }
        });
        PhotorTool.clickScaleView(img13, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(12));
            }
        });
        PhotorTool.clickScaleView(img14, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(13));
            }
        });

        PhotorTool.clickScaleView(img2, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(1));
            }
        });

        PhotorTool.clickScaleView(img3, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(2));
            }
        });


        PhotorTool.clickScaleView(img4, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(3));
            }
        });

        PhotorTool.clickScaleView(img5, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(4));
            }
        });

        PhotorTool.clickScaleView(img6, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(5));
            }
        });

        PhotorTool.clickScaleView(img7, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(6));
            }
        });

        PhotorTool.clickScaleView(img8, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(7));
            }
        });


        PhotorTool.clickScaleView(img9, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(8));
            }
        });

        PhotorTool.clickScaleView(img10, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(9));
            }
        });


        PhotorTool.clickScaleView(img11, (v, event) -> {
            if (interfaces != null) {
                interfaces.pickColor(listColor.get(10));
            }
        });

        textView.setText(model.getName());
        container.addView(linearLayout);
        return linearLayout;
    }


    private void anView(ViewGroup container, int position, int itemPosition) {
        if (AppConst.INSTANCE.getTabChoose() != -1) {
            View view = container.getChildAt(AppConst.INSTANCE.getTabChoose());
//            LinearLayout view = mapLayout.get(AppConst.INSTANCE.getTabChoose());

            Log.d("vinh", "an view " + view.toString() + " - " + AppConst.INSTANCE.getTabChoose());

            if (view == null) {
                Log.d("vinh", "anView: null");
            } else {
                Log.d("vinh", "anView: k null");
            }


            if (view == null) return;

            ImageView img1 = null;
            switch (AppConst.INSTANCE.getPositionChoose()) {
                case 1:
                    Log.d("dsk", "anView: 1");
                    img1 = view.findViewById(R.id.img_choose_1);
                    break;
                case 2:
                    img1 = view.findViewById(R.id.img_choose_2);
                    break;
                case 3:
                    img1 = view.findViewById(R.id.img_choose_3);
                    break;
                case 4:
                    img1 = view.findViewById(R.id.img_choose_4);
                    break;
                case 5:
                    img1 = view.findViewById(R.id.img_choose_5);
                    break;
                case 6:
                    img1 = view.findViewById(R.id.img_choose_6);
                    break;
                case 7:
                    img1 = view.findViewById(R.id.img_choose_7);
                    break;
                case 8:
                    img1 = view.findViewById(R.id.img_choose_8);
                    break;
                case 9:
                    img1 = view.findViewById(R.id.img_choose_9);
                    break;
                case 10:
                    img1 = view.findViewById(R.id.img_choose_10);
                    break;
                case 11:
                    img1 = view.findViewById(R.id.img_choose_11);
                    break;
            }

            if (img1 != null) {
                Log.d("vinh", "k null: ");
                Log.d("dsk", "anView: img1 " + img1.toString());
                img1.setVisibility(View.GONE);
            } else {
                Log.d("vinh", "null: ");
            }
        }
        AppConst.INSTANCE.setTabChoose(position);
        AppConst.INSTANCE.setPositionChoose(itemPosition);

    }


    private void hideAll(ImageView... imgs) {
        Log.d("khanh", "hide all: ");
        for (ImageView img : imgs) {
            if (img != null) {
                img.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LinearLayout view = (LinearLayout) object;
        container.removeView(view);
    }
}
