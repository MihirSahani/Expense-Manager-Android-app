package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import com.example.financemanager.repository.AccountRepo
import com.example.financemanager.repository.AppSettingRepo
import com.example.financemanager.repository.CategoryRepo
import com.example.financemanager.repository.OnboardingRepo
import com.example.financemanager.repository.PayeeCategoryRepo
import com.example.financemanager.repository.RepoFactory
import com.example.financemanager.repository.TransactionRepo
import com.example.financemanager.repository.UserRepo
import com.example.financemanager.repository.data.RepoName
import com.example.financemanager.viewmodel.data.ViewModelName


class ViewModelFactory(repoFactory: RepoFactory) {

    val initialVM: InitialVM by lazy {
        InitialVM(
            repoFactory.get(RepoName.ONBOARDING) as OnboardingRepo,
            repoFactory.get(RepoName.TRANSACTION) as TransactionRepo
        )
    }
    val accountVM: AccountVM by lazy {
        AccountVM(repoFactory.get(RepoName.ACCOUNT) as AccountRepo)
    }
    val transactionVM: TransactionVM by lazy {
        TransactionVM(
            repoFactory.get(RepoName.TRANSACTION) as TransactionRepo,
            repoFactory.get(RepoName.CATEGORY) as CategoryRepo,
            repoFactory.get(RepoName.ACCOUNT) as AccountRepo
        )
    }
    val userVM: UserVM by lazy {
        UserVM(repoFactory.get(RepoName.USER) as UserRepo)
    }
    val categoryVM: CategoryVM by lazy {
        CategoryVM(
            repoFactory.get(RepoName.CATEGORY) as CategoryRepo,
            repoFactory.get(RepoName.PAYEE_CATEGORY) as PayeeCategoryRepo
        )
    }
    val analysisVM: AnalysisVM by lazy {
        AnalysisVM(
            repoFactory.get(RepoName.TRANSACTION) as TransactionRepo,
            repoFactory.get(RepoName.CATEGORY) as CategoryRepo
        )
    }
    val settingsVM: SettingsVM by lazy {
        SettingsVM(repoFactory.get(RepoName.APP_SETTING) as AppSettingRepo)
    }
    val categoryAnalysisVM: CategoryAnalysisVM by lazy {
        CategoryAnalysisVM(repoFactory.get(RepoName.TRANSACTION) as TransactionRepo)
    }

    fun getViewModel(name: ViewModelName): ViewModel {
        return when (name) {
            ViewModelName.INITIAL -> initialVM
            ViewModelName.ACCOUNTS -> accountVM
            ViewModelName.TRANSACTION -> transactionVM
            ViewModelName.USER_DETAILS_UPDATE -> userVM
            ViewModelName.CATEGORY -> categoryVM
            ViewModelName.ANALYSIS -> analysisVM
            ViewModelName.SETTINGS -> settingsVM
            ViewModelName.CATEGORY_ANALYSIS -> categoryAnalysisVM
        }
    }
}
