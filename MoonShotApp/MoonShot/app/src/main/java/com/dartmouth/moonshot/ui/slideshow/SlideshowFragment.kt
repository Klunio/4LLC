package com.dartmouth.moonshot.ui.slideshow

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dartmouth.moonshot.*
import com.dartmouth.moonshot.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {


    lateinit var radgrp: RadioGroup
    lateinit var all: Button
    lateinit var eth: Button
    lateinit var bnb: Button
    lateinit var other1: Button

    lateinit var name: TextView
    lateinit var price: TextView
    lateinit var tfhp: TextView
    lateinit var mrktCap: TextView


    private lateinit var slideshowViewModel: SlideshowViewModel
    private var _binding: FragmentSlideshowBinding? = null


    private lateinit var coinListView: ListView
    private lateinit var arrayList: ArrayList<Coin>
    private lateinit var arrayAdapter: CoinListAdapterRec
    private lateinit var coinsByPubInterest: ArrayList<Coin>
    private lateinit var coinsBy24hChange: ArrayList<Coin>
    private lateinit var coinsByCurrentPrice: ArrayList<Coin>

    private lateinit var coinViewModel: CoinViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //------------------------------------------------------------------------------------

        radgrp = root.findViewById(R.id.radgrp)
        all = root.findViewById(R.id.all)
        eth = root.findViewById(R.id.eth)
        bnb = root.findViewById(R.id.bnb)
        other1 = root.findViewById(R.id.other1)

        /*name = root.findViewById(R.id.name)
        price = root.findViewById(R.id.price)
        tfhp = root.findViewById(R.id.tfhp)
        mrktCap = root.findViewById(R.id.mrktCap)*/

        coinListView = binding.listv

        //------------------------------------------------------------------------------------

        arrayList = ArrayList()
        arrayAdapter = CoinListAdapterRec(requireActivity(), arrayList)
        coinListView.adapter = arrayAdapter

        coinListView.setOnItemClickListener() { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val itemSelected: Coin = parent.getItemAtPosition(position) as Coin

            val indCoinIntent = Intent(requireActivity(), IndividualCoinActivity::class.java)

            indCoinIntent.putExtra(IndividualCoinActivity.NAME_KEY, itemSelected.name)
            indCoinIntent.putExtra(IndividualCoinActivity.SYMBOL_KEY, itemSelected.symbol)
            indCoinIntent.putExtra(IndividualCoinActivity.BLOCKCHAIN_TYPE_KEY, itemSelected.platforms)
            indCoinIntent.putExtra(IndividualCoinActivity.CURRENT_PRICE_KEY, itemSelected.currentPrice.toString())
            indCoinIntent.putExtra(IndividualCoinActivity.ADDRESS_KEY, itemSelected.address)
            indCoinIntent.putExtra(IndividualCoinActivity.IMAGE_LARGE_KEY, itemSelected.image_large)
            indCoinIntent.putExtra(IndividualCoinActivity.ID_KEY, itemSelected.id)
            indCoinIntent.putExtra(IndividualCoinActivity.HOMEPAGE_KEY, itemSelected.links_homepage)
            indCoinIntent.putExtra(IndividualCoinActivity.ANNOUNCEMENT_KEY, itemSelected.links_announcement_url)
            indCoinIntent.putExtra(IndividualCoinActivity.CHAT_KEY, itemSelected.links_chat_url)
            indCoinIntent.putExtra(IndividualCoinActivity.FACEBOOK_KEY, itemSelected.links_facebook_username)
            indCoinIntent.putExtra(IndividualCoinActivity.FORUM_KEY, itemSelected.links_official_forum_url)
            indCoinIntent.putExtra(IndividualCoinActivity.TWITTER_KEY, itemSelected.links_twitter_screen_name)
            indCoinIntent.putExtra(IndividualCoinActivity.PRICE_24_KEY, itemSelected.priceChangePercent24.toString())
            indCoinIntent.putExtra(IndividualCoinActivity.PUBLIC_INT_KEY, itemSelected.public_interest.toString())
            indCoinIntent.putExtra(IndividualCoinActivity.PRICE_30d_KEY, itemSelected.priceChangePercent30d.toString())
            indCoinIntent.putExtra(IndividualCoinActivity.MARK_RANK_KEY, itemSelected.marketCapRank?.toInt().toString())
            indCoinIntent.putExtra(IndividualCoinActivity.MARK_KEY, itemSelected.marketCap.toString())
            //Toast.makeText(this.activity, itemSelected.name, Toast.LENGTH_LONG).show()
            //Toast.makeText(this.activity, itemSelected.image_large.toString(), Toast.LENGTH_LONG).show()

            startActivity(indCoinIntent)
        }

        coinViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(CoinViewModel::class.java)


        /*arrayList = coinViewModel.getAllCoins().value as ArrayList<Coin>
        coinsBy24hChange = ArrayList(arrayList.sortedWith(compareByDescending({it.priceChangePercent24})))
        coinsByPubInterest = ArrayList(arrayList.sortedWith(compareByDescending({it.public_interest})))
        coinsByCurrentPrice = ArrayList(arrayList.sortedWith(compareByDescending({it.currentPrice})))
        arrayAdapter.replace(arrayList)
        arrayAdapter.notifyDataSetChanged()*/

        var t = 0
        coinViewModel.allCoins.observe(viewLifecycleOwner, Observer { allCoinsList ->
            coinsBy24hChange = ArrayList(allCoinsList.sortedWith(compareByDescending({it.priceChangePercent24})))
            coinsByPubInterest = ArrayList(allCoinsList.sortedWith(compareByDescending({it.public_interest})))
            coinsByCurrentPrice = ArrayList(allCoinsList.sortedWith(compareByDescending({it.currentPrice})))
            arrayList = allCoinsList as ArrayList<Coin>
            if(t == 0){
                arrayAdapter.replace(allCoinsList)
                arrayAdapter.notifyDataSetChanged()
                t = 1
            }
            //arrayAdapter.replace(allCoinsList)
            //arrayAdapter.notifyDataSetChanged()
        })

        //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        radgrp.setOnCheckedChangeListener { radioGroup, optionId ->
            run {
                when (optionId) {

                    R.id.all -> {
                        all.rotation = 30F
                        eth.rotation = 0F
                        bnb.rotation = 0F
                        other1.rotation = 0F

                        all.setTextColor(Color.parseColor("#FFFF00"))
                        eth.setTextColor(Color.parseColor("#FFFFFF"))
                        bnb.setTextColor(Color.parseColor("#FFFFFF"))
                        other1.setTextColor(Color.parseColor("#FFFFFF"))
                        arrayAdapter.replace(arrayList)
                        arrayAdapter.notifyDataSetChanged()
                    }
                    R.id.eth -> {
                        all.rotation = 0F
                        eth.rotation = 30F
                        bnb.rotation = 0F
                        other1.rotation = 0F


                        all.setTextColor(Color.parseColor("#FFFFFF"))
                        eth.setTextColor(Color.parseColor("#FFFF00"))
                        bnb.setTextColor(Color.parseColor("#FFFFFF"))
                        other1.setTextColor(Color.parseColor("#FFFFFF"))

                        arrayAdapter.replace(coinsByPubInterest)
                        arrayAdapter.notifyDataSetChanged()
                    }
                    R.id.bnb -> {
                        all.rotation = 0F
                        eth.rotation = 0F
                        bnb.rotation = 30F
                        other1.rotation = 0F

                        all.setTextColor(Color.parseColor("#FFFFFF"))
                        eth.setTextColor(Color.parseColor("#FFFFFF"))
                        bnb.setTextColor(Color.parseColor("#FFFF00"))
                        other1.setTextColor(Color.parseColor("#FFFFFF"))
                        arrayAdapter.replace(coinsBy24hChange)
                        arrayAdapter.notifyDataSetChanged()
                    }
                    R.id.other1 -> {
                        all.rotation = 0F
                        eth.rotation = 0F
                        bnb.rotation = 0F
                        other1.rotation = 30F

                        all.setTextColor(Color.parseColor("#FFFFFF"))
                        eth.setTextColor(Color.parseColor("#FFFFFF"))
                        bnb.setTextColor(Color.parseColor("#FFFFFF"))
                        other1.setTextColor(Color.parseColor("#FFFF00"))
                        arrayAdapter.replace(coinsByCurrentPrice)
                        arrayAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}