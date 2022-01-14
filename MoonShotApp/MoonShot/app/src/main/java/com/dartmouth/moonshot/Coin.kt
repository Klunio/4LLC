package com.dartmouth.moonshot

import com.google.firebase.database.PropertyName

data class Coin(
    var name: String? = null,
    var symbol: String? = null,
    var public_interest: Double? = null,
    var address: String? = null,
    var id: String? = null,
    var platforms: String? = "unknown",
    @get:PropertyName("image-large") @set:PropertyName("image-large")
    var image_large: String? = null,
    @get:PropertyName("image-small") @set:PropertyName("image-small")
    var image_small: String? = null,
    @get:PropertyName("image-thumb") @set:PropertyName("image-thumb")
    var image_thumb: String? = null,
    @get:PropertyName("links-announcement_url") @set:PropertyName("links-announcement_url")
    var links_announcement_url: String? = null,
    @get:PropertyName("links-chat_url") @set:PropertyName("links-chat_url")
    var links_chat_url: String? = null,
    @get:PropertyName("links-facebook_username") @set:PropertyName("links-facebook_username")
    var links_facebook_username: String? = null,
    @get:PropertyName("links-homepage") @set:PropertyName("links-homepage")
    var links_homepage: String? = null,
    @get:PropertyName("links-official_forum_url") @set:PropertyName("links-official_forum_url")
    var links_official_forum_url: String? = null,
    @get:PropertyName("links-twitter_screen_name") @set:PropertyName("links-twitter_screen_name")
    var links_twitter_screen_name: String? = null,
    @get:PropertyName("links-blockchain_site") @set:PropertyName("links-blockchain_site")
    var links_blockchain_site: String? = null,
    @get:PropertyName("market_data-current_price") @set:PropertyName("market_data-current_price")
    var currentPrice: Double? = null,
    @get:PropertyName("market_data-market_cap") @set:PropertyName("market_data-market_cap")
    var marketCap: Double? = null,
    @get:PropertyName("market_data-market_cap_change_24h") @set:PropertyName("market_data-market_cap_change_24h")
    var priceChange24: Double? = null,
    @get:PropertyName("market_data-price_change_percentage_24h") @set:PropertyName("market_data-price_change_percentage_24h")
    var priceChangePercent24: Double? = null,
    @get:PropertyName("market_data-price_change_percentage_30d") @set:PropertyName("market_data-price_change_percentage_30d")
    var priceChangePercent30d: Double? = null,
    @get:PropertyName("market_data-market_cap_rank") @set:PropertyName("market_data-market_cap_rank")
    var marketCapRank: Double? = null
    //var totalTransactions: String? = null,
    //var price24Low: String? = null,
    //var price24High: String? = null,
)
