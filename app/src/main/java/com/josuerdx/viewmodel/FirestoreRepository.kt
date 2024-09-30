package com.josuerdx.lab08.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.josuerdx.lab08.data.model.Task

class FirestoreRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun listenForTasks(onTasksReceived: (List<Task>) -> Unit) {
        firestore.collection("tasks").addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Manejo de errores
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val tasks = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Task::class.java)?.copy(firestoreId = doc.id)
                }
                onTasksReceived(tasks)
            }
        }
    }

    fun addTask(task: Task) {
        firestore.collection("tasks").add(task.copy(firestoreId = null)) // Agregar tarea sin ID de Firestore
    }
}
