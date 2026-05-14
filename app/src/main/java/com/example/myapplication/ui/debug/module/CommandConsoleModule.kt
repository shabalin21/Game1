package com.example.myapplication.ui.debug.module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.NeonPurple

@Composable
fun CommandConsoleModule(
    onCommand: (String) -> Unit
) {
    var commandText by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(history.reversed()) { line ->
                Text(
                    line,
                    color = if (line.startsWith(">")) Color.Cyan else Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = commandText,
            onValueChange = { commandText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter command...", fontSize = 12.sp) },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (commandText.isNotBlank()) {
                    history.add("> $commandText")
                    onCommand(commandText)
                    commandText = ""
                }
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                cursorColor = NeonPurple
            )
        )
        
        Text(
            "Examples: /set hunger 100, /tick 24, /spawn_fx",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
