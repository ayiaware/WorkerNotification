package com.ayia.workernotification.presentation

import android.app.Application
import androidx.lifecycle.*
import com.ayia.workernotification.util.GLOBAL_TAG
import com.ayia.workernotification.R
import com.ayia.workernotification.domain.Event
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.framework.TodoInteractors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class TodoFormViewModel(
    application: Application,
    private val interactors: TodoInteractors,
    val id: Int
) : AndroidViewModel(application) {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val myTag: String = GLOBAL_TAG + " " + TodoFormViewModel::class.java.simpleName

    private val _validForm = MediatorLiveData<Boolean>()
    val validForm: LiveData<Boolean> = _validForm

    val title = MutableLiveData<String?>()

    val deadline = MutableLiveData<Long?>()

    val done = MutableLiveData<Boolean>()

    val reminderOn = MutableLiveData<Boolean>()

    val isNewTodo = id == 0

    val isEditTodo = id != 0

    val isLoading = MutableLiveData(!isNewTodo)

    var todo: Todo? = null

    //Observe to-do to know when it is trashed or deleted so as to activate on back pressed
    val todoObs: LiveData<Todo?> = interactors.getTodoObs.invoke(id)

    //Observe to-do changes when user is editing to know if user can share to-do
    val isTodoChangedObs = MutableLiveData(false)

    private val _updated = MutableLiveData<Long>()
    val updated: MutableLiveData<Long> get() = _updated

    init {

        if (id != 0) {

            Timber.tag(myTag).d("todo != null ${true}")

            viewModelScope.launch(ioDispatcher) {

                interactors.getTodo.invoke(id).let { result ->

                    todo = result
                    onTodoLoaded()

                }

            }

        } else {
            addSources()
        }

    }


    private suspend fun onTodoLoaded() {

        withContext(Dispatchers.Main) {

            todo?.let { todo ->

                title.value = todo.title

                _updated.value = todo.updated!!

                deadline.value = todo.deadline

                done.value = todo.isDone

                reminderOn.value = todo.isReminderOn


            }

            isLoading.value = false

            addSources()

        }

    }


    private fun addSources() {
        _validForm.addSource(title) { validateForm() }
        _validForm.addSource(deadline) { validateForm() }
        _validForm.addSource(done) { validateForm() }
        _validForm.addSource(reminderOn) { validateForm() }
    }

    private fun validateForm() {

        //Timber.tag(myTag).d("validForm reminder isValidReminder ${isValidReminder()}" )

        _validForm.value =
            !title.value.isNullOrBlank() && !title.value.isNullOrEmpty() && isValidDeadline()
                    && when (todo) {
                null -> true
                else -> isTodoChanged()
            }
    }


    fun setDeadline(long: Long) {
      deadline.value = long
      reminderOn.value = true

    // Deadline.value wont update on time for reminder check
    // setReminderOn(true)
    }

    val reminderErrorEvent = MutableLiveData<Event<Int>>()

    fun setReminderOn(isOn: Boolean) {

        Timber.tag(myTag).d("setReminderOn isOn $isOn ${deadline.value}")

        if (isOn && deadline.value == null) {
            reminderErrorEvent.value = Event(R.string.msg_error_reminder_on_no_deadline)
            reminderOn.value = false
        }
        else
            reminderOn.value = isOn
    }


    private fun isTodoChanged(): Boolean {

        val changed =
            isTitleChanged() || isRemainderChanged() || isDoneChanged() || isReminderOnChanged()

        Timber.tag(myTag).d("isTodoChanged $changed")

        isTodoChangedObs.value = changed

        return changed

    }

    private fun isValidDeadline(): Boolean {

        return if (deadline.value == null || todo != null)
            true
        else
            deadline.value!!.compareTo(System.currentTimeMillis()) == 1

    }

    fun onCheckBoxClicked(checked: Boolean) {
        done.value = checked
    }


    fun removeDeadline() {
        deadline.value = null
        setReminderOn(false)
    }

    private fun isRemainderChanged(): Boolean {
        return deadline.value != todo?.deadline
    }

    private fun isDoneChanged(): Boolean {
        return done.value != todo?.isDone
    }

    private fun isReminderOnChanged(): Boolean {
        return reminderOn.value != todo?.isReminderOn
    }

    private fun isTitleChanged(): Boolean {

        var t: String? = null

        when {
            !title.value.isNullOrEmpty() && !title.value.isNullOrBlank() -> t = title.value
        }

        val isChanged = t != todo?.title

        Timber.tag(myTag).d("isTitleChanged $isChanged Title ${todo?.title}")

        return isChanged
    }

    fun onBtnClicked() {

        val newTodo: Todo

        val title = title.value

        val isReminderOn =  reminderOn.value == true
        val deadline =  deadline.value

//        val isReminderOn = true
//        val deadline = System.currentTimeMillis().plus(2000L)

        val isDone = done.value == true

        if (todo == null) {

            newTodo =
                Todo(title = title ,
                    deadline = deadline,
                    isReminderOn = isReminderOn,
                    isDone = isDone)

        } else {

            todo!!.title = title
            todo!!.updated = System.currentTimeMillis()
            todo!!.deadline = deadline
            todo!!.isDone = isDone
            todo!!.isReminderOn = isReminderOn

            newTodo = todo as Todo
        }

        viewModelScope.launch {

            if (todo == null) {
                insertTodo(newTodo)
            } else {
                updateTodo(newTodo)
            }
        }
    }


    private suspend fun insertTodo(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        interactors.insertTodo.invoke(todo)
    }

    private suspend fun updateTodo(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        interactors.updateTodo.invoke(todo)
    }

    fun deleteTodo() {
        viewModelScope.launch {
            todoObs.value?.let {
                interactors.deleteTodo.invoke(it)
            }
        }
    }


    override fun onCleared() {
        _validForm.removeSource(title)
        _validForm.removeSource(deadline)
        _validForm.removeSource(done)
        _validForm.removeSource(reminderOn)
        super.onCleared()
    }

}