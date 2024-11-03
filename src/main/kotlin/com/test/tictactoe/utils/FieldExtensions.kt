package com.test.tictactoe.utils

import com.test.tictactoe.model.Field
import com.test.tictactoe.utils.ai.GameBot
import com.test.tictactoe.utils.game.Cell
import com.test.tictactoe.utils.game.Direction
import com.test.tictactoe.utils.game.Move
import com.test.tictactoe.utils.game.isWithinBounds

fun Field.getDirection(
    startX: Int,
    startY: Int,
    fromDeltaX: Int,
    fromDeltaY: Int,
    toDeltaX: Int,
    toDeltaY: Int,
    oneSideLength: Int = 5
) : Direction {
    var currentX = startX + fromDeltaX * (oneSideLength - 1)
    var currentY = startY + fromDeltaY * (oneSideLength - 1)
    var centerIndex = 0
    var index = -1

    val sequence = mutableListOf<Cell>()
    while(((currentX != startX + toDeltaX * oneSideLength) || toDeltaX == 0)
        && ((currentY != startY + toDeltaY * oneSideLength) || toDeltaY == 0)
    ) {
        if(isWithinBounds(this, currentX, currentY)) {
            sequence.add(Cell(currentX, currentY, this.field[currentY][currentX]))

            index++
            if(currentX == startX && currentY == startY) {
                centerIndex = index
            }
        } else if (isWithinBounds(this, currentX - toDeltaX, currentY - toDeltaY)) {
            return Direction(
                sequence.toList(),
                centerIndex,
            )
        }

        currentX += toDeltaX
        currentY += toDeltaY
    }

    return Direction(
        sequence.toList(),
        centerIndex,
    )
}

fun Field.getAllDirections(fromX: Int, fromY: Int, length: Int = 5) : List<Direction> {

    val startTime = System.currentTimeMillis() // TODO

    val directions = mutableListOf<Direction>()

    directions.add(this.getDirection(fromX,
        fromY,
        0,
        -1,
        0,
        1,
        length)
    )  // Up - Down
    directions.add(this.getDirection(fromX,
        fromY,
        1,
        0,
        -1,
        0,
        length)
    )   // Right - Left
    directions.add(this.getDirection(fromX,
        fromY,
        1,
        -1,
        -1,
        1,
        length)
    )  // Right-Up - Left-Down
    directions.add(this.getDirection(fromX,
        fromY,
        -1,
        -1,
        1,
        1,
        length)
    ) // Left-Up - Right-Down


    GameBot.timeGetDirections += System.currentTimeMillis() - startTime // TODO


    return directions.toList()
}

fun Field.hasAdjacent(x: Int, y: Int, distance: Int = 2) : Boolean {
    for(direction in this.getAllDirections(x, y, distance + 1)) {
        for(i in 1.. distance) {
            if(direction.sequence.getOrNull(direction.centerIndex + i)?.symbol != null
                || direction.sequence.getOrNull(direction.centerIndex - i)?.symbol != null) {
                return true
            }
        }
    }

    return false
}

fun Field.getMoves() : List<Move> {
    val moves = mutableListOf<Move>()

    for (y in this.field.indices) {
        for (x in this.field[y].indices) {
            if (this.field[y][x] != null) {
                moves.add(
                    Move(x, y)
                )
            }
        }
    }

    return moves.toList()
}

fun Field.getMovesToEmptyCells() : List<Move> {
    val moves = mutableListOf<Move>()
    for(y in this.field.indices) {
        for(x in this.field[y].indices) {
            if(this.field[y][x] != null)
                continue

            moves.add(
                Move(x, y)
            )
        }
    }

    return moves.toList()
}

fun Field.countOccupiedCells() : Int {
    var count = 0
    for (row in this.field) {
        for (symbol in row) {
            if (symbol != null) {
                count++
            }
        }
    }
    return count
}

fun Field.getPossibleMovesAdjacentToOccupiedCells(distance: Int) : List<Move> {
    if(this.countOccupiedCells() < (this.width * this.height)) {
        val movesHashSet = hashSetOf<Move>()

        for(y in this.field.indices) {
            for(x in this.field[y].indices) {
                if(this.field[y][x] == null)
                    continue

                for(direction in this.getAllDirections(x, y, distance + 1)) {
                    for(i in 1.. distance) {
                        val el1 = direction.sequence.getOrNull(direction.centerIndex + i)
                        val el2 = direction.sequence.getOrNull(direction.centerIndex - i)

                        if(el1?.symbol == null && el1 != null) {
                            movesHashSet.add(
                                Move(el1.x, el1.y)
                            )
                        }
                        if(el2?.symbol == null && el2 != null) {
                            movesHashSet.add(
                                Move(el2.x, el2.y)
                            )
                        }
                    }
                }
            }
        }

        return movesHashSet.toList()
    } else {
        return listOf()
    }
}