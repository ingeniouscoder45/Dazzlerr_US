package com.dazzlerr_usa.views.fragments.castingprofile.views

import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingProfilePojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioProjectsPojo

interface CastingProfileView
{
    fun onGetProfileSuccess(model: CastingProfilePojo)
    fun onProfilePicUploaded(url: String)
    fun onLikeOrDislike(status:String)//---1 for like and 0 for dislike
    fun onFollowOrUnfollow(status:String)//---1 for follow and 0 for unfollow
    fun onGetActiveJobsSuccess(model: CastingMyJobPojo)
    fun onGetProjectsSuccess(model: PortfolioProjectsPojo)
    fun onProjectDelete(position:Int)
    fun onFailed(message: String)
    fun onProfileFailed(message: String)
    fun showProgress(visiblity:Boolean)
}