
package com.example.catatankuliah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.catatankuliah.data.Catatan
import com.example.catatankuliah.ui.CatatanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val vm: CatatanViewModel = viewModel()
                CatatanKuliahScreen(vm)
            }
        }
    }
}

@Composable
fun CatatanKuliahScreen(vm: CatatanViewModel) {
    val daftar by vm.daftarCatatan.collectAsState()

    var tampilForm by remember { mutableStateOf(false) }
    var catatanDipilih by remember { mutableStateOf<Catatan?>(null) }
    var catatanDihapus by remember { mutableStateOf<Catatan?>(null) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Catatan Kuliah",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Simpan catatan perkuliahan secara offline.")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    catatanDipilih = null
                    tampilForm = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Tambah Catatan")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Daftar Catatan (${daftar.size})")

            Spacer(modifier = Modifier.height(8.dp))

            if (daftar.isEmpty()) {
                Text("Belum ada catatan. Silakan tambahkan catatan.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(daftar, key = { it.id }) { catatan ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text("ID: ${catatan.id}")

                                Text(
                                    text = catatan.judul,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(catatan.isi)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            catatanDipilih = catatan
                                            tampilForm = true
                                        }
                                    ) {
                                        Text("Edit")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            catatanDihapus = catatan
                                        }
                                    ) {
                                        Text("Hapus")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (tampilForm) {
        FormCatatan(
            catatan = catatanDipilih,
            onTutup = {
                tampilForm = false
            },
            onSimpan = { judul, isi ->
                val lama = catatanDipilih

                if (lama == null) {
                    vm.tambahCatatan(judul, isi)
                } else {
                    vm.editCatatan(lama, judul, isi)
                }

                tampilForm = false
            }
        )
    }

    catatanDihapus?.let { catatan ->
        AlertDialog(
            onDismissRequest = {
                catatanDihapus = null
            },
            title = {
                Text("Hapus Catatan")
            },
            text = {
                Text("Yakin ingin menghapus '${catatan.judul}'?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.hapusCatatan(catatan)
                        catatanDihapus = null
                    }
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        catatanDihapus = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun FormCatatan(
    catatan: Catatan?,
    onTutup: () -> Unit,
    onSimpan: (String, String) -> Unit
) {
    var judul by remember(catatan) {
        mutableStateOf(catatan?.judul ?: "")
    }

    var isi by remember(catatan) {
        mutableStateOf(catatan?.isi ?: "")
    }

    var error by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onTutup,
        title = {
            Text(
                if (catatan == null) {
                    "Tambah Catatan"
                } else {
                    "Edit Catatan"
                }
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = judul,
                    onValueChange = {
                        judul = it
                        error = ""
                    },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = isi,
                    onValueChange = {
                        isi = it
                        error = ""
                    },
                    label = { Text("Isi Catatan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (judul.isBlank() || isi.isBlank()) {
                        error = "Judul dan isi wajib diisi."
                    } else {
                        onSimpan(judul, isi)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onTutup) {
                Text("Batal")
            }
        }
    )
}
