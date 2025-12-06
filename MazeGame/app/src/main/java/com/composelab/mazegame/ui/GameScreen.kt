package com.composelab.mazegame.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composelab.mazegame.ui.theme.MazeGameTheme

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory())
) {
    val maze = gameViewModel.maze
    val playerPath = gameViewModel.playerPath
    val solutionPath = gameViewModel.solutionPath

    Column(modifier = Modifier.fillMaxSize()) {
        MazeCanvas(
            modifier = Modifier.weight(1f),
            maze = maze,
            playerPath = playerPath,
            solutionPath = solutionPath,
            onDragStart = { offset -> gameViewModel.onDragStart(offset) },
            onDrag = gameViewModel::onDrag
        )

        Button(onClick = { gameViewModel.resetPlayerPath() }) {
            Text("Reset Path")
        }
        Button(onClick = {
            gameViewModel.generateNewMaze()
        }) {
            Text("New Maze")
        }
        Button(onClick = { gameViewModel.showSolution() }) {
            Text("Show Solution")
        }
    }
}

@Composable
private fun MazeCanvas(
    maze: Maze,
    playerPath: Path,
    solutionPath: List<Pair<Int, Int>>,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset -> onDragStart(offset) },
                onDrag = { change, _ -> onDrag(change.position) }
            )
        }
    ) {
        val cellWidth = size.width / maze.cells[0].size
        val cellHeight = size.height / maze.cells.size

        for (y in maze.cells.indices) {
            for (x in maze.cells[y].indices) {
                val cell = maze.cells[y][x]
                if (cell.topWall) {
                    drawLine(
                        color = Color.Black,
                        start = Offset(x * cellWidth, y * cellHeight),
                        end = Offset((x + 1) * cellWidth, y * cellHeight),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }
                if (cell.bottomWall) {
                    drawLine(
                        color = Color.Black,
                        start = Offset(x * cellWidth, (y + 1) * cellHeight),
                        end = Offset((x + 1) * cellWidth, (y + 1) * cellHeight),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }
                if (cell.leftWall) {
                    drawLine(
                        color = Color.Black,
                        start = Offset(x * cellWidth, y * cellHeight),
                        end = Offset(x * cellWidth, (y + 1) * cellHeight),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }
                if (cell.rightWall) {
                    drawLine(
                        color = Color.Black,
                        start = Offset((x + 1) * cellWidth, y * cellHeight),
                        end = Offset((x + 1) * cellWidth, (y + 1) * cellHeight),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Draw solution path
        if (solutionPath.isNotEmpty()) {
            val solutionGraphicsPath = Path().apply {
                val first = solutionPath.first()
                moveTo(
                    first.first * cellWidth + cellWidth / 2,
                    first.second * cellHeight + cellHeight / 2
                )
                for (i in 1 until solutionPath.size) {
                    val point = solutionPath[i]
                    lineTo(
                        point.first * cellWidth + cellWidth / 2,
                        point.second * cellHeight + cellHeight / 2
                    )
                }
            }
            drawPath(
                path = solutionGraphicsPath,
                color = Color.Magenta,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )
        }

        drawPath(
            path = playerPath,
            color = Color.Blue,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // Draw start point
        drawCircle(
            color = Color.Green,
            radius = cellWidth / 3,
            center = Offset(
                maze.startX * cellWidth + cellWidth / 2,
                maze.startY * cellHeight + cellHeight / 2
            )
        )

        // Draw end point
        drawCircle(
            color = Color.Red,
            radius = cellWidth / 3,
            center = Offset(
                maze.endX * cellWidth + cellWidth / 2,
                maze.endY * cellHeight + cellHeight / 2
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MazeGameTheme {
        GameScreen()
    }
}