package com.vinhuni.senselib.dto

import java.time.Instant

data class BookDTO(
    val id: Int?,
    val title: String?,
    val authors: List<String> = emptyList(),
    val publisherName: String?,
    val coverImage: String?,
    val createdAt: Instant?,
    val viewCount: Int,
    val downloadCount: Int,
    val rating: Double
)
