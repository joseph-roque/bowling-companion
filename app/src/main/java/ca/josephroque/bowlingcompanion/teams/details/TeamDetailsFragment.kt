package ca.josephroque.bowlingcompanion.teams.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMember
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMemberDialog
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMembersListFragment
import kotlinx.android.synthetic.main.view_team_member_header.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing the details of a single team and its members.
 */
class TeamDetailsFragment : BaseFragment(),
        IFloatingActionButtonHandler,
        ListFragment.OnListFragmentInteractionListener,
        TeamMemberDialog.OnTeamMemberDialogInteractionListener,
        TeamMembersListFragment.OnTeamMembersListFragmentInteractionListener {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamDetailsFragment"

        /** Identifier for the argument that represents the [Team] whose details are displayed. */
        private const val ARG_TEAM = "${TAG}_team"

        /**
         * Creates a new instance.
         *
         * @param team team to load details of
         * @return the new instance
         */
        fun newInstance(team: Team): TeamDetailsFragment {
            val fragment = TeamDetailsFragment()
            fragment.arguments = Bundle().apply { putParcelable(ARG_TEAM, team) }
            return fragment
        }
    }

    /** The team whose details are to be displayed. */
    private var team: Team? = null

    /** Indicate if all team members are ready for bowling to begin. */
    private var allTeamMembersReady: Boolean = false

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        team = savedInstanceState?.getParcelable(ARG_TEAM) ?: arguments?.getParcelable(ARG_TEAM)
        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.fragment_team_details, container, false)
        setupHeader(view)

        val team = team
        if (savedInstanceState == null && team != null) {
            val fragment = TeamMembersListFragment.newInstance(team)
            childFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, fragment)
                commit()
            }
        }

        return view
    }

    /**
     * Set up the header of the view.
     *
     * @param rootView the root view
     */
    private fun setupHeader(rootView: View) {
        rootView.tv_header_title.setText(R.string.team_members)
        rootView.tv_header_caption.setText(R.string.team_members_select_league)
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return if (allTeamMembersReady) {
            R.drawable.ic_ball
        } else {
            null
        }
    }

    /** @Override */
    override fun onFabClick() {
        if (allTeamMembersReady) {
            TODO("not implemented")
        }
    }

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is TeamMember) {
            val fragment = TeamMemberDialog.newInstance(item)
            fragmentNavigation?.pushDialogFragment(fragment)
        }
    }

    /** @Override */
    override fun onFinishTeamMember(teamMember: TeamMember) {
        childFragmentManager.fragments
                .filter { it != null && it.isVisible }
                .forEach {
                    val list = it as? TeamMembersListFragment ?: return
                    list.refreshList(teamMember)
                }
    }

    /** @Override */
    override fun onTeamMembersReadyChanged(ready: Boolean) {
        allTeamMembersReady = ready
        fabProvider?.invalidateFab()
    }
}
