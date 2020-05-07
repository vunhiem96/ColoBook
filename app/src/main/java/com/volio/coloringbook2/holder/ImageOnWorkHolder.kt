/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.volio.coloringbook2.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.volio.coloringbook2.R

class ImageOnWorkHolder(itemView: View) : ViewHolder(itemView) {
  val img1: ImageView = itemView.findViewById(R.id.img_list_on_work)
  val txtPercent: TextView = itemView.findViewById(R.id.txt_percent_my_work)
  val progressBar: ContentLoadingProgressBar = itemView.findViewById(R.id.progress_image_my_work)
  val imgDone: ImageView = itemView.findViewById(R.id.img_done_image)
  val share: ImageView = itemView.findViewById(R.id.share)
  val delete: ImageView = itemView.findViewById(R.id.delete)
  val tv_finish: TextView = itemView.findViewById(R.id.tv_finish)


}
