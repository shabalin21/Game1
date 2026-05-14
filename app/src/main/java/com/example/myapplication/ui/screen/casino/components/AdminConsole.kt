package com.example.myapplication.ui.screen.casino.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.ui.theme.*

@Composable
fun AdminConsole(
    onCommand: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var commandText by remember { mutableStateOf("") }
    val consoleLogs = remember { mutableStateListOf<String>("INITIALIZING ROOT ACCESS...", "SCANNING NEURAL LINK...", "ADMIN_SESSION_ACTIVE") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .border(1.dp, Color.Red.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Red))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "NEURAL_OVERRIDE_CONSOLE",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(consoleLogs) { log ->
                        Text(
                            text = "> $log",
                            color = Color.Red.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(">", color = Color.Red, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = commandText,
                        onValueChange = { commandText = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Red, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                        cursorBrush = SolidColor(Color.Red),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (commandText.isNotBlank()) {
                                consoleLogs.add(commandText)
                                onCommand(commandText)
                                commandText = ""
                            }
                        })
                    )
                }
            }
        }
    }
}
