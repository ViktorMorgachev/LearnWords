package com.learn.worlds.data.dataSource.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LearningRemoteItemsDataSource @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher){


    val database  by lazy { Firebase.database }
    val databaseRef = database.getReference("")

    private val _learningItems: MutableStateFlow<Result<List<LearningItemAPI>>> = MutableStateFlow(Result.Success(mutableListOf<LearningItemAPI>()))
    val learningItems: Flow<Result<List<LearningItemAPI>>> = _learningItems.asStateFlow()

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() +  dispatcher)

    init {
        subsribeToDatabase()
    }

    fun subsribeToDatabase(){
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue<@JvmSuppressWildcards List<LearningItemAPI>>()
                value?.let {
                    scope.launch {
                        _learningItems.emit(Result.Success<List<LearningItemAPI>>(it))
                    }
                }
                Timber.d("data from remote database: ${value}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Timber.d("data from remote database: ${error}")
                scope.launch {
                    _learningItems.emit(Result.Error())
                }
            }
        })
    }


    suspend fun addLearningItem(learningItemAPI: LearningItemAPI) = flow<Result<Any>> {
        Timber.e("addLearningItem: learningItemDB $learningItemAPI")
        try {
            emit(Result.Loading)
            databaseRef.child("").setValue(learningItemAPI)
            emit(Result.Complete)
        } catch (t: Throwable){
            Timber.e(t)
            emit(Result.Error())
        }

    }


}