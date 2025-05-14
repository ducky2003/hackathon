package com.vinhuni.senselib.service

import com.vinhuni.senselib.model.Publisher
import com.vinhuni.senselib.repository.PublisherRepository
import org.springframework.stereotype.Service

@Service
class PublisherService(private val publisherRepository: PublisherRepository) {

    fun getAllPublishers(): List<Publisher> {
        return publisherRepository.findAll()
    }

    fun getPublisherById(id: Int): Publisher {
        return publisherRepository.findById(id).orElseThrow {
            IllegalArgumentException("Không tìm thấy nhà xuất bản với ID: $id")
        }
    }

    fun savePublisher(publisher: Publisher): Publisher {
        return publisherRepository.save(publisher)
    }

    fun deletePublisher(id: Int) {
        publisherRepository.deleteById(id)
    }
}