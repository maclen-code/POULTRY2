package com.example.poultry.ui.login


import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.TransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.poultry.databinding.ActivityLoginBinding
import com.example.poultry.ui.MainActivity
import com.example.poultry.ui.function.MySharedPrep
import com.example.poultry.ui.server.ServerEntryActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var pin=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        binding.etPinCode.requestFocus()

        etPinCodeTextChangeListener()
        pin= MySharedPrep.get(this,"user","pin")
        if (pin=="") {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContentView(binding.root)

    }

    private fun etPinCodeTextChangeListener(){

        binding.etPinCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etPinCode.length() == pin.length ) {
                    if (binding.etPinCode.text.toString()==pin) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else
                        Toast.makeText(this@LoginActivity,"Invalid pin",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }



}
