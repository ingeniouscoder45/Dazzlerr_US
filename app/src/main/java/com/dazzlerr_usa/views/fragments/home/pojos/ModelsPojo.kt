package com.dazzlerr_usa.views.fragments.home.pojos

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R

@BindingAdapter("modelImage")
fun loadModelImage(view: ImageView, imageUrl: String)
{
    Glide.with(view.getContext())
            .load("https://www.dazzlerr.com/API/"+imageUrl).apply(RequestOptions().centerCrop())
            .thumbnail(.2f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.model_placeholder)
            .into(view)
}

class ModelsPojo
{

    /**
     * status : true
     * message : User details Successfully!
     * data : [{"user_id":2913,"name":"Karina Murashevska","user_role":"Model","exp_type":"Experienced","profile_pic":"assets/images/2913/profile/45BFB1C3-26A0-A164-D83E-709130B9E72E.jpg"},{"user_id":2782,"name":"Saanika Sahoonja","user_role":"Model","exp_type":"Novice","profile_pic":"assets/images/2782/profile/ABA4E86E-383B-8793-761A-DA580C4B739C.jpg"},{"user_id":1424,"name":"Nitish Johar","user_role":"Model","exp_type":"Experienced","profile_pic":"assets/images/1424/profile/C478545B-0E81-7823-1D5A-D97A11ECA105.jpg"},{"user_id":1128,"name":"Nidhi Mishra","user_role":"Model","exp_type":"Novice","profile_pic":"assets/images/1128/profile/86427CF0-7C42-612E-674A-D90A63B1A6E7.jpg"},{"user_id":1109,"name":"Nishchal Mishra","user_role":"Model","exp_type":"Novice","profile_pic":"assets/images/1109/profile/F579A6BC-FC6A-6DE8-A215-59C8CD360DE1.jpg"},{"user_id":1021,"name":"Tanisha Yadav","user_role":"Model","exp_type":"Experienced","profile_pic":"assets/images/1021/profile/03990992-B231-278A-6961-9FB8A7864807.jpg"},{"user_id":1020,"name":"Shruti Jain","user_role":"Model","exp_type":"Professional","profile_pic":"assets/images/1020/profile/36CCF530-C321-6EA7-CA90-6E5AF5943635.jpg"},{"user_id":981,"name":"KARINA","user_role":"Model","exp_type":"Novice","profile_pic":"assets/images/981/profile/43B624E7-1619-3E0C-07E0-6A6F5992BEBA.jpg"},{"user_id":980,"name":"Mariane","user_role":"Model","exp_type":"Experienced","profile_pic":"assets/images/980/profile/C0D2B03E-100C-2C33-8439-98039830543A.jpg"},{"user_id":979,"name":"Olena","user_role":"Model","exp_type":"Novice","profile_pic":"assets/images/979/profile/16CB9AAF-BDCE-6378-7758-5468D0E3964E.jpg"},{"user_id":974,"name":"Swell","user_role":"Model","exp_type":"Novice","profile_pic":"assets/images/974/profile/9C2E4DCF-F00A-F93B-C537-E6D713C2656C.jpg"},{"user_id":958,"name":"Vidushi Kaul","user_role":"Model","exp_type":"Professional","profile_pic":"assets/images/958/profile/46A87497-5E07-6577-A388-CCD960CD8FA9.jpg"},{"user_id":106,"name":"Ridham Sethi","user_role":"Model","exp_type":"Novice","profile_pic":"assets/images/106/profile/DE87C6FD-0C15-851F-BBFC-8FC1DBD3299A.jpg"},{"user_id":78,"name":"Ana","user_role":"Model","exp_type":"Novice","profile_pic":"assets/images/78/profile/C5A15BC8-BBCC-0270-D90B-EE4C2FAD71ED.jpg"}]
     */

    var status: Boolean = false
    var message: String? = null
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        /**
         * user_id : 2913
         * name : Karina Murashevska
         * user_role : Model
         * exp_type : Experienced
         * profile_pic : assets/images/2913/profile/45BFB1C3-26A0-A164-D83E-709130B9E72E.jpg
         */

        var user_id: Int = 0
        var is_featured: String? = null
        var toprated: String? = null
        var is_trending: String? = null
        var name: String? = null
        var user_role: String? = null
        var secondary_role: String? = null
        var exp_type: String? = null
        var profile_pic: String? = null
        var is_casting: String? = null
    }
}
