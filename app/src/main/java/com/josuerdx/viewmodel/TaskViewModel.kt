package com.josuerdx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josuerdx.lab08.data.dao.TaskDao
import com.josuerdx.lab08.data.firestore.FirestoreRepository // Asegúrate de importar tu FirestoreRepository
import com.josuerdx.lab08.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao, private val firestoreRepo: FirestoreRepository) : ViewModel() {

    // Estado para la lista de tareas
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        // Al inicializar, cargamos las tareas de la base de datos
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }

        // Escuchar cambios en Firestore
        firestoreRepo.listenForTasks { tasksFromFirestore ->
            setTasks(tasksFromFirestore) // Actualizar el estado con tareas de Firestore
        }
    }

    // Función para añadir una nueva tarea
    fun addTask(description: String) {
        val newTask = Task(description = description)
        viewModelScope.launch {
            dao.insertTask(newTask)
            firestoreRepo.addTask(newTask) // Agregar tarea a Firestore
            _tasks.value = dao.getAllTasks() // Recargamos la lista
        }
    }

    // Función para alternar el estado de completado de una tarea
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            dao.updateTask(updatedTask)
            _tasks.value = dao.getAllTasks() // Recargamos la lista
        }
    }

    // Función para eliminar todas las tareas
    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAllTasks()
            _tasks.value = emptyList() // Vaciamos la lista en el estado
        }
    }

    // Función para actualizar el estado de las tareas
    private fun setTasks(newTasks: List<Task>) {
        _tasks.value = newTasks
        viewModelScope.launch {
            // Actualiza las tareas en la base de datos local si es necesario
            dao.deleteAllTasks() // Limpia las tareas anteriores si es necesario
            dao.insertAll(newTasks) // Inserta las nuevas tareas
        }
    }
}
