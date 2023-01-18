package com.andreykaranik.journeys

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.andreykaranik.journeys.models.Itinerary
import java.text.SimpleDateFormat
import java.util.*


class PointAdapter(private val context: Context) : RecyclerView.Adapter<PointAdapter.PointViewHolder>(){
    var points: List<Itinerary.Point> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    val myCalendar = Calendar.getInstance();

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_point, parent, false)
        val pointNameEditText: EditText = itemView.findViewById(R.id.point_name_edit_text)
        val pointTimeEditText: EditText = itemView.findViewById(R.id.point_time_edit_text)

        pointNameEditText.addTextChangedListener{
            val point = pointNameEditText.tag as Itinerary.Point
            point.name = pointNameEditText.text.toString()
        }
        pointTimeEditText.addTextChangedListener {
            val point = pointTimeEditText.tag as Itinerary.Point
            point.time = pointTimeEditText.text.toString()
        }

        val date = OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                val myFormat = "dd/MM/yy"
                val dateFormat = SimpleDateFormat(myFormat, Locale.US)
                pointTimeEditText.setText(dateFormat.format(myCalendar.time))
            }
        pointTimeEditText.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                context,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        })

        return PointViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val point = points[position]
        holder.pointNameEditText.tag = point
        holder.pointTimeEditText.tag = point

        if (position == 0) {
            holder.pointNameEditText.hint = context.getString(R.string.start_point_name_hint)
            holder.pointTimeEditText.hint = context.getString(R.string.start_point_time_hint)
        } else {
            holder.pointNameEditText.hint = context.getString(R.string.point_name_hint)
            holder.pointTimeEditText.hint = context.getString(R.string.point_time_hint)
        }

        holder.pointNameEditText.setText(point.name)
        holder.pointTimeEditText.setText(point.time)
    }

    override fun getItemCount(): Int = points.size

    class PointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pointNameEditText: EditText = itemView.findViewById(R.id.point_name_edit_text)
        val pointTimeEditText: EditText = itemView.findViewById(R.id.point_time_edit_text)
    }
}