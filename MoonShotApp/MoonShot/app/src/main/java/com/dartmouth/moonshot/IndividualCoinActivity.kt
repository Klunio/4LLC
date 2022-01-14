package com.dartmouth.moonshot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class IndividualCoinActivity: AppCompatActivity() {
    private lateinit var closeButton: Button
    private lateinit var chartButton: Button
    private lateinit var saveButton: Button
    private lateinit var coinHomepageButton: Button
    private lateinit var socialButton: Button

    private lateinit var profileViewModel: ProfileViewModel

    private var mFirebaseAuth: FirebaseAuth? = null

    private var address: String? = null
    private var blockchainType: String? = null
    private var currentPrice: String? = null
    private var dailyVolume: String? = null
    private var holders: String? = null
    private var name: String? = null
    private var symbol: String? = null
    private var imageLarge: String? = null
    private var coinID: String? = null
    private var homepageURL: String? = null

    private var announcement: String? = null
    private var chat: String? = null
    private var facebook: String? = null
    private var forum: String? = null
    private var twitter: String? = null

    private var savedCoinsList: ArrayList<String>? = null

    companion object coinSPKeys {
        val BUNDLE_KEY = "bundle"
        val ADDRESS_KEY = "address"
        val BLOCKCHAIN_TYPE_KEY = "blockchainType"
        val CURRENT_PRICE_KEY = "currentPrice"
        val DAILY_VOLUME_KEY = "dailyVolume"
        val HOLDERS_KEY = "holders"
        val NAME_KEY = "name"
        val SYMBOL_KEY = "symbol"
        val IMAGE_LARGE_KEY = "image-large"
        val ID_KEY = "id"
        val HOMEPAGE_KEY = "homepage"
        val ANNOUNCEMENT_KEY = "announcement"
        val CHAT_KEY = "chat"
        val FACEBOOK_KEY = "facebook"
        val FORUM_KEY = "forum"
        val TWITTER_KEY = "key"
        val PRICE_24_KEY = "price_24"
        val PRICE_30d_KEY = "price_30d"
        val PUBLIC_INT_KEY = "pubint"
        val MARK_RANK_KEY = "markrankkey"
        val MARK_KEY = "markkey"
        val SHOW_SAVE_KEY = "showSave"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_coin)

        closeButton = findViewById(R.id.button_close)
        closeButton.setOnClickListener(){
            finish()
        }

        address = intent.getStringExtra(ADDRESS_KEY)
        coinID = intent.getStringExtra(ID_KEY)

        mFirebaseAuth = FirebaseAuth.getInstance()
        //profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        profileViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                .create(ProfileViewModel::class.java)

        savedCoinsList = fromString(profileViewModel.getUser().value?.savedCoins)
        //Toast.makeText(this, savedCoinsList.toString(), Toast.LENGTH_LONG).show()

        saveButton = findViewById(R.id.button_save)
        saveButton.setOnClickListener(){
            if (savedCoinsList != null) {
                coinID?.let { it1 -> savedCoinsList!!.add(it1) }
                profileViewModel.updateSavedCoins(savedCoinsList!!)
            }
            finish()
        }

        chartButton = findViewById(R.id.soc)
        chartButton.setOnClickListener(){
            val url = "https://www.coingecko.com/en/coins/" + coinID
            showWebpage(url)
        }

        socialButton = findViewById(R.id.twit)
        socialButton.setOnClickListener(){
            showSocials()
        }


        imageLarge = intent.getStringExtra(IMAGE_LARGE_KEY)


        val nameTextView: TextView = findViewById(R.id.coinName)
        val symbolTextView: TextView = findViewById(R.id.coinSymbol)
        val bcTypeTextView: TextView = findViewById(R.id.blockChainTypeAB)
        val currPriceTextView: TextView = findViewById(R.id.price)
        val imageLargeImageView: ImageView = findViewById(R.id.coinPic)
        val addressTextView: TextView = findViewById(R.id.addressB)
        val price24TextView: TextView = findViewById(R.id.priceChangeB)
        val pubintTextView: TextView = findViewById(R.id.dailVB)
        val price30dTextView: TextView = findViewById(R.id.totalTransB)
        val marketCapRankTextView: TextView = findViewById(R.id.priceHighB)
        val marketCapTextView: TextView = findViewById(R.id.priceLowB)

        nameTextView.text = intent.getStringExtra(NAME_KEY)
        symbolTextView.text = intent.getStringExtra(SYMBOL_KEY)
        bcTypeTextView.text = intent.getStringExtra(BLOCKCHAIN_TYPE_KEY)
        currPriceTextView.text = "$" + intent.getStringExtra(CURRENT_PRICE_KEY)
        addressTextView.text = intent.getStringExtra(ADDRESS_KEY)
        price24TextView.text = intent.getStringExtra(PRICE_24_KEY) + "%"
        pubintTextView.text = intent.getStringExtra(PUBLIC_INT_KEY) + "%"
        price30dTextView.text = intent.getStringExtra(PRICE_30d_KEY) + "%"
        marketCapRankTextView.text =  intent.getStringExtra(MARK_RANK_KEY)
        marketCapTextView.text =  "$" + intent.getStringExtra(MARK_KEY)
        Picasso.get().load(imageLarge).into(imageLargeImageView)
    }


    override fun onResume() {
        super.onResume()
        savedCoinsList = fromString(profileViewModel.getUser().value?.savedCoins)
        // Get coin info from intent
        //val bundle = intent.getBundleExtra(BUNDLE_KEY)
        address = intent.getStringExtra(ADDRESS_KEY)
        blockchainType = intent.getStringExtra(BLOCKCHAIN_TYPE_KEY)
        currentPrice = intent.getStringExtra(CURRENT_PRICE_KEY)
        coinID = intent.getStringExtra(ID_KEY)
        //dailyVolume = intent.getStringExtra(DAILY_VOLUME_KEY)
        //holders = intent.getStringExtra(HOLDERS_KEY)
        name = intent.getStringExtra(NAME_KEY)
        symbol = intent.getStringExtra(SYMBOL_KEY)
        homepageURL = intent.getStringExtra(HOMEPAGE_KEY)

        announcement = intent.getStringExtra(ANNOUNCEMENT_KEY)
        chat = intent.getStringExtra(CHAT_KEY)
        facebook = intent.getStringExtra(FACEBOOK_KEY)
        forum = intent.getStringExtra(FORUM_KEY)
        twitter = intent.getStringExtra(TWITTER_KEY)

        Log.d("debug", "twitter = $twitter")

        coinHomepageButton = findViewById(R.id.web)
        if(homepageURL != null && homepageURL != ""){
            coinHomepageButton.setOnClickListener() {
                showWebpage(homepageURL)
            }
        } else {
            coinHomepageButton.isVisible = false
        }

        val showSaveKey = intent.getBooleanExtra(SHOW_SAVE_KEY, true)
        if(savedCoinsList!!.contains(coinID) || !showSaveKey ){
            saveButton.isVisible = false
        }

    }

    fun showSocials(){
        val intent = Intent(this, SocialsActivity::class.java)
        intent.putExtra(SocialsActivity.ANNOUNCEMENT_KEY, announcement)
        intent.putExtra(SocialsActivity.CHAT_KEY, chat)
        intent.putExtra(SocialsActivity.FACEBOOK_KEY, facebook)
        intent.putExtra(SocialsActivity.FORUM_KEY, forum)
        intent.putExtra(SocialsActivity.TWITTER_KEY, twitter)
        startActivity(intent)
    }

    fun showWebpage(url: String?){
        var finalUrl = url
        if(url!!.subSequence(0,5) == "http:"){
            val end = url!!.substring(5)
            finalUrl = "https:" + end
        }
        val intent = Intent(this, CoinWebpageActivity::class.java)
        // Hardcode
        intent.putExtra(CoinWebpageActivity.URL_KEY, finalUrl)
        this.startActivity(intent)
    }

    fun fromString(value: String?): ArrayList<String>? {
        val listType = object : TypeToken<ArrayList<String>?>() {}.type
        return Gson().fromJson(value, listType)
    }
}