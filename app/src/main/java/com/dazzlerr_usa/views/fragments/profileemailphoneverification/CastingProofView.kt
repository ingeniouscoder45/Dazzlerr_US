package com.dazzlerr_usa.views.fragments.profileemailphoneverification

interface CastingProofView
{
    fun onUpload()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}