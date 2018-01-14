package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.entity.DocumentEntity
import org.springframework.data.repository.CrudRepository

interface DocumentRepository : CrudRepository<DocumentEntity, String>, DocumentSearch