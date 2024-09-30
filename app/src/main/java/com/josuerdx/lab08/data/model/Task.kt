package com.josuerdx.lab08.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "description") val description: String,
    @get:PropertyName("isCompleted") @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    var firestoreId: String? = null
) {
    // Constructor sin argumentos
    constructor() : this(0, "", false, null)
}
