package com.example.financemanager.ui.composable.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanager.database.PrepopulationData
import com.example.financemanager.ui.composable.category.parseColor
import com.example.financemanager.ui.composable.utils.MyText.toIndianFormat
import com.example.financemanager.viewmodel.data.CategorySpending
import kotlin.math.cos
import kotlin.math.sin

object Chart {

    @Composable
    fun DrawDonutChart(x: List<CategorySpending>, modifier: Modifier = Modifier, label: String = "Label") {
        if (x.isEmpty()) return
        val totalAmount = x.sumOf { it.totalSpending }
        if (totalAmount == 0.0) return

        val textMeasurer = rememberTextMeasurer()
        val labelColor = MaterialTheme.colorScheme.onSurface

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Canvas(
                modifier = modifier
                    .fillMaxSize()
            ) {
                val textLayout = textMeasurer
                    .measure(
                        label,
                        TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = labelColor
                        )
                    )
                drawText(
                    textLayout,
                    topLeft = Offset(center.x - textLayout.size.width / 2, center.y - textLayout.size.height/ 2)
                )
                val donutThickness = size.minDimension / 4f
                val diameter = size.minDimension - donutThickness
                var startAngle = -90f

                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )
                val arcSize = Size(diameter, diameter)

                for (item in x) {
                    val sweepAngle = ((item.totalSpending / totalAmount) * 360f).toFloat()

                    // Calculate position for the text (middle of the arc segment)
                    val midAngleRad =
                        Math.toRadians((startAngle + sweepAngle / 2f).toDouble()).toFloat()
                    val labelRadius = diameter / 2f
                    val textX = center.x + labelRadius * cos(midAngleRad)
                    val textY = center.y + labelRadius * sin(midAngleRad)

                    drawArc(
                        color = parseColor(item.category.color),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = donutThickness),
                        topLeft = topLeft,
                        size = arcSize
                    )

                    // Only draw text labels for segments large enough to contain them
                    if (sweepAngle > 15f) {
                        val textToDraw = "${item.category.name}\n${
                            item.totalSpending.toIndianFormat().split(".")[0]
                        }" // Simplified for space
                        val textLayoutResult = textMeasurer.measure(
                            text = textToDraw,
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                textX - textLayoutResult.size.width / 2,
                                textY - textLayoutResult.size.height / 2
                            )
                        )
                    }

                    startAngle += sweepAngle
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrawDonutChartPreview() {
    val dummySpending = listOf(
        CategorySpending(PrepopulationData.categories[0], 1200.0, 2000.0),
        CategorySpending(PrepopulationData.categories[1], 800.0, 1500.0),
        CategorySpending(PrepopulationData.categories[2], 500.0, null),
        CategorySpending(PrepopulationData.categories[3], 300.0, 500.0)
    )
    Box(
        modifier = Modifier
            .size(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Chart.DrawDonutChart(dummySpending)
    }
}
