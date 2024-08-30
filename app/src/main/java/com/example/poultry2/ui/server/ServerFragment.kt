package com.example.poultry2.ui.server


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.poultry2.R
import com.example.poultry2.databinding.FragmentServerBinding
import com.example.poultry2.ui.global.filter.Filter


class ServerFragment : Fragment() {

    private lateinit var adapter :ServerListAdapter

    private var _binding: FragmentServerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentServerBinding.inflate(inflater, container, false)


        setupMenu()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Filter.updated.observe(viewLifecycleOwner
        ) {
            showItems()
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu itemm
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_add, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item

                return when (menuItem.itemId) {
                    R.id.menu_add -> {
                        val intent = Intent(activity, ServerEntryActivity::class.java)
                        startActivity(intent)
                        true
                    }


                    else -> false}
                }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun showItems(){
        adapter = ServerListAdapter()
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val divider = DividerItemDecoration(activity,DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.divider)!!)
        binding.rvList.addItemDecoration(divider)

        adapter.setData(Filter.listServer)
        adapter.onItemClick = {
            val intent = Intent(activity, ServerEntryActivity::class.java)
            intent.putExtra("cid", it.cid)
            startActivity(intent)
        }
    }


}