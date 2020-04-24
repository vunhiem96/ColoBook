
package com.volio.coloringbook2.customview.recyclical

import androidx.recyclerview.widget.DiffUtil

typealias LeftAndRightComparer = (left: Any, right: Any) -> Boolean

/** @author Aidan Follestad (@afollestad) */
class ItemDiffCallback(
  private val oldItems: List<Any>,
  private val newItems: List<Any>,
  private val areTheSame: LeftAndRightComparer,
  private val areContentsTheSame: LeftAndRightComparer
) : DiffUtil.Callback() {

  override fun areItemsTheSame(
    oldItemPosition: Int,
    newItemPosition: Int
  ): Boolean {
    val oldItem = oldItems[oldItemPosition]
    val newItem = newItems[newItemPosition]
    return areTheSame(oldItem, newItem)
  }

  override fun getOldListSize(): Int = oldItems.size

  override fun getNewListSize(): Int = newItems.size

  override fun areContentsTheSame(
    oldItemPosition: Int,
    newItemPosition: Int
  ): Boolean {
    val oldItem = oldItems[oldItemPosition]
    val newItem = newItems[newItemPosition]
    return areContentsTheSame(oldItem, newItem)
  }
}
