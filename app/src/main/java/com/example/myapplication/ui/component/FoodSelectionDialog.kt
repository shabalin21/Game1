package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.ui.screen.home.FoodInventoryItem
import com.example.myapplication.ui.theme.*

@Composable
fun FoodSelectionDialog(
    foodItems: List<FoodInventoryItem>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SELECT FOOD",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                if (foodItems.isEmpty()) {
                    Text(
                        text = "No food in storage. Buy some at the market!",
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(foodItems) { foodItem ->
                            FoodItemCard(
                                foodItem = foodItem,
                                onClick = {
                                    onSelect(foodItem.item.id)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onDismiss) {
                    Text("CANCEL", color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodInventoryItem,
    onClick: () -> Unit
) {
    val item = foodItem.item
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = item.icon, fontSize = 36.sp)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(NeonPurple, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "x${foodItem.quantity}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (item.effect.hungerChange > 0) {
                    Text(
                        text = "+${item.effect.hungerChange.toInt()}🥩",
                        fontSize = 9.sp,
                        color = NeonOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                if (item.effect.happinessChange > 0) {
                    Text(
                        text = "+${item.effect.happinessChange.toInt()}💖",
                        fontSize = 9.sp,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
