package com.example.poultry2.ui.global.filter.period

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.poultry2.databinding.FragmentFilterPeriodBinding
import com.example.poultry2.ui.function.MyDate
import com.example.poultry2.ui.function.MyDate.toLocalDate
import com.example.poultry2.ui.global.filter.Filter
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.format.DateTimeFormatter


class FilterPeriodFragment : Fragment() {
    private var _binding: FragmentFilterPeriodBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFilterPeriodBinding.inflate(inflater, container, false)


            val f=Filter.dates.from.toLocalDate()
            val t=Filter.dates.to.toLocalDate()

            val first:String
            val second:String
            if (f.year !=t.year){
                first=f.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                second=t.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
            }else
            {
                first=f.format(DateTimeFormatter.ofPattern("MMM d"))
                second=t.format(DateTimeFormatter.ofPattern("MMM d"))
            }
            binding.tvDateRange.text="$first - $second"

        addClickListenerToTextView(arrayOf(binding.tvTap,binding.tvDateRange))

        return binding.root
    }

    private fun addClickListenerToTextView(textViewArray: Array<TextView>) {

        textViewArray.forEach { it ->
            it.setOnClickListener {
                val longFrom= MyDate.formatDateTimeStringToLong(Filter.dates.from+"T00:00:00.01Z")
                val longTo= MyDate.formatDateTimeStringToLong(Filter.dates.to+"T00:00:00.01Z")

                val builder = MaterialDatePicker.Builder.dateRangePicker()
                builder.setSelection(androidx.core.util.Pair(longFrom,longTo))
                val datePicker = builder.build()
                datePicker.show(childFragmentManager, "DatePicker")

                // Setting up the event for when ok is clicked
                datePicker.addOnPositiveButtonClickListener {

                    val from = MyDate.formatLongToLocalDateTime(it.first)
                    val to = MyDate.formatLongToLocalDateTime(it.second)


                    if (from.year==to.year && from.month==to.month) {
                        Filter.setDates(from.toLocalDate(),to.toLocalDate())
                        binding.tvDateRange.text= Filter.range
                    }else{
                        Toast.makeText(requireContext(),"Invalid date selection",Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


}

