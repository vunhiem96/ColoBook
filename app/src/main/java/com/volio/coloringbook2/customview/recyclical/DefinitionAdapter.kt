@file:Suppress("UNCHECKED_CAST")

package com.volio.coloringbook2.customview.recyclical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/** @author Aidan Follestad (@afollestad) */
class DefinitionAdapter internal constructor(
    setup: RecyclicalSetup,
    private val dataSource: DataSource
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemClassToType = setup.itemClassToType
    private val bindingsToTypes = setup.bindingsToTypes

    override fun getItemViewType(position: Int): Int {
        return dataSource[position].getItemType()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return viewType.getItemDefinition()
            .createViewHolder(view)
    }

    override fun getItemCount() = dataSource.size()

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = dataSource[position]
        val viewType = item.getItemType()
        viewType.getItemDefinition()
            .bindViewHolder(holder, item, position)
    }

    private fun Any.getItemType(): Int {
        val itemClassName = this::class.java.name
        return itemClassToType[itemClassName] ?: throw IllegalStateException(
            "Didn't find type for class $itemClassName"
        )
    }

    private fun Int.getItemDefinition(): ItemDefinition<*> {
        return bindingsToTypes[this] ?: throw IllegalStateException(
            "Unable to view item definition for viewType $this"
        )
    }
}
