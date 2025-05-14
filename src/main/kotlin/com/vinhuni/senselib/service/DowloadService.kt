package com.vinhuni.senselib.service

import com.vinhuni.senselib.model.Download
import com.vinhuni.senselib.repository.DowloadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DowloadService (private val downloadRepository: DowloadRepository) {
    @Transactional
    fun saveDownload(download: Download): Download {
        return downloadRepository.save(download)
    }

//    fun getDownloadsByUser(userId: Int): List<Download> {
//        return downloadRepository.findAllByUserIdOrderByDownloadDateDesc(userId)
//    }
    fun getDownloadsByUser(userId: Int): List<Download> {
        return downloadRepository.findByUserIdAndIsDeletedFalseOrderByDownloadDateDesc(userId)
    }
    fun getDownloadById(downloadId: Int): Download? {
        return downloadRepository.findById(downloadId).orElse(null)
    }
    fun softDeleteDownload(downloadId: Int) {
        val download = getDownloadById(downloadId)
        download?.let {
            it.isDeleted = true
            downloadRepository.save(it)
        }
    }
    fun deleteDownload(downloadId: Int) {
        downloadRepository.deleteById(downloadId)
    }
}