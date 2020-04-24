
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.volio.coloringbook2.customview.recyclical

import android.view.View
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter

/**
 * Provides a data set for a RecyclerView to bind and display.
 *
 * @author Aidan Follestad (@afollestad)
 */
interface DataSource {

    /** Retrieves an item at a given index from the data source */
    operator fun get(index: Int): Any

    /** Appends an item to the data source; calls [add] in its default implementation. */
    operator fun plusAssign(item: Any) {
        add(item)
    }

    /** Removes an item from the data source; calls [remove] in its default implementation. */
    operator fun minusAssign(item: Any) {
        remove(item)
    }

    /** Returns true if the data source contains the given item. */
    operator fun contains(item: Any): Boolean

    /** Returns an iterator to loop over all items in the data source. */
    operator fun iterator(): Iterator<Any>

    /**
     * Attaches the data source to an empty view and adapter. This doesn't need to be manually
     * called.
     */
    @RestrictTo(LIBRARY)
    fun attach(
        emptyView: View?,
        adapter: DefinitionAdapter?
    )

    /**
     * Detaches the data source, clearing up references to anything that can leak.
     */
    @RestrictTo(LIBRARY)
    fun detach()

    /**
     * Replaces the whole contents of the data source. If [areTheSame] AND [areContentsTheSame] are
     * both provided, [DiffUtil] will be used.
     */
    fun set(
        newItems: List<Any>,
        areTheSame: LeftAndRightComparer? = null,
        areContentsTheSame: LeftAndRightComparer? = null
    )

    /** Appends an item to the data source. */
    fun add(item: Any)

    /** Inserts an item into the dats source at a given index. */
    fun insert(
        index: Int,
        item: Any
    )

    /** Update an item into the dats source at a given index. */
    fun updateItem(
        index: Int,
        item: Any
    )

    /** Removes an item from the data source at a given index. */
    fun removeAt(index: Int)

    /** Removes a given item from the data source, if it exists. */
    fun remove(item: Any)

    /** Swaps two items at given indices in the data source. */
    fun swap(
        left: Int,
        right: Int
    )

    /** Moves an item to another index in the data source. */
    fun move(
        from: Int,
        to: Int
    )

    /** Clears all items from the data source, making it empty. */
    fun clear()

    /** Returns how many items are in the data source. */
    fun size(): Int

    /** Returns true if the data source is empty. */
    fun isEmpty(): Boolean = size() == 0

    /** Returns true if the data source is NOT empty. */
    fun isNotEmpty(): Boolean = !isEmpty()

    /** Calls [block] for each item in the data source. */
    fun forEach(block: (Any) -> Unit)

    /** Returns the index of the first item matching the given predicate. -1 if none. */
    fun indexOfFirst(predicate: (Any) -> Boolean): Int

    /** Returns the index of the last item matching the given predicate. -1 if none. */
    fun indexOfLast(predicate: (Any) -> Boolean): Int

    /** Returns the index of the first item that equals the given. -1 if none. */
    fun indexOf(item: Any): Int = indexOfFirst { it == item }

    /** Used by other [DataSource] implementations to notify that an item has changed state. */
    fun invalidateAt(index: Int)

    /** Used by other [DataSource] implementations to notify that the whole data set has changed state. */
    fun invalidateAll()
}

