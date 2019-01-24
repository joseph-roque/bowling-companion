package ca.josephroque.bowlingcompanion.teams.list

import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.teams.Team

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [Team] and makes a call to the delegate.
 */
class TeamRecyclerViewAdapter(
    items: List<Team>,
    delegate: BaseRecyclerViewAdapter.AdapterDelegate<Team>?
) : BaseRecyclerViewAdapter<Team>(items, delegate) {

    companion object {
        @Suppress("unused")
        private const val TAG = "TeamRecyclerViewAdapter"

        private enum class ViewType {
            Active,
            Deleted;

            companion object {
                private val map = ViewType.values().associateBy(ViewType::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    // MARK: BaseRecyclerViewAdapter

    override fun getItemViewType(position: Int): Int {
        return if (getItemAt(position).isDeleted) {
            ViewType.Deleted.ordinal
        } else {
            ViewType.Active.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewAdapter<Team>.ViewHolder {
        return when (ViewType.fromInt(viewType)) {
            ViewType.Active -> { ViewHolderActive(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_team, parent, false))
            }
            ViewType.Deleted -> { ViewHolderDeleted(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_deleted, parent, false))
            } else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<Team>.ViewHolder, position: Int) {
        holder.bind(getItemAt(position))
    }

    // MARK: ViewHolderActive

    inner class ViewHolderActive(view: View) : BaseRecyclerViewAdapter<Team>.ViewHolder(view) {
        private val tvName: TextView? = view.findViewById(R.id.tv_name)
        private val chipGroupMembers: ChipGroup? = view.findViewById(R.id.cg_members)

        override fun bind(item: Team) {
            val context = itemView.context
            tvName?.text = item.name

            chipGroupMembers?.removeAllViews()
            item.members.forEach {
                val viewId = View.generateViewId()
                Chip(context).apply {
                    id = viewId
                    isFocusable = false
                    isClickable = false
                    text = it.bowlerName
                    setChipBackgroundColorResource(R.color.primary)
                    setTextColor(ContextCompat.getColor(context, R.color.primaryWhiteText))
                    chipGroupMembers?.addView(this)
                }
            }

            itemView.setOnClickListener(this@TeamRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@TeamRecyclerViewAdapter)
        }
    }

    // MARK: ViewHolderDeleted

    inner class ViewHolderDeleted(view: View) : BaseRecyclerViewAdapter<Team>.ViewHolder(view) {
        private val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        private val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        override fun bind(item: Team) {
            val context = itemView.context

            tvDeleted?.text = String.format(
                    context.resources.getString(R.string.query_delete_item),
                    getItemAt(adapterPosition).name
            )

            val deletedItemListener = View.OnClickListener {
                if (it.id == R.id.tv_undo) {
                    delegate?.onItemSwipe(getItemAt(adapterPosition))
                } else {
                    delegate?.onItemDelete(getItemAt(adapterPosition))
                }
            }
            itemView.setOnClickListener(deletedItemListener)
            itemView.setOnLongClickListener(null)
            tvUndo?.setOnClickListener(deletedItemListener)
        }
    }
}
