package com.dazzlerr_usa.views.fragments.profile

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import timber.log.Timber


@BindingAdapter("profileImage")
fun loadProfileImage(view: ImageView, imageUrl: String)
{

    Timber.e("Image"+imageUrl)
    Glide.with(view.getContext())
            .load("https://www.dazzlerr.com/API/"+imageUrl).apply(RequestOptions().centerCrop())
            .placeholder(R.drawable.placeholder)
            .into(view)
}

class ProfilePojo {

    var status: String? = null
    var success: Boolean = false
    var data: List<DataBean>? = null

    class DataBean {

        var user_id: String? = null
        var cust_id: String? = null
        var username: String? = null
        var email: String? = null
        var email_isverified: String? = null
        var verified: String? = null
        var user_created_on: String? = null
        var name: String? = null
        var phone: String? = null
        var whats_app: String? = null
        var city: String? = null
        var current_city: String =""
        var state_id: String? = null
        var phone_isverified: String? = null
        var user_role_id: String? = null
        var user_role: String = ""
        var profile_tabs: String = ""
        var profile_id:String? = null
        var about: String? = null
        var exp_type: String=""
        var profile_pic: String? = null
        var gender: String? = null
        var dob: String? = null
        var age: String? = null
        var parent_name: String? = null
        var parent_contact: String? = null
        var languages: String? = null
        var available_for: String? = null
        var pref_location: String? = null
        var height: String? = null
        var weight: String? = null
        var chest: String? = null
        var waist: String? = null
        var hips: String? = null
        var eye_color: String? = null
        var hair_length: String? = null
        var hair_color: String? = null
        var hair_type: String? = null
        var skintone: String? = null
        var dress: String? = null
        var shoes: String? = null
        var ethnicity: String? = null
        var tags: String? = null
        var biceps: String? = null
        var hourly: String? = null
        var half_day: String? = null
        var full_day: String? = null
        var will_drive: String? = null
        var drive_miles: String? = null
        var availability: String? = null
        var payment_options: String? = null
        var will_fly:String? = null
        var test: String? = null
        var facebook: String? = null
        var twitter: String? = null
        var insta: String? = null
        var website: String? = null
        var marital_status: String? = null
        var is_published: String? = null
        var followers: Any? = null
        var likes: Any? = null
        var views: String? = null
        var last_seen: Any? = null
        var agency_signed: String? = null
        var is_followed: Int = 0
        var is_liked: Int = 0
        var is_shortlisted: Int = 0
        var agency_name: String? = null
        var agency_phone: String? = null
        var agency_email: String? = null
        var profile_view: String? = null
        var profile_hide: String? = null
        var is_public: String? = null
        var is_featured:String? = null
        var is_trending:String? = null
        var toprated:String? = null
        var is_face_of_the_week: String? = null
        var passport_ready: String? = null
        var interested_in: String? = null
        var created_by: String? = null
        var reference_url: String? = null
        var skills: String? = null
        var created_on: String? = null
        var updated_on: String? = null
        var state_name: String? = null
        var current_state: String =""
        var emp_publis: String? = null
        var followers_count: String = ""
        var likes_count: String = ""
        var photos_count: String = ""
        var is_document_verified: String = ""
        var is_document_submitted: String = ""
        var user_services: String = ""
        var user_products: String = ""
        var profile_complete: String = ""
        var account_type: String = ""
        var membership_id: String = ""
        var can_contact: String = ""
        var contact_count: String = ""
        var secondary_role: String = ""
        var sender_blocked: String = ""
        var country: String = ""
    }
}