/** @author Aidan Follestad (@afollestad) */
open class RealDataSource internal constructor(
    initialData: List<Any> = mutableListOf()
) : DataSource {
    private var items = if (initialData is MutableList) {
        initialData
    } else {
        initialData.toMutableList()
    }
    private var adapter: DefinitionAdapter? = null
    private var emptyView: View? = null
    private var isAttached: Boolean = false

    override operator fun get(index: Int): Any = items[index]

    override operator fun contains(item: Any): Boolean = items.contains(item)

    override operator fun iterator(): Iterator<Any> = items.iterator()

    override fun attach(
        emptyView: View?,
        adapter: DefinitionAdapter?
    ) {
        if (this.isAttached) return
        this.isAttached = true

        this.emptyView = emptyView
        this.adapter = adapter
        notify { notifyDataSetChanged() }
    }

    override fun detach() {
        this.adapter = null
        this.emptyView = null
        this.isAttached = false
    }

    override fun add(item: Any) {
        items.add(item)
        notify { notifyItemInserted(items.size - 1) }
    }

    override fun updateItem(
        index: Int,
        item: Any
    )  {

        items[index] = item
        notify { notifyItemChanged(index) }
    }

    override fun set(
        newItems: List<Any>,
        areTheSame: LeftAndRightComparer?,
        areContentsTheSame: LeftAndRightComparer?
    ) {
        if (this.items.isNotEmpty() && areTheSame != null && areContentsTheSame != null) {
            val oldItems = this.items
            this.items = newItems.toMutableList()

            val diffCallback = ItemDiffCallback(
                oldItems = oldItems,
                newItems = items,
                areTheSame = areTheSame,
                areContentsTheSame = areContentsTheSame
            )
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            adapter?.let { diffResult.dispatchUpdatesTo(it) }
        } else {
            this.items = newItems.toMutableList()
            notify { notifyDataSetChanged() }
        }
    }

    override fun insert(
        index: Int,
        item: Any
    ) {
        items.add(index, item)
        notify { notifyItemInserted(index) }
    }

    override fun removeAt(index: Int) {
        items.removeAt(index)
        notify { notifyItemRemoved(index) }
    }

    override fun remove(item: Any) {
        val index = items.indexOf(item)
        if (index == -1) return
        removeAt(index)
    }

    override fun swap(
        left: Int,
        right: Int
    ) {
        val leftItem = items[left]
        items[left] = items[right]
        items[right] = leftItem
        notify {
            notifyItemChanged(left)
            notifyItemChanged(right)
        }
    }

    override fun move(
        from: Int,
        to: Int
    ) {
        val item = items[from]
        items.removeAt(from)
        items.add(to, item)
        notify { notifyItemMoved(from, to) }
    }

    override fun clear() {
        items.clear()
        notify { notifyDataSetChanged() }
    }

    override fun size() = items.size

    override fun isEmpty() = items.isEmpty()

    override fun isNotEmpty() = items.isNotEmpty()

    override fun forEach(block: (Any) -> Unit) = items.forEach(block)

    override fun indexOfFirst(predicate: (Any) -> Boolean): Int = items.indexOfFirst(predicate)

    override fun indexOfLast(predicate: (Any) -> Boolean): Int = items.indexOfLast(predicate)

    override fun indexOf(item: Any): Int = items.indexOf(item)

    override fun invalidateAt(index: Int) {
        adapter?.notifyItemChanged(index)
    }

    override fun invalidateAll() {
        adapter?.notifyDataSetChanged()
    }

    private fun notify(block: Adapter<*>.() -> Unit) {
        adapter?.let {
            it.block()
            emptyView.showOrHide(isEmpty())
        }
    }
}

/**
 * Constructs a [DataSource] with an initial list of items of any type.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun dataSourceOf(items: List<Any>): DataSource = RealDataSource(items.toMutableList())

/**
 * Constructs a [DataSource] with an initial set of items of any type.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun dataSourceOf(vararg items: Any): DataSource = RealDataSource(items.toMutableList())

/**
 * Constructs a data source that is empty by default.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun emptyDataSource(): DataSource = RealDataSource()

/**
 * Same as [DataSource.forEach] but only emits items of a certain type.
 *
 * @author Aidan Follestad (@afollestad)
 */
inline fun <reified T : Any> DataSource.forEachOf(block: (T) -> Unit) {
    iterator()
        .asSequence()
        .filter { it is T }
        .map { it as T }
        .forEach { block(it) }
}
