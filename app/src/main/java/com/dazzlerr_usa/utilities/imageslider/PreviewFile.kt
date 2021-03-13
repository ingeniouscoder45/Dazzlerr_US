package com.dazzlerr_usa.utilities.imageslider

import java.io.Serializable

/**
 * Created by Arvind on 4/7/19
 */

class PreviewFile(var imageURL: String?
                  , var isLikeEnabled: Boolean?
                  , var isDownload_enabled: Boolean?
                  , var user_id:String
                  , var portfolio_id:String
                  , var profile_id:String
                  , var is_heart:Int
                  , var is_like:Int// is_like or is_heart
                  , var total_likes:String
                  , var total_hearts: String

) : Serializable
