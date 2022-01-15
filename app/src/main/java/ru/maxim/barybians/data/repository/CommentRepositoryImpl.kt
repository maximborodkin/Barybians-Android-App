package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.service.CommentService
import ru.maxim.barybians.data.persistence.PreferencesManager
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService,
    private val preferencesManager: PreferencesManager,
    private val repositoryBound: RepositoryBound
): CommentRepository {

}