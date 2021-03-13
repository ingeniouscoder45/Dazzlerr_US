package com.dazzlerr_usa.views.activities.portfolio.addproject

interface ProjectPresenter
{

    fun addProject(user_id: String ,project_title: String , project_role:String, project_start_date:String ,project_end_date:String , project_description:String)
    fun updateProject(user_id: String ,project_id: String ,project_title: String , project_role:String, project_start_date:String ,project_end_date:String , project_description:String)
    fun validate(project_title: String , project_role:String, project_start_date:String ,project_end_date:String , project_description:String)
    fun cancelRetrofitRequest()
}