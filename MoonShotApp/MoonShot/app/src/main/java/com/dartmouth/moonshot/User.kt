package com.dartmouth.moonshot

import android.widget.ImageView
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.Glide
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

data class User(
    var name : String? = null,
    var bio : String? = null,
    var savedCoins: String? = "[ , ]",
    var image: String? = null
){

    companion object {

        @JvmStatic
        @BindingAdapter("imageUrl")
        //fun loadImage(view: CircleImageView, imageUrl: String?) {
        fun loadImage(view: ImageView, imageUrl: String?) {
            imageUrl?.let {
                //Glide.with(view.context).load(imageUrl).into(view)
                Picasso.get().load(imageUrl).rotate(90F).into(view)
            }
        }
    }

}
