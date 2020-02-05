package com.ict.assetmanagement.uidesign.button

/**
 * Different State of the button.
 */
enum class ButtonState {
    NORMAL, // Use to communicate the button is interactive.
    PRESSED, //Use for hover, focus, and pressed states.
    DISABLED // Use when the user cannot proceed until an additional input is provided.
}
