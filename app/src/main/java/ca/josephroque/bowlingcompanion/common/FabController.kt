package ca.josephroque.bowlingcompanion.common

import android.graphics.Color
import android.support.design.widget.FloatingActionButton
import android.view.View
import ca.josephroque.bowlingcompanion.utils.isVisible

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Control the floating action button with common actions.
 */
class FabController(private val floatingActionButton: FloatingActionButton, listener: View.OnClickListener) {
    init {
        floatingActionButton.setOnClickListener(listener)
    }

    var image: Int? = null
        set(value) {
            field = value
            if (floatingActionButton.isVisible) {
                floatingActionButton.hide(fabVisibilityChangeListener)
            } else {
                fabVisibilityChangeListener.onHidden(floatingActionButton)
            }
        }

    private val fabVisibilityChangeListener = object : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            fab?.let {
                it.setColorFilter(Color.BLACK)
                val image = image ?: return
                it.setImageResource(image)
                it.show()
            }
        }
    }
}
