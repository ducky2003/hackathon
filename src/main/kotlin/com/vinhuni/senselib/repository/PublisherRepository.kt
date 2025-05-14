package com.vinhuni.senselib.repository

import com.vinhuni.senselib.model.Publisher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PublisherRepository : JpaRepository<Publisher, Int>