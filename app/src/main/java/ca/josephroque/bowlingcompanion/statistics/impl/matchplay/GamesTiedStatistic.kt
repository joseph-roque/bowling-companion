package ca.josephroque.bowlingcompanion.statistics.impl.matchplay

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.statistics.PercentageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatGame

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of match play games tied.
 */
class GamesTiedStatistic(override var numerator: Int = 0, override var denominator: Int = 0) : PercentageStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(game: StatGame) {
        denominator++
        if (game.matchPlay == MatchPlayResult.TIED) {
            numerator++
        }
    }

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.MatchPlay
    override val secondaryGraphDataLabelId = R.string.statistic_total_match_play_games_recorded
    override fun isModifiedBy(game: StatGame) = game.matchPlay != MatchPlayResult.NONE

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::GamesTiedStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_games_tied
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
