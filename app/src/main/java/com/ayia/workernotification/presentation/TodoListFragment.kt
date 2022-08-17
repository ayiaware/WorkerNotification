package com.ayia.workernotification.presentation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.ayia.workernotification.*
import com.ayia.workernotification.databinding.FragmentTodoListBinding
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.framework.TodoApp
import com.ayia.workernotification.framework.TodoInteractors
import com.ayia.workernotification.util.GLOBAL_TAG
import com.ayia.workernotification.util.getTextToShare
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import timber.log.Timber

class TodoListFragment : Fragment() {

    val todoInteractors: TodoInteractors = TodoApp.instance.getTodoInteractions()

    private val myTag: String = GLOBAL_TAG + " " + TodoListFragment::class.java.simpleName

    private val mainViewModel: MainViewModel by activityViewModels()

    private val viewModel: TodoListViewModel by viewModels {
        TodoListViewModelFactory(todoInteractors)
    }

    private var _binding: FragmentTodoListBinding? = null


    private var _adapter: TodosAdapter? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val todosAdapter get() = _adapter!!

    var mActionMode: ActionMode? = null
    var isActionMode = false

    var miDelete: MenuItem? = null
    var miShare: MenuItem? = null
    var miEdit: MenuItem? = null

    var selectedTodo: Todo? = null
    var selectedCount = 0

    override fun onResume() {
        super.onResume()
        Timber.tag(myTag).d("onResume")
    }

    override fun onPause() {
        super.onPause()
        Timber.tag(myTag).d("onPause")
    }

    override fun onStart() {
        super.onStart()
        Timber.tag(myTag).d("onPause")
    }

    override fun onStop() {
        Timber.tag(myTag).d("onStop")
        super.onStop()
        disableActionMode()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentTodoListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.viewmodel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()

        submitListsToUi(mainViewModel.todos)

        setToolBarTitle()

    }

    private fun setToolBarTitle() {
        (activity as MainActivity).supportActionBar?.title = getString(R.string.label_todos)
    }


    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerviewTransform

        _adapter = TodosAdapter(todoClickCallback, viewModel)

