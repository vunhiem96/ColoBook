
@file:Suppress("unused")

package com.volio.coloringbook2.customview.recyclical

/**
 * Returns true if the [DataSource] is a [SelectableDataSource] and the item at the given [index]
 * is currently selected.
 */
fun <IT : Any> ItemDefinition<IT>.isSelectedAt(index: Int): Boolean {
  return selectableDataSource?.isSelectedAt(index) ?: false
}

/**
 * If the [DataSource] is a [SelectableDataSource], selects the item at the given [index].
 */
fun <IT : Any> ItemDefinition<IT>.selectAt(index: Int): Boolean {
  return selectableDataSource?.selectAt(index) ?: false
}

/**
 * If the [DataSource] is a [SelectableDataSource], deselects the item at the given [index].
 */
fun <IT : Any> ItemDefinition<IT>.deselectAt(index: Int): Boolean {
  return selectableDataSource?.deselectAt(index) ?: false
}

/**
 * The [DataSource] must be a [SelectableDataSource]. If the item at the given [index] is selected,
 * it is deselected. If it's not selected, then it is selected.
 */
fun <IT : Any> ItemDefinition<IT>.toggleSelectionAt(index: Int): Boolean {
  return selectableDataSource?.toggleSelectionAt(index) ?: false
}

/**
 * Returns true if the [DataSource] is a [SelectableDataSource] and at least one item is selected.
 */
fun <IT : Any> ItemDefinition<IT>.hasSelection(): Boolean {
  return selectableDataSource?.hasSelection() ?: false
}

private val ItemDefinition<*>.selectableDataSource: SelectableDataSource?
  get() = setup.dataSource as? SelectableDataSource
