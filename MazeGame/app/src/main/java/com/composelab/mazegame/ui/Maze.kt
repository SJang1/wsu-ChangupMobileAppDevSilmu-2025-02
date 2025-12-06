package com.composelab.mazegame.ui

data class Cell(
    var x: Int,
    var y: Int,
    var topWall: Boolean = true,
    var bottomWall: Boolean = true,
    var leftWall: Boolean = true,
    var rightWall: Boolean = true,
    var visited: Boolean = false
)

data class Maze(val width: Int, val height: Int) {
    val cells = Array(height) { y ->
        Array(width) { x ->
            Cell(x, y)
        }
    }
    var startX: Int = 0
    var startY: Int = 0
    var endX: Int = width - 1
    var endY: Int = height - 1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Maze

        if (width != other.width) return false
        if (height != other.height) return false
        if (!cells.contentDeepEquals(other.cells)) return false
        if (startX != other.startX) return false
        if (startY != other.startY) return false
        if (endX != other.endX) return false
        if (endY != other.endY) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + cells.contentDeepHashCode()
        result = 31 * result + startX
        result = 31 * result + startY
        result = 31 * result + endX
        result = 31 * result + endY
        return result
    }

    fun generate() {
        // Reset visited status for all cells
        for (y in cells.indices) {
            for (x in cells[y].indices) {
                cells[y][x].visited = false
            }
        }

        val stack = mutableListOf<Cell>()
        var current = cells[startY][startX]
        current.visited = true
        stack.add(current)

        while (stack.isNotEmpty()) {
            current = stack.removeAt(stack.size - 1)
            val neighbors = getUnvisitedNeighbors(current)

            if (neighbors.isNotEmpty()) {
                stack.add(current)
                val neighbor = neighbors.random()
                removeWall(current, neighbor)
                neighbor.visited = true
                stack.add(neighbor)
            }
        }
    }

    private fun getUnvisitedNeighbors(cell: Cell): List<Cell> {
        val neighbors = mutableListOf<Cell>()

        if (cell.x > 0 && !cells[cell.y][cell.x - 1].visited) {
            neighbors.add(cells[cell.y][cell.x - 1])
        }
        if (cell.x < width - 1 && !cells[cell.y][cell.x + 1].visited) {
            neighbors.add(cells[cell.y][cell.x + 1])
        }
        if (cell.y > 0 && !cells[cell.y - 1][cell.x].visited) {
            neighbors.add(cells[cell.y - 1][cell.x])
        }
        if (cell.y < height - 1 && !cells[cell.y + 1][cell.x].visited) {
            neighbors.add(cells[cell.y + 1][cell.x])
        }

        return neighbors
    }

    private fun removeWall(current: Cell, neighbor: Cell) {
        if (current.x == neighbor.x && current.y == neighbor.y + 1) {
            current.topWall = false
            neighbor.bottomWall = false
        } else if (current.x == neighbor.x && current.y == neighbor.y - 1) {
            current.bottomWall = false
            neighbor.topWall = false
        } else if (current.x == neighbor.x + 1 && current.y == neighbor.y) {
            current.leftWall = false
            neighbor.rightWall = false
        } else if (current.x == neighbor.x - 1 && current.y == neighbor.y) {
            current.rightWall = false
            neighbor.leftWall = false
        }
    }

    /**
     * Solves the maze using BFS and returns the path from start to end.
     * Returns a list of (x, y) pairs representing the solution path.
     */
    fun solve(): List<Pair<Int, Int>> {
        val visited = Array(height) { BooleanArray(width) { false } }
        val parent = Array(height) { arrayOfNulls<Pair<Int, Int>>(width) }
        val queue = ArrayDeque<Pair<Int, Int>>()

        queue.add(Pair(startX, startY))
        visited[startY][startX] = true

        while (queue.isNotEmpty()) {
            val (cx, cy) = queue.removeFirst()

            if (cx == endX && cy == endY) {
                // Reconstruct path
                val path = mutableListOf<Pair<Int, Int>>()
                var current: Pair<Int, Int>? = Pair(cx, cy)
                while (current != null) {
                    path.add(current)
                    current = parent[current.second][current.first]
                }
                return path.reversed()
            }

            val cell = cells[cy][cx]

            // Check all four directions
            // Up
            if (!cell.topWall && cy > 0 && !visited[cy - 1][cx]) {
                visited[cy - 1][cx] = true
                parent[cy - 1][cx] = Pair(cx, cy)
                queue.add(Pair(cx, cy - 1))
            }
            // Down
            if (!cell.bottomWall && cy < height - 1 && !visited[cy + 1][cx]) {
                visited[cy + 1][cx] = true
                parent[cy + 1][cx] = Pair(cx, cy)
                queue.add(Pair(cx, cy + 1))
            }
            // Left
            if (!cell.leftWall && cx > 0 && !visited[cy][cx - 1]) {
                visited[cy][cx - 1] = true
                parent[cy][cx - 1] = Pair(cx, cy)
                queue.add(Pair(cx - 1, cy))
            }
            // Right
            if (!cell.rightWall && cx < width - 1 && !visited[cy][cx + 1]) {
                visited[cy][cx + 1] = true
                parent[cy][cx + 1] = Pair(cx, cy)
                queue.add(Pair(cx + 1, cy))
            }
        }

        return emptyList() // No solution found
    }
}
