package com.test.tictactoe.utils

import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Field
import com.test.tictactoe.utils.game.isWithinBounds

fun Field.getDirection(fromX: Int, fromY: Int, deltaX: Int, deltaY: Int, length: Int = 5) : List<GameSymbol?> {
    var currentX = fromX
    var currentY = fromY
    var currentLength = 1

    val direction = mutableListOf<GameSymbol?>()

    while(isWithinBounds(this, currentX, currentY) && currentLength <= length) {
        direction.add(this.field[currentY][currentX])

        currentX += deltaX
        currentY += deltaY
        currentLength++
    }

    return direction.toList()
}

fun Field.getAllDirections(fromX: Int, fromY: Int, length: Int = 5) : List<List<GameSymbol?>> {
    val directions = mutableListOf<List<GameSymbol?>>()

    directions.add(this.getDirection(fromX, fromY, 0, -1, length))  // Up
    directions.add(this.getDirection(fromX, fromY, 0, 1, length))   // Down
    directions.add(this.getDirection(fromX, fromY, 1, 0, length))   // Right
    directions.add(this.getDirection(fromX, fromY, -1, 0, length))  // Left
    directions.add(this.getDirection(fromX, fromY, 1, -1, length))  // Right-Up
    directions.add(this.getDirection(fromX, fromY, -1, 1, length))  // Left-Down
    directions.add(this.getDirection(fromX, fromY, -1, -1, length)) // Left-Up
    directions.add(this.getDirection(fromX, fromY, 1, 1, length))   // Right-Down

    return directions.toList()
}

fun Field.hasAdjacent(x: Int, y: Int, distance: Int = 2) : Boolean {

    for(direction in this.getAllDirections(x, y)) {
        for(i in direction.indices) {
            if(i > distance)
                break

            if(direction[i] != null)
                return true
        }
    }

    return false
}