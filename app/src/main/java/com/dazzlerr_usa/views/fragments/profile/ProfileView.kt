package com.dazzlerr_usa.views.fragments.profile

import com.dazzlerr_usa.views.fragments.profile.pojo.GetContactDetailsPojo

interface ProfileView
{
    fun onGetProfileSuccess(model: ProfilePojo)
    fun onProfilePicUploaded(url: String)
    fun onLikeOrDislike(status:String)//---1 for like and 0 for dislike
    fun onFollowOrUnfollow(status:String)//---1 for like and 0 for dislike
    fun onShortList(status:String)//---1 for Shortlist and 0 for de-shortlist
    fun onFailed(message: String)
    fun onProfileFailed(message: String)
    fun showProgress(visiblity:Boolean)
    fun onGetContactDetails(model: GetContactDetailsPojo)
}