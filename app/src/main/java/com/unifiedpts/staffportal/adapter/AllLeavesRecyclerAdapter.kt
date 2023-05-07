package com.unifiedpts.staffportal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.Leave


class AllLeavesRecyclerAdapter(private val list: List<Leave>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            Leave.APPROVED -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item_previous_application_approved, parent, false)
                return ApprovedViewHolder(view)
            }

            Leave.DENIED -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item_previous_application_denied, parent, false)
                return DeniedViewHolder(view)
            }

            else -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item_previous_application_pending, parent, false)
                return PendingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Leave = list[position]
        when (item.status) {
            Leave.STATUS_APPROVED -> {
                (holder as ApprovedViewHolder).leaveTypeTextView.text = item.leaveType
                holder.leaveDateTextView.text = item.fromDate + " - " + item.toDate
            }

            Leave.STATUS_DENIED -> {
                (holder as DeniedViewHolder).leaveTypeTextView.text = item.leaveType
                holder.leaveDateTextView.text = item.fromDate + " - " + item.toDate
            }

            Leave.STATUS_PENDING -> {
                (holder as PendingViewHolder).leaveTypeTextView.text = item.leaveType
                holder.leaveDateTextView.text = item.fromDate + " - " + item.toDate
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].status) {
            Leave.STATUS_APPROVED -> Leave.APPROVED
            Leave.STATUS_DENIED -> Leave.DENIED
            Leave.STATUS_PENDING -> Leave.PENDING
            else -> -1
        }
    }

    class ApprovedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leaveTypeTextView: TextView
        var leaveDateTextView: TextView

        init {
            leaveTypeTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
            leaveDateTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
        }
    }

    class DeniedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leaveTypeTextView: TextView
        var leaveDateTextView: TextView

        init {
            leaveTypeTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
            leaveDateTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
        }
    }

    class PendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leaveTypeTextView: TextView
        var leaveDateTextView: TextView

        init {
            leaveTypeTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
            leaveDateTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
        }
    }
}