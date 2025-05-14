package com.vinhuni.senselib.repository

import com.vinhuni.senselib.model.Download
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DowloadRepository : JpaRepository<Download, Int> {
    fun findAllByUserIdOrderByDownloadDateDesc(userId: Int): List<Download>
    fun findByUserIdAndIsDeletedFalseOrderByDownloadDateDesc(userId: Int): List<Download>
}