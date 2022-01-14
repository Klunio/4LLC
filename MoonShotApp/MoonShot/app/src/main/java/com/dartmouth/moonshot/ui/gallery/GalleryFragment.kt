package com.dartmouth.moonshot.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dartmouth.moonshot.*
import com.dartmouth.moonshot.databinding.FragmentGalleryBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    private lateinit var coinListView: RecyclerView
    private lateinit var arrayList: ArrayList<Coin>
    private lateinit var arrayAdapter: CoinListAdapter

    private lateinit var coinViewModel: CoinViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var savedCoinsList: ArrayList<String>

    private var cList: ArrayList<Coin>? = null
    private lateinit var cIDs : ArrayList<String>


    // This property is only valid between onCreateView and
    // onDestroyView.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ret = inflater.inflate(R.layout.fragment_gallery, container, false)

        coinListView = ret.findViewById(R.id.listview_savedcoins)

        coinViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(CoinViewModel::class.java)

        profileViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(ProfileViewModel::class.java)

        arrayList = ArrayList()
        arrayAdapter = CoinListAdapter(requireActivity(), arrayList)
        coinListView.layoutManager = LinearLayoutManager(requireContext())
        coinListView.adapter = arrayAdapter

        val swipingGesture = object : SwipingGesture(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT -> {
                        //add delete from database here
                        cIDs = ArrayList<String>()
                        for(coind in 0..cList!!.size-1){
                            if(coind != viewHolder.adapterPosition){
                                cList!!.get(coind).id?.let { cIDs.add(it) }
                            }
                        }
                        //profileViewModel.updateSavedCoins(cIDs)
                        arrayAdapter.deleteItem(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipingGesture)
        touchHelper.attachToRecyclerView(coinListView)


        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { userModel ->
            savedCoinsList = fromString(userModel.savedCoins)
            //Toast.makeText(this.activity, savedCoinsList.toString(), Toast.LENGTH_LONG).show()
            cList = coinViewModel.getSavedCoins(savedCoinsList).value
          
            if(cList == null){
                coinViewModel.getSavedCoins(savedCoinsList).observe(viewLifecycleOwner, Observer {
                    cList = it
                    arrayAdapter.replace(cList as ArrayList<Coin>)
                    arrayAdapter.notifyDataSetChanged()
                })
            }else{
                coinViewModel.getSavedCoins(savedCoinsList).removeObservers(this)
                arrayAdapter.replace(cList as ArrayList<Coin>)
                arrayAdapter.notifyDataSetChanged()
            }
        })

        /*coinViewModel.getSavedCoins(savedCoinsList).observe(viewLifecycleOwner, Observer {
            arrayAdapter.replace(it as ArrayList<Coin>)
            arrayAdapter.notifyDataSetChanged()
        })*/


        /*val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, coinList)
        listView.adapter = adapter

        listView.setOnItemClickListener() { _, _, _, _ ->
            val intent = Intent(requireActivity(), IndividualCoinActivity::class.java)
            requireActivity().startActivity(intent)
        }*/

        return ret
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { userModel ->
            savedCoinsList = fromString(userModel.savedCoins)
            //Toast.makeText(this.activity, savedCoinsList.toString(), Toast.LENGTH_LONG).show()
            cList = coinViewModel.getSavedCoins(savedCoinsList).value
            //println(cList.toString())
            if(cList != null){
                //Toast.makeText(this.activity, cList.size.toString(), Toast.LENGTH_LONG).show()
                arrayAdapter.replace(cList as ArrayList<Coin>)
                arrayAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if(this::cIDs.isInitialized){
            profileViewModel.updateSavedCoins(cIDs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    fun fromString(value: String?): ArrayList<String> {
        val listType = object : TypeToken<ArrayList<String>?>() {}.type
        return Gson().fromJson(value, listType)
    }
}
