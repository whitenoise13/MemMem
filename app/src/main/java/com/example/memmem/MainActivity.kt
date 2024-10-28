package com.example.memmem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemMemApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemMemApp(viewModel: NoteViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(viewModel, navController) }
        composable("notes") { NoteListScreen(viewModel, navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: NoteViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7C0)) // 배경색
            .padding(16.dp)
    ) {
        Text(
            text = "MeMeM",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Color(0xFF355E3B),
            modifier = Modifier.padding(16.dp)
        )

        var newTitle by remember { mutableStateOf("") }
        var newContent by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .background(Color(0xFFDFFFC6), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            TextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("제목을 입력하세요") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFDFFFC6))
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newContent,
                onValueChange = { newContent = it },
                label = { Text("내용을 입력하세요") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFDFFFC6))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 등록하기 버튼을 메모 목록 보기 버튼의 위치로 이동
        Button(
            onClick = {
                if (newTitle.isNotEmpty() && newContent.isNotEmpty()) {
                    viewModel.addNote(newTitle, newContent) // 문자열로 추가
                    newTitle = ""
                    newContent = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF095B10)), // 초록색 배경
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // 중앙 정렬
                .padding(bottom = 8.dp) // 아래 여백 추가
        ) {
            Text("등록하기", color = Color.White) // 텍스트 색상을 흰색으로 설정
        }

        // 메모 목록 보기 버튼을 아래로 이동
        Button(
            onClick = { navController.navigate("notes") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF095B10)), // 초록색 배경
            modifier = Modifier.align(Alignment.CenterHorizontally) // 중앙 정렬
        ) {
            Text("메모 목록 보기", color = Color.White) // 텍스트 색상을 흰색으로 설정
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(viewModel: NoteViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7C0)) // 홈 화면과 같은 배경색 설정
            .padding(16.dp)
    ) {
        Text(
            text = "메모 목록",
            fontSize = 24.sp,
            color = Color(0xFF355E3B),
            modifier = Modifier.padding(16.dp)
        )
        NoteList(
            notes = viewModel.notes,
            onEdit = { id, title, content -> viewModel.editNote(id, Note(id, title, content)) },
            onDelete = { id -> viewModel.deleteNote(id) }
        )
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.align(Alignment.End).padding(16.dp)
        ) {
            Text("돌아가기")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card( // 카드 스타일로 각 메모 항목을 감싸기
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp), // 위아래 여백 추가
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDFFFC6)) // 카드 배경색 설정
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${note.title}\n${note.content}",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Note", tint = Color(0xFF355E3B))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Note", tint = Color(0xFF355E3B))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteList(
    notes: List<Note>,
    onEdit: (Long, String, String) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn {
        itemsIndexed(notes) { index, note ->
            var isEditing by remember { mutableStateOf(false) }
            var editedTitle by remember { mutableStateOf(note.title) }
            var editedContent by remember { mutableStateOf(note.content) }

            if (isEditing) {
                // 제목과 내용을 수정할 수 있도록 TextField 사용
                TextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("제목 수정") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                TextField(
                    value = editedContent,
                    onValueChange = { editedContent = it },
                    label = { Text("내용 수정") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        onEdit(note.id, editedTitle, editedContent) // 수정된 제목과 내용 전달
                        isEditing = false
                    }) {
                        Text("저장")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { isEditing = false }) {
                        Text("취소")
                    }
                }
            } else {
                NoteItem(
                    note = note,
                    onEdit = { isEditing = true },
                    onDelete = { onDelete(note.id) }
                )
            }
        }
    }
}