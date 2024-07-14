package com.example.expogbss.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.expogbss.FragmentPageAdapter
import com.example.expogbss.R
import com.example.expogbss.databinding.FragmentBuscarBinding
import com.google.android.material.tabs.TabLayout

class dddDashboardFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: FragmentPageAdapter
    private var _binding: FragmentBuscarBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentBuscarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Llamar a findViewById en la vista inflada
        tabLayout = binding.root.findViewById(R.id.tabLayout)
        viewPager2 = binding.root.findViewById(R.id.viewPager2)

        // Asegurarse de que las vistas no son null
        if (tabLayout == null) {
            Log.e("DashboardFragment", "View with ID 'tabLayout' not found in layout")
        } else {
            // Configurar el TabLayout si es necesario
        }

        if (viewPager2 == null) {
            Log.e("DashboardFragment", "View with ID 'viewPager2' not found in layout")
        } else {
            // Configurar el ViewPager2 si es necesario
        }

        // Utilizar childFragmentManager en lugar de supportFragmentManager
        adapter = FragmentPageAdapter(childFragmentManager, lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("First"))
        tabLayout.addTab(tabLayout.newTab().setText("Second"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No se necesita implementación
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No se necesita implementación
            }
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        dashboardViewModel.text.observe(viewLifecycleOwner) {
            // Actualizar la UI con los datos del ViewModel
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
