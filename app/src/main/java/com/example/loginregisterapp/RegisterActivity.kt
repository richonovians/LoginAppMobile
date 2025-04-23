package com.example.loginregisterapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.loginregisterapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Menggunakan DataBinding
        val binding: ActivityRegisterBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_register)

        // Menghubungkan ViewModel ke layout
        binding.viewModel = userViewModel
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userViewModel.userLiveData.observe(this) { user ->
            user?.let {
                Toast.makeText(this, "Registrasi berhasil: ${it.name}", Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.registrationSuccess.observe(this) { success ->
            if (success) {
                // Pindah ke halaman login jika registrasi berhasil
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()  // Menutup halaman register agar tidak bisa kembali ke halaman ini
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val phone = binding.etPhone.text.toString()
            val address = binding.etAddress.text.toString()

            // Register User dengan data yang dimasukkan
            val user = User(name = name, email = email, password = password, phone = phone, address = address)
            userViewModel.register(user)
        }
    }
}

