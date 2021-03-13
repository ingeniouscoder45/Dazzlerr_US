package com.dazzlerr_usa.views.activities.addjob

interface AddOrEditJobPresenter
{
    fun addJob(user_id:String , employer_id:String , title:String ,description:String,start_date:String,end_date:String,budget:String
                ,address:String,country_id : String,city:String,city_id:String,state_id:String
                ,job_role_id:String,gender:String,age_range:String,audition:String,vacancies:String,job_expiry:String,tags:String,budget_duration:String,contact_email:String
                ,contact_mobile:String,contact_whatsapp:String,contact_person:String)

    fun isValid(title:String ,description:String,start_date:String,end_date:String,budget:String
                ,address:String,country_id:String,city_id:String,state_id:String
                ,job_role_id:String,gender:String,min_age_range:String,max_age_range:String,tags:String,budget_duration:String,contact_email:String
                ,contact_mobile:String,contact_whatsapp:String,contact_person:String,vacancies:String
    )

    fun updateJob(user_id:String , call_id:String , title:String ,description:String,start_date:String,end_date:String,budget:String
               ,address:String,country_id : String,city:String,city_id:String,state_id:String
               ,job_role_id:String,gender:String,age_range:String,audition:String,vacancies:String,job_expiry:String,tags:String,budget_duration:String,contact_email:String
               ,contact_mobile:String,contact_whatsapp:String,contact_person:String)


    fun getCountries()
    fun getStates(country_id :String)
    fun getCities(state_id :String)

    fun cancelRetrofitRequest()
}