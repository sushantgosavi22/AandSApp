package com.aandssoftware.aandsinventory.firebase

import com.google.firebase.database.DataSnapshot

interface GetDataSnapshotListner {
    fun onDataChange(dataSnapshot: DataSnapshot)
}
