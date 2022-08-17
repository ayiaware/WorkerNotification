package com.ayia.workernotification.presentation

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.databinding.RowTodoBinding

/**
 * Adapter for the task list. Has a reference to the [TodoListViewModel] to send actions back to it.
 */
class TodosAdapter(
    private val clickCallback: TodoClickCallback,
    private val viewModel: TodoListViewModel
) :
    ListAdapter<Todo, TodosAdapter.ViewHolder>(TodoDiffCallback()) {

    private var list: List<Todo>? = null

    private var selectedIdsSBArray: SparseBooleanArray? = null


    override fun submitList(list: List<Todo>?) {

        this.list = list
        selectedIdsSBArray = SparseBooleanArray()

        super.submitList(list)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, position, item, clickCallback)

        holder.foreground.visibility =
            if (selectedIdsSBArray!![position]) View.VISIBLE else View.GONE

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: RowTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val foreground = binding.layoutForeground

        fun bind(
            viewModel: TodoListViewModel,
            position: Int,
            todo: Todo,
            clickCallback: TodoClickCallback
        ) {

            binding.viewModel = viewModel
            binding.position = position
            binding.todo = todo
            binding.clickCallback = clickCallback
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowTodoBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }


    /***
     * Methods required for do selections, remove selections, etc.
     */
    //Toggle selection methods
    fun toggleSelection(position: Int) {
        selectView(position)
        notifyDataSetChanged()

    }


    //Put or delete selected position into SparseBooleanArray
    private fun selectView(position: Int) {
        if (selectedIdsSBArray!![position, false]) {
            selectedIdsSBArray!!.delete(position)
        } else {
            selectedIdsSBArray!!.put(position, true)
        }
        notifyItemChanged(position)
    }

    fun removeSelections() {
        selectedIdsSBArray!!.clear()
        notifyDataSetChanged()
    }


    //Return all selected ids
    fun getSelectedIds(): IntArray {

        val ids = IntArray(selectedItemCount)

        for (i in 0 until selectedItemCount) {
            ids[i] = list!![selectedIdsSBArray!!.keyAt(i)].id
        }
        return ids
    }

    //Get total selected count
    val selectedItemCount: Int
        get() = selectedIdsSBArray!!.size()

    fun getSelected(): List<Todo> {

        val items: MutableList<Todo> = ArrayList(selectedItemCount)

        for (i in 0 until selectedItemCount) {
            items.add(list!![selectedIdsSBArray!!.keyAt(i)])
        }
        return items
    }


}