        recyclerView.adapter = _adapter

    }



    private fun submitListsToUi(
        todosObs: LiveData<List<Todo>?>
    ) {

        todosObs.observe(viewLifecycleOwner) {

            binding.isEmpty = it?.isEmpty()


            it?.let {

                todosAdapter.submitList(it)
            }

        }


    }


    private val todoClickCallback: TodoClickCallback = object : TodoClickCallback {

        override fun onClick(todo: Todo, position: Int) {

            selectedTodo = todo

            if (mActionMode != null) {
                enableActionMode(position)

            } else {

                goToTodoForm()
            }

        }

        override fun onLongClick(todo: Todo, position: Int): Boolean {
            selectedTodo = todo
            enableActionMode(position)
            return true
        }

    }


    override fun onDestroyView() {

        Timber.tag(myTag).d("onDestroyView")
        super.onDestroyView()
        _binding = null
        setAllAdaptersToNull()
    }

    private fun setAllAdaptersToNull() {
        _adapter = null

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        Timber.tag(myTag).d("onCreateOptionsMenu")

        inflater.inflate(R.menu.menu_todo, menu)
        val searchItem = menu.findItem(R.id.miSearch)
        val searchView = SearchView(requireActivity())


        mainViewModel.searchQueryTodo.observe(viewLifecycleOwner) {

            Timber.tag(myTag)
                .d("searchQueryObs $it searchQuery ${searchView.query} == ${it == searchView.query}")

        }

        searchView.queryHint = getString(R.string.label_search)
        searchView.maxWidth = Int.MAX_VALUE

        var searchJob: Job? = null

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                Timber.tag(myTag).d("onQueryTextChange")

                searchJob?.cancel()

                searchJob = MainScope().launch {

                    delay(400L)

                    mainViewModel.setQuery(newText)

                }

                return true
            }
        })

        searchItem.actionView = searchView

        val query = mainViewModel.searchQueryTodo.value

        searchItem.isVisible = true

        if (!TextUtils.isEmpty(query)) {
            searchView.setQuery(query, false)
            searchView.isFocusable = true
            searchView.isIconified = false
            searchView.requestFocusFromTouch()
        }

    }


    private val mActionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_todo_context, menu)

            miShare = menu.findItem(R.id.miShare)
            miDelete = menu.findItem(R.id.miDelete)
            miEdit = menu.findItem(R.id.miEdit)


            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {

            when (item.itemId) {
                R.id.miDelete -> {
                    deleteTodos(todosAdapter.getSelected())
                    mode.finish() // Action picked, so close the CAB
                    return true
                }
                R.id.miShare -> {
                    shareText()
                    mode.finish() // Action picked, so close the CAB
                    return true
                }
                R.id.miEdit -> {

                    goToTodoForm()

                    mode.finish() // Action picked, so close the CAB
                    return true
                }
                else -> return false
            }
        }

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode) {
            disableActionMode()
            todosAdapter.removeSelections()

        }
    }

    private fun enableActionMode(position: Int) {

        if (mActionMode == null) {

            requireActivity()
            mActionMode =
                (requireActivity() as AppCompatActivity).startSupportActionMode(mActionModeCallback)
            isActionMode = true

        }

        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {

        todosAdapter.toggleSelection(position)

        selectedCount = todosAdapter.selectedItemCount

        miShare?.isVisible = false


        if (selectedCount == 1) {
            miShare?.isVisible = true
            miEdit?.isVisible = true

            val text = selectedTodo!!.title!!.lowercase()

            if (text != "" && text.length > 10)
                mActionMode?.title = text.substring(0, 10) + "..." else mActionMode?.title = text

        } else if (selectedCount > 1) {
            miShare?.isVisible = false
            miEdit?.isVisible = false
            mActionMode?.title = selectedCount.toString() + " " + getString(R.string.label_selected)
        } else {
            disableActionMode()
        }


        if (mActionMode != null)
            mActionMode?.invalidate()

    }

    //Set action mode null after use
    fun disableActionMode() {

        mActionMode?.let {
            it.finish()
            removeSelections()
        }

        mActionMode = null

    }

    private fun removeSelections() {
        selectedCount = 0
        selectedTodo = null
    }


    private fun deleteTodos(todos: List<Todo>) {
        showActionSnack(todos)

    }

    private fun showActionSnack(
        todos: List<Todo>
    ) {

        val msg: Int = if (selectedCount == 1)
            R.string.msg_delete_selection
        else
            R.string.msg_delete_selections

        val btnTxt: Int = R.string.label_delete



        Snackbar.make(
            requireActivity().findViewById(android.R.id.content), msg,
            Snackbar.LENGTH_LONG
        ).setAction(btnTxt) {

            viewModel.deleteTodos(todos)

        }.show()
    }


    private fun shareText() {

        val isValidShare = selectedTodo!!.title

        if (isValidShare.isNullOrBlank() || isValidShare.isNullOrEmpty())
            Toast.makeText(
                requireContext(), R.string.msg_empty_text,
                Toast.LENGTH_SHORT
            ).show()
        else {
            val sendIntent: Intent = Intent().apply {

                action = Intent.ACTION_SEND

                putExtra(
                    Intent.EXTRA_TEXT, getTextToShare(
                        selectedTodo!!, listOf(
                            getString(R.string.label_todo),
                            getString(R.string.label_done),
                            getString(R.string.label_not_done)
                        )
                    )
                )

                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

    }

    private fun goToTodoForm() {

        val navController = findNavController()

        navController.navigate(
            TodoListFragmentDirections.actionTodoListFragmentToTodoFormFragment(
                selectedTodo!!.id
            )

        )

    }


}