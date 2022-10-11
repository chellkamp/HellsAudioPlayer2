package com.hellscode.hellsaudioplayer.catalog

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView

import kotlin.math.abs
import kotlin.math.min

/**
 * Observable synchronized collection of objects.  This does not implement the ObservableList
 * interface.  Instead, it implements the [ResultCollection.OnChangedListener] interface, a smaller
 * one designed to work better with [RecyclerView.Adapter].
 *
 */
class ResultCollection<T>(initialCapacity: Int) {
    private val _objLock = Any()
    private val _coll = ObservableArrayList<T>()

    init {
        synchronized(_objLock){
            _coll.ensureCapacity(initialCapacity)
        }
    }

    @Suppress("unused")
    val size: Int
        get() {
            synchronized(_objLock) {
                return _coll.size
            }
        }

    @Suppress("unused")
    fun get(index: Int): T {
        synchronized(_objLock) {
            return _coll[index]
        }
    }

    @Suppress("unused")
    fun set(index: Int, element: T): T {
        synchronized(_objLock) {
            return _coll.set(index, element)
        }
    }

    @Suppress("unused")
    fun add(element: T): Boolean {
        synchronized(_objLock) {
            return _coll.add(element)
        }
    }

    @Suppress("unused")
    fun add(index: Int, element: T) {
        synchronized(_objLock) {
            _coll.add(index, element)
        }
    }

    @Suppress("unused")
    fun addAll(collection: Collection<T>): Boolean {
        synchronized(_objLock) {
            return _coll.addAll(collection)
        }
    }

    @Suppress("unused")
    fun addAll(index: Int, collection: Collection<T>): Boolean {
        synchronized(_objLock) {
            return _coll.addAll(index, collection)
        }
    }

    @Suppress("unused")
    fun clear() {
        synchronized(_objLock) {
            _coll.clear()
        }
    }

    @Suppress("unused")
    fun remove(element: T) {
        synchronized(_objLock) {
            _coll.remove(element)
        }
    }

    @Suppress("unused")
    fun forEach(f: (T)->Unit) {
        synchronized(_objLock) {
            for (element: T in _coll) {
                f(element)
            }
        }
    }

    @Suppress("unused")
    fun addOnChangedCallback(callback: OnChangedListener<T>) {
        synchronized(_objLock) {
            _coll.addOnListChangedCallback(CallbackMediator(callback))
        }
    }

    @Suppress("unused")
    fun removeOnChangedCallback(callback: OnChangedListener<T>) {
        synchronized(_objLock) {
            _coll.removeOnListChangedCallback(CallbackMediator(callback))
        }
    }

    interface OnChangedListener<T> {
        fun onDataSetChanged(sender: ResultCollection<T>)
        fun onItemRangeChanged(sender: ResultCollection<T>, positionStart: Int, itemCount: Int)
        fun onItemRangeInserted(sender: ResultCollection<T>, positionStart: Int, itemCount: Int)
        fun onItemRangeRemoved(sender: ResultCollection<T>, positionStart: Int, itemCount: Int)
    }

    /**
     * Serves as a bridge between the inner collection's listener and the listener methods
     * for the outer class.
     */
    private inner class CallbackMediator(callback: OnChangedListener<T>):
        ObservableList.OnListChangedCallback<ObservableList<T>>(){
        private val _callback: OnChangedListener<T> = callback

        override fun equals(other: Any?): Boolean {
            var retVal = false

            val castOther: ResultCollection<*>.CallbackMediator? = other as ResultCollection<*>.CallbackMediator?

            if (castOther != null) {
                retVal = castOther._callback == _callback
            }
            return retVal
        }

        override fun hashCode(): Int {
            return _callback.hashCode()
        }

        /**
         * Called whenever a change of unknown type has occurred, such as the entire list being
         * set to new values.
         *
         * @param sender The changing list.
         */
        override fun onChanged(sender: ObservableList<T>?) {
            _callback.onDataSetChanged(this@ResultCollection)
        }

        /**
         * Called whenever one or more items in the list have changed.
         * @param sender The changing list.
         * @param positionStart The starting index that has changed.
         * @param itemCount The number of items that have changed.
         */
        override fun onItemRangeChanged(
            sender: ObservableList<T>?,
            positionStart: Int,
            itemCount: Int
        ) {
            _callback.onItemRangeChanged(this@ResultCollection, positionStart, itemCount)
        }

        /**
         * Called whenever items have been inserted into the list.
         * @param sender The changing list.
         * @param positionStart The insertion index
         * @param itemCount The number of items that have been inserted
         */
        override fun onItemRangeInserted(
            sender: ObservableList<T>?,
            positionStart: Int,
            itemCount: Int
        ) {
            _callback.onItemRangeInserted(this@ResultCollection, positionStart, itemCount)
        }

        /**
         * Called whenever items in the list have been moved.
         * @param sender The changing list.
         * @param fromPosition The position from which the items were moved
         * @param toPosition The destination position of the items
         * @param itemCount The number of items moved
         */
        override fun onItemRangeMoved(
            sender: ObservableList<T>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            val changedStart = min(fromPosition, toPosition)
            val changedCount = abs(toPosition - fromPosition) + itemCount
            _callback.onItemRangeChanged(this@ResultCollection, changedStart, changedCount)
        }

        /**
         * Called whenever items in the list have been deleted.
         * @param sender The changing list.
         * @param positionStart The starting index of the deleted items.
         * @param itemCount The number of items removed.
         */
        override fun onItemRangeRemoved(
            sender: ObservableList<T>?,
            positionStart: Int,
            itemCount: Int
        ) {
            _callback.onItemRangeRemoved(this@ResultCollection, positionStart, itemCount)
        }
    }
}