package com.example.loginregisterapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.loginregisterapp.databinding.ActivityLoginBinding
import com.example.loginregisterapp.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        // Menghubungkan ViewModel ke layout
        binding.viewModel = userViewModel
        binding.lifecycleOwner = this

        // Mengamati perubahan userLiveData untuk menunjukkan login berhasil atau gagal
        userViewModel.userLiveData.observe(this) { user ->
            if (user != null) {
                // Login berhasil, lanjut ke HomeActivity atau halaman lain
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Jika login gagal, tampilkan pesan peringatan
                Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup aksi untuk tombol login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Mengatur nilai pada ViewModel untuk login
            userViewModel.email.value = email
            userViewModel.password.value = password

            // Panggil fungsi login dari ViewModel
            userViewModel.login()
        }
    }
}



