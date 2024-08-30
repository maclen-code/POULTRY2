package com.example.poultry2.ui.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.poultry2.databinding.FragmentUserBinding
import com.example.poultry2.ui.function.MySharedPrep


class UserFragment : Fragment()  {

    private var _binding: FragmentUserBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserBinding.inflate(inflater, container, false)

        get()

        binding.btSave.setOnClickListener {
            save()
            findNavController().popBackStack()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun save(){
        val pin=binding.etPin.text.toString()
        MySharedPrep.set(requireContext(),"user","pin",pin)
    }

    private fun get(){
        val pin= MySharedPrep.get(requireContext(),"user","pin")
        binding.etPin.setText(pin)
    }


}


