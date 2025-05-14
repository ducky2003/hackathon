package com.vinhuni.senselib.service

import com.vinhuni.senselib.model.Author
import com.vinhuni.senselib.repository.AuthorRepository
import org.springframework.stereotype.Service

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    fun getAllAuthors(): List<Author> {
        return authorRepository.findAll()
    }

    fun getAuthorById(id: Int): Author {
        return authorRepository.findById(id).orElseThrow {
            IllegalArgumentException("Không tìm thấy tác giả với ID: $id")
        }
    }

    fun saveAuthor(author: Author): Author {
        return authorRepository.save(author)
    }

    fun deleteAuthor(id: Int) {
        authorRepository.deleteById(id)
    }
}