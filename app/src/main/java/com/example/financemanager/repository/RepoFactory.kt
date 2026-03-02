package com.example.financemanager.repository

import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import com.example.financemanager.repository.data.RepoName

class RepoFactory(private val db: ExpenseManagementDatabase) {
    val accountRepo: AccountRepo by lazy { AccountRepo(db) }
    val transactionRepo: TransactionRepo by lazy { TransactionRepo(db) }
    val userRepo: UserRepo by lazy { UserRepo(db) }
    val appSettingRepo: AppSettingRepo by lazy { AppSettingRepo(db) }
    val categoryRepo: CategoryRepo by lazy { CategoryRepo(db) }
    val onboardingRepo: OnboardingRepo by lazy { OnboardingRepo(db) }
    val payeeCategoryRepo: PayeeCategoryRepo by lazy { PayeeCategoryRepo(db) }


    fun get(name: RepoName): Any {
        return when (name) {
            RepoName.ACCOUNT -> accountRepo
            RepoName.TRANSACTION -> transactionRepo
            RepoName.USER -> userRepo
            RepoName.APP_SETTING -> appSettingRepo
            RepoName.CATEGORY -> categoryRepo
            RepoName.ONBOARDING -> onboardingRepo
            RepoName.PAYEE_CATEGORY -> payeeCategoryRepo
        }
    }
}