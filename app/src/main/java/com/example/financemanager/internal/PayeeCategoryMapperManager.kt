package com.example.financemanager.internal

import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.database.localstorage.dao.PayeeCategoryMapperDao

class PayeeCategoryMapperManager(
    private val payeeCategoryMapperDao: PayeeCategoryMapperDao
) {
    suspend fun addMapping(payee: String, categoryId: Int) {
        payeeCategoryMapperDao.addMapping(PayeeCategoryMapper(payee = payee,
            categoryId = categoryId))
    }

    suspend fun getMapping(payee: String): Int? {
        return payeeCategoryMapperDao.getCategoryId(payee)
    }
}
