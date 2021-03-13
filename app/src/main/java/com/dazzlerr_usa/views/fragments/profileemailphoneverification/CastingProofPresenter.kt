package com.dazzlerr_usa.views.fragments.profileemailphoneverification

import java.io.File

interface CastingProofPresenter
{

    fun uploadCastingImageProof(user_id: String, proof: File, type: String)
    fun uploadCastingVideoProof(user_id: String, proof: File, type: String)
    fun cancelRetrofitRequest()
}