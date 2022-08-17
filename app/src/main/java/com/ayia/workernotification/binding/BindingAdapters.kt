package com.ayia.workernotification.binding

import android.text.format.DateUtils.*
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.ayia.workernotification.util.ACTION_NEW
import com.ayia.workernotification.R
import com.ayia.workernotification.domain.Todo


@BindingAdapter("isNotGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("isNotInvisible")
fun showInvisible(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("setDrawable")
fun setDrawable(imageView: ImageView, drawableId: Int) {
    imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, drawableId))
}


@BindingAdapter(value = ["todoInfo", "action"], requireAll = true)
fun setTodoInfo(tv: TextView, reminder: Long?, action: String) {

    if (reminder != null) {

        tv.visibility = View.VISIBLE

        if (reminder.compareTo(System.currentTimeMillis()) != 1) {

            if (action == ACTION_NEW)
                tv.setText(R.string.msg_past_date)
            else
                tv.text = getRelativeTimeSpanString(
                    reminder, System.currentTimeMillis(), MINUTE_IN_MILLIS, FORMAT_ABBREV_RELATIVE
                )

            tv.background = ContextCompat.getDrawable(tv.context, R.drawable.badge_deadline_past)

        } else {

            tv.background = ContextCompat.getDrawable(tv.context, R.drawable.badge_info)

            tv.text = getRelativeTimeSpanString(
                reminder, System.currentTimeMillis(), MINUTE_IN_MILLIS, FORMAT_ABBREV_RELATIVE
            )

            val isOverdue: Boolean = System.currentTimeMillis() > reminder

            val textColor = if (isOverdue) R.color.text_overdue else R.color.text_due
            tv.setTextColor(ContextCompat.getColor(tv.context, textColor))
        }

    } else {
        tv.visibility = View.GONE
    }

}

@BindingAdapter("todoInfo")
fun setTodoInfo(tv: TextView, todo: Todo) {

    val reminder = todo.deadline

    if (reminder != null) {

        tv.visibility = View.VISIBLE

        val isOverdue: Boolean = System.currentTimeMillis() > reminder

        tv.background = ContextCompat.getDrawable(
            tv.context,
            if (isOverdue) R.drawable.badge_deadline_past else R.drawable.badge_info
        )

        tv.text = getRelativeTimeSpanString(
            reminder, System.currentTimeMillis(),
            MINUTE_IN_MILLIS,
            FORMAT_ABBREV_RELATIVE
        )

    } else {
        tv.visibility = View.GONE
    }

}

