package com.dartmouth.moonshot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_individual_coin.*

class SocialsActivity: AppCompatActivity() {
    private lateinit var announcementTV: LinearLayout
    private lateinit var chatTV: LinearLayout
    private lateinit var facebookTV: LinearLayout
    private lateinit var forumTV: LinearLayout
    private lateinit var twitterTV: LinearLayout

    private lateinit var announcementTVt: TextView
    private lateinit var chatTVt: TextView
    private lateinit var facebookTVt: TextView
    private lateinit var forumTVt: TextView
    private lateinit var twitterTVt: TextView

    private lateinit var closeButton: Button

    private var announcement: String? = null
    private var chat: String? = null
    private var facebook: String? = null
    private var forum: String? = null
    private var twitter: String? = null

    companion object {
        val ANNOUNCEMENT_KEY = "announcement"
        val CHAT_KEY = "chat"
        val FACEBOOK_KEY = "facebook"
        val FORUM_KEY = "forum"
        val TWITTER_KEY = "key"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socials)

        announcementTV = findViewById(R.id.text_announcement)
        chatTV = findViewById(R.id.text_chat)
        facebookTV = findViewById(R.id.text_facebook)
        forumTV = findViewById(R.id.text_forum)
        twitterTV = findViewById(R.id.text_twitter)

        announcementTVt = findViewById(R.id.text_announcementt)
        chatTVt = findViewById(R.id.text_chatt)
        facebookTVt = findViewById(R.id.text_facebookt)
        forumTVt = findViewById(R.id.text_forumt)
        twitterTVt = findViewById(R.id.text_twittert)

        closeButton = findViewById(R.id.button_close)
        closeButton.setOnClickListener(){
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        announcement = intent.getStringExtra(ANNOUNCEMENT_KEY)
        chat = intent.getStringExtra(CHAT_KEY)
        facebook = intent.getStringExtra(FACEBOOK_KEY)
        forum = intent.getStringExtra(FORUM_KEY)
        twitter = intent.getStringExtra(TWITTER_KEY)

        if(announcement != null && announcement != ""){
            announcementTVt.text = "Coin's announcements"
            announcementTV.setOnClickListener(){
                showWebpage(announcement)
            }
        }

        if(chat != null && chat != ""){
            chatTVt.text = "Coin's Discord"
            chatTV.setOnClickListener(){
                showWebpage(chat)
            }
        }

        if(facebook != null && facebook != ""){
            facebookTVt.text = "Coin's Facebook"
            facebookTV.setOnClickListener(){
                val url = "https://www.facebook.com/" + facebook
                showWebpage(url)
            }
        }

        if(forum != null && forum != ""){
            forumTVt.text = "Coin's official forum"
            forumTV.setOnClickListener(){
                showWebpage(forum)
            }
        }

        if(twitter != null && twitter != ""){
            twitterTVt.text = "Coin's twitter"
            twitterTV.setOnClickListener(){
                val url = "https://twitter.com/" + twitter
                showWebpage(url)
            }
        }
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
}