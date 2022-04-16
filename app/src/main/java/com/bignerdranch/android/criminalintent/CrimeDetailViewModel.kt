package com.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

// crimefragment has a crimeid and to display crime data, it needs to pull crime object from db
// when crimefragment requests a crime via its ID, this view model should getCrime using the UUID
// when request is complete, viewmodel should notify the fragment and pass along the crime object

class CrimeDetailViewModel(): ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    // stores ID of crime currently displayed by crimefragment
    private val crimeIdLiveData = MutableLiveData<UUID>()

    // live data transformation is a way to set up a trigger-response relationship bw two livedata objects
    // transformation takes two inputs = livedata object used as a trigger and a mapping function
    // that returns a livedata object (a transformation result) whose value is updated every time
    // a new value gets set on the trigger livedata instance
    var crimeLiveData: LiveData<Crime?> = Transformations.switchMap(crimeIdLiveData) { crimeId ->
        crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)  // writes crime to db
    }

    // exposes file info to CrimeFragment
    fun getPhotoFile(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }
}