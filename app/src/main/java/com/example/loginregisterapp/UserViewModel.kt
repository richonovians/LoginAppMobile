package com.example.loginregisterapp

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val prefs: SharedPreferences = application.getSharedPreferences("user_prefs", 0)

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val userLiveData = MutableLiveData<User?>()  // Mengubah menjadi nullable
    val registrationSuccess = MutableLiveData<Boolean>()
    val updateSuccess = MutableLiveData<Boolean>()  // Menandakan apakah update berhasil
    val userListLiveData = MutableLiveData<List<User>>()

    // Ambil user yang sedang login berdasarkan email yang disimpan di SharedPreferences
    fun fetchLoggedInUser() {
        val email = prefs.getString("email", null)
        if (email != null) {
            viewModelScope.launch {
                val user = userDao.getUserByEmail(email)
                userLiveData.postValue(user)
            }
        } else {
            userLiveData.postValue(null)  // Jika tidak ada user yang login
        }
    }

    fun onRegisterClick() {
        val nameValue = name.value ?: ""
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""
        val phoneValue = phone.value ?: ""
        val addressValue = address.value ?: ""

        val newUser = User(
            name = nameValue,
            email = emailValue,
            password = passwordValue,
            phone = phoneValue,
            address = addressValue
        )

        register(newUser)
        Log.d("UserViewModel", "Tombol Register diklik")
    }

    fun onRegisterTextClick() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, RegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun onLoginTextClick() {
        // Arahkan ke halaman login
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    // Fungsi untuk memperbarui profil
    fun onUpdateProfileClick() {
        val currentUser = userLiveData.value
        if (currentUser != null) {
            val updatedUser = User(
                id = currentUser.id,
                name = name.value ?: "",
                email = email.value ?: "",
                password = password.value ?: "",
                phone = phone.value ?: "",
                address = address.value ?: ""
            )

            update(updatedUser)
        }
    }


    // Fungsi untuk mengupdate data pengguna di database
    private fun updateUserProfile(user: User) = viewModelScope.launch {
        val currentUser = userLiveData.value
        if (currentUser != null) {
            // Update berdasarkan ID yang sedang login
            val updatedUser = user.copy(id = currentUser.id)
            userDao.update(updatedUser)
            userLiveData.postValue(updatedUser)
            updateSuccess.postValue(true)  // Tandai update berhasil
        }
    }


    fun register(user: User) = viewModelScope.launch {
        userDao.insert(user)
        userLiveData.postValue(user)
        registrationSuccess.postValue(true)  // Update status setelah registrasi berhasil
    }

    fun login() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""

        if (emailValue.isEmpty() || passwordValue.isEmpty()) {
            // Jika email atau password kosong, beri peringatan
            userLiveData.postValue(null)  // Mengirimkan null jika ada masalah
            return
        }

        viewModelScope.launch {
            val user = userDao.login(emailValue, passwordValue)
            if (user != null) {
                // Simpan email user di SharedPreferences setelah login berhasil
                prefs.edit().putString("email", user.email).apply()
                userLiveData.postValue(user)  // Kirimkan user ke LiveData
            } else {
                userLiveData.postValue(null)  // Login gagal
            }
        }
    }

    fun logout() {
        // Hapus email user dari SharedPreferences
        prefs.edit().remove("email").apply()

        // Kosongkan LiveData agar data tidak terbawa
        name.postValue("")
        email.postValue("")
        password.postValue("")
        phone.postValue("")
        address.postValue("")
        userLiveData.postValue(null)
    }

    fun update(user: User) = viewModelScope.launch {
        userDao.update(user)
        userLiveData.postValue(user)
        updateSuccess.postValue(true)
    }
}