package com.test.tictactoe.repository

import com.test.tictactoe.model.Field
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FieldRepository : JpaRepository<Field, Long>