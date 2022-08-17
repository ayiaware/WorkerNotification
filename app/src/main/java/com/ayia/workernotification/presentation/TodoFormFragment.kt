package com.ayia.workernotification.presentation

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ayia.workernotification.*
import com.ayia.workernotification.databinding.FragmentTodoFormBinding
import com.ayia.workernotification.domain.Event
import com.ayia.workernotification.framework.TodoApp
import com.ayia.workernotification.framework.TodoInteractors
import com.ayia.workernotification.util.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import timber.log.Timber
import java.util.*


class TodoFormFragment : Fragment() {

    private val myTag: String = GLOBAL_TAG + " " + TodoFormFragment::class.java.simpleName

    private var todoId: Int = 0

    private var miShare: MenuItem? = null

    val todoInteractors: TodoInteractors = TodoApp.instance.getTodoInteractions()

    private val viewModel: TodoFormViewModel by viewModels {

        TodoFormViewModelFactory(
            requireActivity().application, todoInteractors, todoId
        )

    }

    private var _binding: FragmentTodoFormBinding? = null

    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            todoId = TodoFormFragmentArgs.fromBundle(it).todoId
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTodoFormBinding.inflate(inflater, container, false)

        binding.action = if (todoId == 0) ACTION_NEW else ACTION_EDIT

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setActionBarTitle()

        binding.viewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner


        binding.btnAdd.setOnClickListener {
            viewModel.onBtnClicked()
             findNavController().navigateUp()
        }


        binding.cardDate.setOnClickListener { showRemainderDialog() }

        binding.cardTime.setOnClickListener {

            val cal =
                if (viewModel.isNewTodo) toCalender(viewModel.deadline.value!!) else Calendar.getInstance()

            timePickerDialog(
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)
            )
        }

        if (viewModel.isEditTodo) {

            viewModel.todoObs.observe(viewLifecycleOwner) {
                //Observe to-do to know when it is deleted
                //so as to activate on back pressed
                if (it == null) {
                    findNavController().navigateUp()
                }
            }
        }

        if (!viewModel.isNewTodo) {

            //edit the to-do freely and share if changes have not been made
            //Observe changes when user is editing to know if user can share todo, you should not
            //allow user to share when changes have been made to a to-do
            //Only observe when in edit mode i.e when todoId != 0

            viewModel.isTodoChangedObs.observe(viewLifecycleOwner) { isTodoChanged ->
                miShare?.let {
                    it.isVisible = !isTodoChanged
                }
            }


        }


        viewModel.reminderErrorEvent.observe(viewLifecycleOwner) { event: Event<Int>? ->

            event?.getContentIfNotHandledOrReturnNull()?.let {
                showToast(it)
            }

        }


    }


    private fun showToast(strId: Int) {
        Toast.makeText(requireContext(), getString(strId), Toast.LENGTH_SHORT).show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_todo, menu)

        miShare = menu.findItem(R.id.miShare)

        val miDelete = menu.findItem(R.id.miDelete)

        Timber.tag(myTag).d("onCreateOptionsMenu")

        when (todoId) {
            0 -> {
                miDelete.isVisible = false
                miShare!!.isVisible = false
            }
            else -> {
                miDelete.isVisible = true
                miShare!!.isVisible = true
            }

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return true // must return true to consume it here

            }
            R.id.miDelete -> {
                showActionSnack()
            }

            R.id.miShare -> {
                shareText()
            }

        }

        return super.onOptionsItemSelected(item)

    }

    private fun showActionSnack(
    ) {

        Timber.tag(myTag).d("showActionSnack")

        val msg: Int = R.string.msg_delete_selection
        val btnTxt: Int = R.string.label_delete


        Snackbar.make(
            requireActivity().findViewById(android.R.id.content), msg,
            Snackbar.LENGTH_LONG
        ).setAction(btnTxt) {
           viewModel.deleteTodo()

        }.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setActionBarTitle() {
        (activity as MainActivity).supportActionBar?.title =
                getString(
                    if (viewModel.isNewTodo) R.string.label_new else R.string.label_edit
                ) + " " + getString(
                    R.string.label_todo
                ).lowercase()

    }

    private fun shareText() {

        if (viewModel.title.value.isNullOrBlank()
            || viewModel.title.value.isNullOrEmpty()
        )
            Toast.makeText(
                requireContext(), R.string.msg_empty_text,
                Toast.LENGTH_SHORT
            ).show()
        else {

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, getTextToShare(
                        viewModel.todo!!, listOf(
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



    private fun showRemainderDialog() {

        val date =
            if (viewModel.deadline.value == null) System.currentTimeMillis() else viewModel.deadline.value


        val calendarConstraintBuilder = CalendarConstraints.Builder()
        calendarConstraintBuilder.setStart(date!!)
        calendarConstraintBuilder.setValidator(DateValidatorPointForward.now())

        val materialDatePickerBuilder = MaterialDatePicker.Builder.datePicker()
        materialDatePickerBuilder.setTitleText("Select day")

        materialDatePickerBuilder.setCalendarConstraints(calendarConstraintBuilder.build())

        val materialDatePicker = materialDatePickerBuilder.build()
        materialDatePicker.show(requireActivity().supportFragmentManager, "DatePicker")

        materialDatePicker.addOnPositiveButtonClickListener { selection: Long? ->
            // TimeZone.getTimeZone("UTC")
            val cal = Calendar.getInstance()

            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            cal.timeInMillis = selection!!

            timePickerDialog(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                hour,
                minute
            )

        }
    }

    private fun timePickerDialog(year: Int, month: Int, day: Int, hour: Int, minute: Int) {

        Timber.tag(myTag).d("timePickerDialog")

        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H


        // instance of MDC time picker
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            // set the title for the alert dialog
            .setTitleText("SELECT YOUR TIMING")
            // set the default hour for the
            // dialog when the dialog opens
            .setHour(hour)
            // set the default minute for the
            // dialog when the dialog opens
            .setMinute(minute + 5)
            // set the time format
            // according to the region
            .setTimeFormat(clockFormat)
            .build()



        materialTimePicker.show(requireActivity().supportFragmentManager, "TimePickerDialog")

        // on clicking the positive button of the time picker
        // dialog update the TextView accordingly
        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute

            val pickedDateTime = Calendar.getInstance()

            pickedDateTime.set(year, month, day, pickedHour, pickedMinute)

            viewModel.setDeadline(pickedDateTime.timeInMillis)

            val isFutureTime = pickedDateTime.timeInMillis.compareTo(System.currentTimeMillis())

            Timber.tag(myTag).d("isFuture time %s", isFutureTime)

            Timber.tag(myTag).d("Date time %s", toDateTimeString(pickedDateTime.timeInMillis))


        }
    }


}