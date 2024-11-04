package com.test.tictactoe.utils

import com.test.tictactoe.model.Field
import com.test.tictactoe.utils.game.Cell
import com.test.tictactoe.utils.game.isWithinBounds

fun Field.getDirection(
    startX: Int,
    startY: Int,
    fromDeltaX: Int,
    fromDeltaY: Int,
    toDeltaX: Int,
    toDeltaY: Int,
    oneSideLength: Int = 5
) : List<Cell> {
    var currentX = startX + fromDeltaX * oneSideLength
    var currentY = startY + fromDeltaY * oneSideLength

    val direction = mutableListOf<Cell>()
    while(((currentX != startX + toDeltaX * oneSideLength) || toDeltaX == 0)
        && ((currentY != startY + toDeltaY * oneSideLength) || toDeltaY == 0)
    ) {
        if(isWithinBounds(this, currentX, currentY))
            direction.add(Cell(currentX, currentY, this.field[currentY][currentX]))

        currentX += toDeltaX
        currentY += toDeltaY
    }

    return direction.toList()
}

fun Field.getAllDirections(fromX: Int, fromY: Int, length: Int = 4) : List<List<Cell>> {
    val directions = mutableListOf<List<Cell>>()

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

    return directions.toList()
}

fun Field.hasAdjacent(x: Int, y: Int, distance: Int = 2) : Boolean {
    for(direction in this.getAllDirections(x, y, distance)) {
        for(i in direction.indices) {
            if(i > distance)
                break

            if(direction[i].symbol != null)
                return true
        }
    }

    return false
}

fun Field.hasMoves() : Boolean {
    return field.any { row -> row.any { symbol -> symbol != null } }
}