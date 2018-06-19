package com.mylocations.network

import android.content.res.AssetManager
import com.google.gson.Gson
import com.mylocations.network.model.ResponseModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Remote DataHandler acts as the medium to communicate to web services and returns the
 * results to main DataHandler
 */
class NetworkHandler(val asset: AssetManager) {

    //Executor for performing work in a separate thread.
    private val executor: Executor

    init {
        executor = Executors.newSingleThreadExecutor()
    }

    /**
     * Convert json string to ResponseModel object and notify the dataHandler.
     */
    fun getLocationsFromFile(onRemoteDataReadyCallback: OnRemoteDataReadyCallback) {
        executor.execute({
            val gson = Gson()
            val responseModel: ResponseModel = gson.fromJson(readJSONFromAssets(), ResponseModel::class.java)
            onRemoteDataReadyCallback.onRemoteDataReady(responseModel)
        })
    }

    /**
     * Utility function to read the json file from assets and return string.
     */
    fun readJSONFromAssets(): String {
        val inputStream = asset.open("locations.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }
}

/**
 * Callback interface to notify the calling class after importing json file to object
 */
interface OnRemoteDataReadyCallback {
    fun onRemoteDataReady(responseModel: ResponseModel)
}