package com.josuerdx.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.josuerdx.lab08.components.MyTabBarScreen
import com.josuerdx.lab08.components.MyToolbar
import com.josuerdx.lab08.data.database.TaskDatabase
import com.josuerdx.lab08.ui.theme.Lab08Theme
import com.josuerdx.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build()

                val taskDao = db.taskDao()
                val viewModel = TaskViewModel(taskDao)

                // Toolbar - TabBar
                Scaffold(
                    topBar = { MyToolbar() },
                    content = { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            MyTabBarScreen {
                                TaskScreen(viewModel)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }

    // Estructura de pantalla
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Campo de texto
            TextField(
                value = newTaskDescription,
                onValueChange = { newTaskDescription = it },
                label = { Text("Nueva tarea") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Comprobar si hay tareas
            if (tasks.isEmpty()) {
                EmptyStateView()
            } else {
                // Lista de tareas
                tasks.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = task.description)
                        Button(onClick = { viewModel.toggleTaskCompletion(task) }) {
                            Text(if (task.isCompleted) "Completada" else "Pendiente")
                        }
                    }
                }
            }
        }

        // FloatingActionButton para eliminar todas las tareas
        FloatingActionButton(
            onClick = {
                coroutineScope.launch { viewModel.deleteAllTasks() }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Alinear en la parte inferior derecha

                .padding(bottom = 90.dp, end = 16.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Eliminar todas las tareas")
        }

        // FloatingActionButton para agregar una nueva tarea
        FloatingActionButton(
            onClick = {
                if (newTaskDescription.isNotEmpty()) {
                    viewModel.addTask(newTaskDescription)
                    newTaskDescription = ""
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar tarea")
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen si no hay tareas
        Image(
            painter = painterResource(id = R.drawable.tasksss),
            contentDescription = "No hay tareas",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Texto si no hay tareas
        Text(text = "No hay tareas hoy", style = MaterialTheme.typography.headlineSmall)

        Text(
            text = "Sal a caminar",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 15.dp)
        )
    }
}