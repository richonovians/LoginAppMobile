package com.example.loginregisterapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.loginregisterapp.databinding.ActivityUpdateProfileBinding

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // ✅ Tambahkan baris ini untuk ambil user yang login
        viewModel.fetchLoggedInUser()

        viewModel.userLiveData.observe(this) { user ->
            if (user != null) {
                // Set data ke EditText
                binding.etName.setText(user.name)
                binding.etEmail.setText(user.email)
                binding.etPassword.setText(user.password)
                binding.etPhone.setText(user.phone)
                binding.etAddress.setText(user.address)

                // ✅ Sync juga ke LiveData agar data binding bekerja
                viewModel.name.value = user.name
                viewModel.email.value = user.email
                viewModel.password.value = user.password
                viewModel.phone.value = user.phone
                viewModel.address.value = user.address
            }
        }

        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
