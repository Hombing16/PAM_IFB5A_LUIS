package com.example.catatankuliah.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.catatankuliah.data.Catatan
import com.example.catatankuliah.data.CatatanDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatatanViewModel(application: Application) :
    AndroidViewModel(application) {

    private val dao = CatatanDatabase
        .getDatabase(application)
        .catatanDao()

    val daftarCatatan = dao.semuaCatatan().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    fun tambahCatatan(judul: String, isi: String) {
        if (judul.isBlank() || isi.isBlank()) return

        viewModelScope.launch {
            dao.tambah(
                Catatan(
                    judul = judul.trim(),
                    isi = isi.trim(),
                )
            )
        }
    }

    fun editCatatan(catatan: Catatan, judul: String, isi: String) {
        if (judul.isBlank() || isi.isBlank()) return

        viewModelScope.launch {
            dao.ubah(
                catatan.copy(
                    judul = judul.trim(),
                    isi = isi.trim(),
                )
            )
        }
    }

    fun hapusCatatan(catatan: Catatan) {
        viewModelScope.launch {
            dao.hapus(catatan)
        }
    }
}
