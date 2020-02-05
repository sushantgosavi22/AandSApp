package com.aandssoftware.aandsinventory.firebase

import com.google.firebase.database.DataSnapshot

interface GetAlphaNumericAndNumericIdListener {
    fun afterGettingIds(alphaNumericId: String, numericId: String)
}
