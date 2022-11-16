package and09.multiweatherapp.ui.home

import and09.multiweatherapp.MainActivity
import and09.multiweatherapp.R
import and09.multiweatherapp.databinding.FragmentHomeBinding
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers.Main


class HomeFragment : Fragment() {



    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)



        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textViewLocation = root.findViewById<TextView>(R.id.textview_location)
        homeViewModel.location.observe(viewLifecycleOwner, Observer {
            textViewLocation.text = it
        })

        val textViewTemperature = root.findViewById<TextView>(R.id.textview_temperature)
        homeViewModel.temperature.observe(viewLifecycleOwner, Observer {
            textViewTemperature.text = it
        })

        val textViewDescription = root.findViewById<TextView>(R.id.textview_description)
        homeViewModel.description.observe(viewLifecycleOwner, Observer {
            textViewDescription.text = it
        })

        val textViewProvider = root.findViewById<TextView>(R.id.textview_provider)
        homeViewModel.provider.observe(viewLifecycleOwner, Observer {
            textViewProvider.text = it
        })

        val textViewIconBitmap = root.findViewById<ImageView>(R.id.imageview_weathericon)
        homeViewModel.iconBitmap.observe(viewLifecycleOwner, Observer {
            textViewIconBitmap.setImageBitmap(it)
        })
        
        /*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        homeViewModel.doAction()
         */

        homeViewModel.retrieveWeatherData()

        return root
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}