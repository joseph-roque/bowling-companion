package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isRightChopOff

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are right chop offs.
 */
class RightChopOffsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedBy(deck: Deck) = deck.isRightChopOff

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::RightChopOffsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_right_chops
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
