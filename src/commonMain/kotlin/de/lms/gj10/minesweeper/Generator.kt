package de.lms.gj10.minesweeper

fun generateSolvableMinesweeperGrid(width: Int, height: Int, bombCount: Int, startingAreaSize: Double = 3.5): Grid {
    repeat(2000) {
        val grid = Grid.generate(width, height, bombCount, startingAreaSize)
        val copy = grid.copy()
        val solved = solve(copy)
        if (solved) return grid
    }
    error("could not generate puzzle")
}
