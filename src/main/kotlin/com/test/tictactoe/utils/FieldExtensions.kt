package com.test.tictactoe.utils

import com.test.tictactoe.enum.Directions
import com.test.tictactoe.model.Field
import com.test.tictactoe.utils.game.Cell
import com.test.tictactoe.utils.game.Direction
import com.test.tictactoe.utils.game.Move
import com.test.tictactoe.utils.game.isWithinBounds

fun Field.getDirection(
    startX: Int,
    startY: Int,
    direction: Directions,
    oneSideLength: Int = 5
) : Direction {
    var currentX = startX + direction.fromDeltaX * (oneSideLength - 1)
    var currentY = startY + direction.fromDeltaY * (oneSideLength - 1)
    var centerIndex = 0
    var index = -1

    val sequence = mutableListOf<Cell>()
    while(((currentX != startX + direction.toDeltaX * oneSideLength) || direction.toDeltaX == 0)
        && ((currentY != startY + direction.toDeltaY * oneSideLength) || direction.toDeltaY == 0)
    ) {
        if(isWithinBounds(this, currentX, currentY)) {
            sequence.add(Cell(currentX, currentY, this.field[currentY][currentX]))

            index++
            if(currentX == startX && currentY == startY) {
                centerIndex = index
            }
        }

        currentX += direction.toDeltaX
        currentY += direction.toDeltaY
    }

    return Direction(
        sequence.toList(),
        centerIndex,
    )
}

fun Field.getAllDirections(fromX: Int, fromY: Int, length: Int = 5) : List<Direction> {
    val directions = mutableListOf<Direction>()

    directions.add(this.getDirection(fromX,
        fromY,
        Directions.VERTICAL,
        length)
    )
    directions.add(this.getDirection(fromX,
        fromY,
        Directions.HORIZONTAL,
        length)
    )
    directions.add(this.getDirection(fromX,
        fromY,
        Directions.MAIN_DIAGONAL,
        length)
    )
    directions.add(this.getDirection(fromX,
        fromY,
        Directions.SECONDARY_DIAGONAL,
        length)
    )

    return directions.toList()
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

fun Field.updatedDirection(direction: Direction) : Direction {
    direction.sequence = direction.sequence.map { Cell(it.x, it.y, this.field[it.y][it.x]) }

    return direction
}