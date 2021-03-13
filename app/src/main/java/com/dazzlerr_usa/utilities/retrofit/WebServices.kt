package com.dazzlerr_usa.utilities.retrofit

import com.dazzlerr_usa.utilities.imageslider.ImageSliderPojo
import com.dazzlerr_usa.views.activities.addjob.AddOrEditJobPojo
import com.dazzlerr_usa.views.activities.becomefeatured.BecomeFeaturedPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.AllCategoryBlogsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogCategoriesPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogDetailsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogListPojo
import com.dazzlerr_usa.views.activities.calllogs.CallLogsPojo
import com.dazzlerr_usa.views.activities.featuredccavenue.CheckoutPojo
import com.dazzlerr_usa.views.activities.changepassword.ChangePasswordPojo
import com.dazzlerr_usa.views.activities.contact.ContactPojo
import com.dazzlerr_usa.views.activities.contactus.PojoContactUs
import com.dazzlerr_usa.views.activities.editcastingprofile.EditCastingProfilePojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.*
import com.dazzlerr_usa.views.activities.elitecontact.EliteContactPojo
import com.dazzlerr_usa.views.activities.elitememberdetails.EliteMemberDetailsPojo
import com.dazzlerr_usa.views.activities.elitememberdetails.LikeOrDislikePojo
import com.dazzlerr_usa.views.activities.elitemembers.EliteMembersPojo
import com.dazzlerr_usa.views.activities.eventcontact.EventContactPojo
import com.dazzlerr_usa.views.activities.eventdetails.EventDetailsPojo
import com.dazzlerr_usa.views.activities.influencerdetails.InfluencerDetailsPojo
import com.dazzlerr_usa.views.activities.influencers.InfluencersPojo
import com.dazzlerr_usa.views.activities.jobsdetails.*
import com.dazzlerr_usa.views.activities.mainactivity.MonetizeApiPojo
import com.dazzlerr_usa.views.activities.portfolio.addproject.ProjectPojo
import com.dazzlerr_usa.views.activities.portfolio.addvideo.VideoPojo
import com.dazzlerr_usa.views.activities.home.ClearDeviceIdPojo
import com.dazzlerr_usa.views.activities.interestedtalents.InterestedTalentsPojo
import com.dazzlerr_usa.views.activities.profileteam.pojos.AddOrUpdatePojo
import com.dazzlerr_usa.views.activities.profileteam.pojos.GetTeamPojo
import com.dazzlerr_usa.views.activities.report.ReportPojo
import com.dazzlerr_usa.views.activities.settings.GetProfileSettingsPojo
import com.dazzlerr_usa.views.activities.settings.PublishOrUnpublishProfilePojo
import com.dazzlerr_usa.views.activities.settings.SetSecurityQuestionPojo
import com.dazzlerr_usa.views.activities.settings.UpdateUsernamePojo
import com.dazzlerr_usa.views.fragments.castingdashboard.CastingDashboardPojo
import com.dazzlerr_usa.views.fragments.profileemailphoneverification.CastingProofPojo
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingProfilePojo
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.UpdateJobPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ValidateOtpPojo
import com.dazzlerr_usa.views.fragments.home.pojos.HomeCategoryPojo
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo
import com.dazzlerr_usa.views.fragments.jobs.AppliedJobsPojo
import com.dazzlerr_usa.views.fragments.jobs.JobsPojo
import com.dazzlerr_usa.views.fragments.login.LoginPojo
import com.dazzlerr_usa.views.fragments.messages.MessageDeleteAndRestorePojo
import com.dazzlerr_usa.views.fragments.messages.MessagesListPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.*
import com.dazzlerr_usa.views.fragments.profile.pojo.FollowOrUnfollowPojo
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.dazzlerr_usa.views.fragments.profile.pojo.UploadProfilePicPojo
import com.dazzlerr_usa.views.activities.events.pojos.*
import com.dazzlerr_usa.views.activities.institute.InstitutePojo
import com.dazzlerr_usa.views.activities.institutedetails.InstituteDetailsPojo
import com.dazzlerr_usa.views.activities.jobfilter.JobFilterPojo
import com.dazzlerr_usa.views.activities.membership.FreeMembershipPojo
import com.dazzlerr_usa.views.activities.membership.GetTokenPojo
import com.dazzlerr_usa.views.activities.talentfollowers.TalentFollowersPojo
import com.dazzlerr_usa.views.activities.transactions.TransactionsPojo
import com.dazzlerr_usa.views.activities.membership.MembershipPojo
import com.dazzlerr_usa.views.activities.membership.MembershipSuccessPojo
import com.dazzlerr_usa.views.activities.messagewindow.*
import com.dazzlerr_usa.views.activities.mymembership.MyMembershipPojo
import com.dazzlerr_usa.views.activities.notifications.NotificationsPojo
import com.dazzlerr_usa.views.activities.setprimarydevice.SetPrimaryDevicePojo
import com.dazzlerr_usa.views.activities.synchcalling.AddCallLogPojo
import com.dazzlerr_usa.views.fragments.profile.pojo.GetContactDetailsPojo
import com.dazzlerr_usa.views.fragments.register.RegisterPojo
import com.dazzlerr_usa.views.fragments.talentdashboard.ActivitySummaryPojo
import com.dazzlerr_usa.views.fragments.talentdashboard.TalentDashboardPojo
import retrofit2.Call
import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.http.POST
import retrofit2.http.Multipart
import okhttp3.MultipartBody
import okhttp3.ResponseBody


interface WebServices
{

/*
    @FormUrlEncoded
    @POST("user/signin")
    fun loginApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("email") user: String, @Field("password") password: String, @Field("device_id") device_id: String): Call<LoginPojo>
*/

    @FormUrlEncoded
    @POST("user/v2_signin_abroad")
    fun loginApi(@Header("Content-Type") header: String
                 , @Field("auth_key") auth_key: String
                 , @Field("email") user: String
                 , @Field("password") password: String
                 , @Field("device_id") device_id: String
                 , @Field("device_brand") device_brand: String
                 , @Field("device_model") device_model: String
                 , @Field("device_type") device_type: String
                 , @Field("operating_system") operating_system: String
                 , @Field("app_version") app_version: String
    ): Call<LoginPojo>

    @FormUrlEncoded
    @POST("register/v4_mob_talent_register_abroad")
    fun registerApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String
                    , @Field("user_role_id") user_role_id: String
                    ,  @Field("email") email: String
                    , @Field("password") password: String
                    ,  @Field("phone") phone: String
                    , @Field("name") name: String
                    , @Field("userrole") user_role: String
                    , @Field("device_id") device_id: String
                    , @Field("device_brand") device_brand: String
                    , @Field("device_model") device_model: String
                    , @Field("device_type") device_type: String
                    , @Field("operating_system") operating_system: String
                    , @Field("app_version") app_version: String): Call<RegisterPojo>

    @FormUrlEncoded
    @POST("home/v2_home_filtered_data")
    fun getModelsApi(@Header("Content-Type") header: String
                     ,@Header("token") token: String
                     , @Field("auth_key") auth_key: String
                     , @Field("user_id") user_id: String
                     , @Field("city") city: String
                     , @Field("type") type: String
                     , @Field("page") page: String
                     , @Field("device_id") device_id: String
                     , @Field("device_brand") device_brand: String
                     , @Field("device_model") device_model: String
                     , @Field("device_type") device_type: String
                     , @Field("operating_system") operating_system: String): Call<ModelsPojo>

    @GET("home/category_stat")
    fun getCategoryApi(): Call<HomeCategoryPojo>

    @FormUrlEncoded
    @POST("talent/v3_talent_mob_filtered_data")
    fun getTalentsApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String
                      ,@Field("city") city: String
                      ,@Field("page") page: String
                      ,@Field("uname") model_name: String
                      ,@Field("gender") gender:String
                      ,@Field("category") category_id : String
                      ,@Field("exp_type") experience_type:String
                      ,@Field("user_id") user_id:String
                      ,@Field("age_range") age_range:String
                      ,@Field("height_range") height_range:String
                      ,@Header("token") token: String
                      , @Field("device_id") device_id: String
                      , @Field("device_brand") device_brand: String
                      , @Field("device_model") device_model: String
                      , @Field("device_type") device_type: String
                      , @Field("operating_system") operating_system: String): Call<ModelsPojo>

    @FormUrlEncoded
    @POST("profilemob/v4_profiledata")
    fun getProfileApi(@Header("Content-Type") header: String
                      ,  @Field("auth_key") auth_key: String
                      ,@Field("profile_id") profile_id: String
                      , @Field("user_id") user_id: String
                      ,@Header("token") token: String
                      , @Field("device_id") device_id: String
                      , @Field("device_brand") device_brand: String
                      , @Field("device_model") device_model: String
                      , @Field("device_type") device_type: String
                      , @Field("operating_system") operating_system: String): Call<ProfilePojo>

    @Multipart
    @POST("user/upload_profile_pic")
    fun editProfilePicApi(@Part image: MultipartBody.Part
                          , @Part("auth_key") auth_key: RequestBody
                          , @Part("user_id") user_id: RequestBody
                          , @Part("device_id") device_id: RequestBody
                          , @Part("device_brand") device_brand: RequestBody
                          , @Part("device_model") device_model: RequestBody
                          , @Part("device_type") device_type: RequestBody
                          , @Part("operating_system") operating_system: RequestBody): Call<UploadProfilePicPojo>

    @Multipart
    @POST("user/upload_casting_profile_pic")
    fun editCastingProfilePicApi(@Part image: MultipartBody.Part
                                 , @Part("auth_key") auth_key: RequestBody
                                 , @Part("user_id") user_id: RequestBody
                                 , @Part("device_id") device_id: RequestBody
                                 , @Part("device_brand") device_brand: RequestBody
                                 , @Part("device_model") device_model: RequestBody
                                 , @Part("device_type") device_type: RequestBody
                                 , @Part("operating_system") operating_system: RequestBody): Call<UploadProfilePicPojo>

    @Multipart
    @POST("user/upload_portfolio")
    fun UploadPortfolioImageApi(@Part image: MultipartBody.Part, @Part("auth_key") auth_key: RequestBody
                                , @Part("user_id") user_id: RequestBody
                                , @Part("device_id") device_id: RequestBody
                                , @Part("device_brand") device_brand: RequestBody
                                , @Part("device_model") device_model: RequestBody
                                , @Part("device_type") device_type: RequestBody
                                , @Part("operating_system") operating_system: RequestBody): Call<UploadImagePojo>

    @FormUrlEncoded
    @POST("profilemob/v3_getportfolio")
    fun getPortfolioImages(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("user_id") user_id: String
                           , @Field("profile_id") profile_id: String
                           , @Field("explore_portfolio") explore_portfolio: String
                           , @Field("page") page: String
                           ,@Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system") operating_system: String): Call<PortfolioImagesPojo>

    @FormUrlEncoded
    @POST("profilemob/getvideos")
    fun getPortfolioVideos(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("user_id") user_id: String
                           , @Field("page") page: String
                           ,@Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system") operating_system: String): Call<PortfolioVideosPojo>

    @FormUrlEncoded
    @POST("user/add_user_video")
    fun addPortfolioVideo(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String
                          , @Field("user_id") user_id: String, @Field("video_title") video_title: String
                          , @Field("video_description") video_description: String
                          , @Field("video_url") video_url: String
                          ,@Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system") operating_system: String): Call<VideoPojo>

    @FormUrlEncoded
    @POST("profilemob/getprojects")
    fun getPortfolioProjects(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                             , @Field("user_id") user_id: String
                             , @Field("page") page: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system") operating_system: String): Call<PortfolioProjectsPojo>

    @FormUrlEncoded
    @POST("user/delete_user_project")
    fun deletePortfolioProject(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String
                               , @Field("project_id") project_id: String
                               ,@Header("token") token: String
                               , @Field("device_id") device_id: String
                               , @Field("device_brand") device_brand: String
                               , @Field("device_model") device_model: String
                               , @Field("device_type") device_type: String
                               , @Field("operating_system") operating_system: String): Call<DeletePojo>

    @FormUrlEncoded
    @POST("user/delete_user_video")
    fun deletePortfolioVideo(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("video_id") video_id: String
                             ,@Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system") operating_system: String): Call<DeletePojo>

    @FormUrlEncoded
    @POST("user/delete_user_portofolio")
    fun deletePortfolioImage(@Header("Content-Type") header: String,  @Field("auth_key") auth_key: String,@Field("user_id") user_id: String, @Field("portfolio_id") portfolio_id: String
                             ,@Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system") operating_system: String): Call<DeletePojo>

    @FormUrlEncoded
    @POST("user/delete_user_document")
    fun deletePortfolioDocument(@Header("Content-Type") header: String
                                , @Field("auth_key") auth_key: String
                                , @Field("user_id") user_id: String
                                , @Field("document_id") document_id: String
                                ,@Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system") operating_system: String): Call<DeletePojo>

    @FormUrlEncoded
    @POST("profilemob/getdocuments")
    fun getPortfolioDocuments(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("page") page: String
                              ,@Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system") operating_system: String): Call<PortfolioAudiosPojo>

    @Multipart
    @POST("user/upload_document")
    fun UploadPortfolioDocApi(@Part image: MultipartBody.Part, @Part("auth_key") auth_key: RequestBody
                              , @Part("user_id") user_id: RequestBody
                              , @Part("device_id") device_id: RequestBody
                              , @Part("device_brand") device_brand: RequestBody
                              , @Part("device_model") device_model: RequestBody
                              , @Part("device_type") device_type: RequestBody
                              , @Part("operating_system") operating_system: RequestBody): Call<UploadAudioPojo>

    @FormUrlEncoded
    @POST("job/v3_jobs_filtered_data")
    fun getJobsApi(@Header("Content-Type") header: String
                   ,@Field("auth_key") auth_key: String
                   ,@Field("user_id") user_id: String
                   ,@Field("city") city: String
                   ,@Field("page") page: String
                   ,@Field("uname") job_name: String
                   ,@Field("gender") gender:String
                   ,@Field("category") category_id : String
                   ,@Field("exp_type") experience_type:String
                   ,@Field("type") type:String
                   ,@Header("token") token: String
                   ,@Field("device_id") device_id: String
                   ,@Field("device_brand") device_brand: String
                   ,@Field("device_model") device_model: String
                   ,@Field("device_type") device_type: String
                   ,@Field("operating_system") operating_system: String): Call<JobsPojo>

/*
    @FormUrlEncoded
    @POST("job/job_detail")
    fun getJobDetails(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("call_id") call_id: String, @Field("user_id") user_id: String): Call<JobDetailsPojo>
*/

    @FormUrlEncoded
    @POST("job/v3_job_detail")
    fun getJobDetails(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String
                      , @Field("call_id") call_id: String
                      , @Field("user_id") user_id: String
                      , @Field("membership_id") membership_id: String
                      ,@Header("token") token: String
                      , @Field("device_id") device_id: String
                      , @Field("device_brand") device_brand: String
                      , @Field("device_model") device_model: String
                      , @Field("device_type") device_type: String
                      , @Field("operating_system") operating_system: String): Call<JobDetailsPojo>

    @FormUrlEncoded
    @POST("job/v3_job_apply")
    fun sendMessage(@Header("Content-Type") header: String,  @Field("auth_key") auth_key: String,@Field("call_id") call_id: String, @Field("user_id") user_id: String
                    , @Field("msg") message: String
                    , @Field("name") name: String
                    , @Field("email") email: String
                    , @Field("title") title: String
                    , @Field("event_slug_name") user_name: String
                    , @Field("phone") phone: String
                    , @Header("token") token: String
                    , @Field("device_id") device_id: String
                    , @Field("device_brand") device_brand: String
                    , @Field("device_model") device_model: String
                    , @Field("device_type") device_type: String
                    , @Field("operating_system") operating_system: String): Call<AddMessagePojo>

    @FormUrlEncoded
    @POST("profile/v3_check_chat_list")
    fun getChatlist(@Header("Content-Type") header: String
                         , @Field("auth_key") auth_key: String
                         , @Field("user_id") user_id: String/*
                         , @Field("page") page: String*/
                         , @Header("token") token: String
                         , @Field("device_id") device_id: String
                         , @Field("device_brand") device_brand: String
                         , @Field("device_model") device_model: String
                         , @Field("device_type") device_type: String
                         , @Field("operating_system") operating_system: String): Call<MessagesListPojo>

    @FormUrlEncoded
    @POST("profile/get_trash_contact_messages")
    fun getTrashMessages(@Header("Content-Type") header: String
                         , @Field("auth_key") auth_key: String
                         , @Field("user_id") user_id: String
                         , @Field("page") page: String
                         , @Header("token") token: String
                         , @Field("device_id") device_id: String
                         , @Field("device_brand") device_brand: String
                         , @Field("device_model") device_model: String
                         , @Field("device_type") device_type: String
                         , @Field("operating_system") operating_system: String): Call<MessagesListPojo>

    @FormUrlEncoded
    @POST("profile/trash_messages")
    fun deleteMessages(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("thread_id") thread_id: String
                       , @Header("token") token: String
                       , @Field("device_id") device_id: String
                       , @Field("device_brand") device_brand: String
                       , @Field("device_model") device_model: String
                       , @Field("device_type") device_type: String
                       , @Field("operating_system") operating_system: String): Call<MessageDeleteAndRestorePojo>

    @FormUrlEncoded
    @POST("profile/restore_messages")
    fun restoreMessages(@Header("Content-Type") header: String
                        , @Field("auth_key") auth_key: String
                        , @Field("thread_id") thread_id: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system") operating_system: String): Call<MessageDeleteAndRestorePojo>

    @FormUrlEncoded
    @POST("profile/v3_view_contact_message")
    fun getAllMessages(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("user_id") user_id: String
                       , @Field("thread_id") thread_id: String
                       , @Header("token") token: String
                       , @Field("device_id") device_id: String
                       , @Field("device_brand") device_brand: String
                       , @Field("device_model") device_model: String
                       , @Field("device_type") device_type: String
                       , @Field("operating_system") operating_system: String): Call<MessageWindowPojo>

    @FormUrlEncoded
    @POST("profile/contact_message_reply")
    fun replyToMessage(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("thread_id") thread_id: String
                       , @Field("sender_id") sender_id: String
                       , @Field("name") name: String
                       , @Field("email") email: String
                       , @Field("phone") phone: String
                       , @Field("message") message: String
                       , @Header("token") token: String
                       , @Field("device_id") device_id: String
                       , @Field("device_brand") device_brand: String
                       , @Field("device_model") device_model: String
                       , @Field("device_type") device_type: String
                       , @Field("operating_system") operating_system: String): Call<ReplyMessagePojo>



    @FormUrlEncoded
    @POST("user/v4_update_general_info")
    fun updateGeneralInformation(@Header("Content-Type") header: String
                                 , @Field("auth_key") auth_key: String
                                 , @Field("user_id") user_id: String
                                 ,  @Field("email") email: String
                                 ,  @Field("user_role_id") user_role_id: String
                                 ,  @Field("name") name: String
                                 ,  @Field("phone") phone: String
                                 , @Field("whats_app") whats_app: String
                                 ,  @Field("country_id") country_id: String
                                 ,  @Field("state_id") state_id: String
                                 , @Field("city") city: String
                                 , @Field("exp_type") exp_type: String
                                 , @Field("languages") languages: String
                                 , @Field("dob") dob: String
                                 , @Field("marital_status") marital_status: String
                                 , @Field("agency_signed") agency_signed: String
                                 , @Field("available_for") available_for: String
                                 , @Field("gender") gender: String
                                 , @Field("pref_location") pref_location: String
                                 , @Field("about") about: String
                                 , @Field("interested_in") interested_in: String
                                 , @Field("skills") skills: String
                                 , @Field("agency_name") agency_name: String
                                 , @Field("is_public") is_public: String
                                 , @Field("parent_name") parent_name: String
                                 , @Field("parent_contact") parent_contact: String
                                 , @Field("agency_phone") agency_phone: String
                                 , @Field("agency_email") agency_email: String
                                 , @Field("username") username: String
                                 , @Header("token") token: String
                                 , @Field("device_id") device_id: String
                                 , @Field("device_brand") device_brand: String
                                 , @Field("device_model") device_model: String
                                 , @Field("device_type") device_type: String
                                 , @Field("operating_system") operating_system: String): Call<GeneralInformationPojo>

    @FormUrlEncoded
    @POST("user/update_rates_info")
    fun updateRatesAndTravel(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                             , @Field("user_id") user_id: String
                             , @Field("full_day") full_day: String
                             , @Field("half_day") half_day: String
                             , @Field("hourly") hourly: String
                             , @Field("test") test: String
                             , @Field("will_fly") will_fly: String
                             , @Field("passport_ready") passport_ready: String
                             , @Field("drive_miles") drive_miles: String
                             , @Field("payment_options") payment_options: String
                             , @Field("availability") availability: String
                             , @Field("facebook") facebook: String
                             , @Field("twitter") twitter: String
                             , @Field("insta") insta: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system") operating_system: String): Call<RatesPojo>

    @GET("common/get_states")
    fun getStates(@Header("Content-Type") header: String): Call<StatesPojo>

    @FormUrlEncoded
    @POST("common/get_city_by_state")
    fun getCities(@Header("Content-Type") header: String
                  , @Field("auth_key") auth_key: String
                  , @Field("state_id") state_id: String
                  , @Header("token") token: String
                  , @Field("device_id") device_id: String
                  , @Field("device_brand") device_brand: String
                  , @Field("device_model") device_model: String
                  , @Field("device_type") device_type: String
                  , @Field("operating_system") operating_system: String): Call<CitiesPojo>


    @FormUrlEncoded
    @POST("user/update_appearance_info")
    fun updateAppearance(@Header("Content-Type") header: String
                         , @Field("auth_key") auth_key: String
                         , @Field("user_id") user_id: String
                         , @Field("height") height: String
                         , @Field("weight") weight: String
                         , @Field("biceps") biceps: String
                         , @Field("chest") chest: String
                         , @Field("waist") waist: String
                         , @Field("hips") hips: String
                         , @Field("hair_color") hair_color: String
                         , @Field("hair_length") hair_length: String
                         , @Field("hair_type") hair_type: String
                         , @Field("eye_color") eye_color: String
                         , @Field("skintone") skintone: String
                         , @Field("dress") dress: String
                         , @Field("shoes") shoes: String
                         , @Field("tags") tags: String
                         , @Header("token") token: String
                         , @Field("device_id") device_id: String
                         , @Field("device_brand") device_brand: String
                         , @Field("device_model") device_model: String
                         , @Field("device_type") device_type: String
                         , @Field("operating_system") operating_system: String): Call<AppearancePojo>

    @FormUrlEncoded
    @POST("user/add_user_project")
    fun addProject(@Header("Content-Type") header: String
                   , @Field("auth_key") auth_key: String
                   , @Field("user_id") user_id: String
                   , @Field("project_title") project_title: String
                   , @Field("project_role") project_role: String
                   , @Field("project_start_date") project_start_date: String
                   , @Field("project_end_date") project_end_date: String
                   , @Field("project_description") project_description: String
                   , @Header("token") token: String
                   , @Field("device_id") device_id: String
                   , @Field("device_brand") device_brand: String
                   , @Field("device_model") device_model: String
                   , @Field("device_type") device_type: String
                   , @Field("operating_system") operating_system: String): Call<ProjectPojo>

    @FormUrlEncoded
    @POST("user/update_user_project")
    fun updateProject(@Header("Content-Type") header: String
                      , @Field("auth_key") auth_key: String
                      , @Field("user_id") user_id: String
                      , @Field("project_id") project_id: String
                      , @Field("project_title") project_title: String
                      , @Field("project_role") project_role: String
                      , @Field("project_start_date") project_start_date: String
                      , @Field("project_end_date") project_end_date: String
                      , @Field("project_description") project_description: String
                      , @Header("token") token: String
                      , @Field("device_id") device_id: String
                      , @Field("device_brand") device_brand: String
                      , @Field("device_model") device_model: String
                      , @Field("device_type") device_type: String
                      , @Field("operating_system") operating_system: String): Call<ProjectPojo>

    @FormUrlEncoded
    @POST("register/v2_forgotpassword")
    fun forgotPasswordByEmailOrPhone(@Header("Content-Type") header: String
                                     , @Field("auth_key") auth_key: String
                                     , @Field("email") email: String
                                     , @Field("phone") phone: String
                                     , @Field("email_type") email_type: String
                                     , @Header("token") token: String
                                     , @Field("device_id") device_id: String
                                     , @Field("device_brand") device_brand: String
                                     , @Field("device_model") device_model: String
                                     , @Field("device_type") device_type: String
                                     , @Field("operating_system") operating_system: String): Call<ForgotPasswordPojo> // This api is using for forgot password as well as Account verification

    @FormUrlEncoded
    @POST("register/forgotpassword_qa")
    fun forgotPasswordByQuestion(@Header("Content-Type") header: String  , @Field("auth_key") auth_key: String, @Field("email") email: String
                                 , @Header("token") token: String
                                 , @Field("device_id") device_id: String
                                 , @Field("device_brand") device_brand: String
                                 , @Field("device_model") device_model: String
                                 , @Field("device_type") device_type: String
                                 , @Field("operating_system") operating_system: String): Call<ForgotPasswordByQuestionPojo>

    @FormUrlEncoded
    @POST("register/verify_forgotpassword_phone_otp")
    fun validateOtpByPhone(@Header("Content-Type") header: String  , @Field("auth_key") auth_key: String, @Field("phone") phone: String , @Field("otp") otp: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system") operating_system: String): Call<ValidateOtpPojo>

    @FormUrlEncoded
    @POST("register/verify_forgotpassword_otp")
    fun validateOtpByEmail(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String , @Field("email") email: String, @Field("otp") otp: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system") operating_system: String): Call<ValidateOtpPojo>

    @FormUrlEncoded
    @POST("register/v2_verify_forgotpassword_otp")
    fun validateForgotPasswordOtp(@Header("Content-Type") header: String
                                  , @Field("auth_key") auth_key: String
                                  , @Field("email") email: String
                                  , @Field("otp") otp: String
                                  , @Header("token") token: String
                                  , @Field("device_id") device_id: String
                                  , @Field("device_brand") device_brand: String
                                  , @Field("device_model") device_model: String
                                  , @Field("device_type") device_type: String
                                  , @Field("operating_system") operating_system: String): Call<ValidateOtpPojo>

    @FormUrlEncoded
    @POST("register/verify_forgotpassword_qa")
    fun validateQuestion(@Header("Content-Type") header: String
                         , @Field("auth_key") auth_key: String
                         , @Field("email") email: String
                         , @Field("question_id") phone: String
                         , @Field("answer") otp: String
                         , @Header("token") token: String
                         , @Field("device_id") device_id: String
                         , @Field("device_brand") device_brand: String
                         , @Field("device_model") device_model: String
                         , @Field("device_type") device_type: String
                         , @Field("operating_system") operating_system: String): Call<ValidateOtpPojo>

    @FormUrlEncoded
    @POST("register/changepassword_by_otp")
    fun resetPassword(@Header("Content-Type") header: String
                      , @Field("auth_key") auth_key: String
                      , @Field("user_id") email: String
                      , @Field("password") phone: String
                      , @Field("device_id") device_id: String
                      , @Field("device_brand") device_brand: String
                      , @Field("device_model") device_model: String
                      , @Field("device_type") device_type: String
                      , @Field("operating_system") operating_system: String): Call<ForgotPasswordPojo>

    @FormUrlEncoded
    @POST("register/verify_user_email_otp")
    fun validateOtpbyEmail(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("email") email: String
                           , @Field("otp") otp: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system") operating_system: String): Call<ValidateOtpPojo>

    @FormUrlEncoded
   // @POST("register/verify_user_phone_otp")
    @POST("register/v4_verify_user_phone_otp")
    fun validateOtpbyPhone(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("phone") email: String
                           , @Field("otp") otp: String
                           , @Field("type") type: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system") operating_system: String): Call<ValidateOtpPojo>

    @FormUrlEncoded
    @POST("elite/elite_list")
    fun getEliteMembersApi(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("page") page: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system")operating_system: String): Call<EliteMembersPojo>

    @FormUrlEncoded
    @POST("elite/elite_profile_mobile")
    fun getEliteMemberDetailsApi(@Header("Content-Type") header: String
                                 , @Field("auth_key") auth_key: String
                                 , @Field("user_id") user_id: String
                                 , @Field("profile_id") profile_id: String
                                 , @Header("token") token: String
                                 , @Field("device_id") device_id: String
                                 , @Field("device_brand") device_brand: String
                                 , @Field("device_model") device_model: String
                                 , @Field("device_type") device_type: String
                                 , @Field("operating_system")operating_system: String): Call<EliteMemberDetailsPojo>

    @FormUrlEncoded
    @POST("elite/contact_request")
    fun contact(@Header("Content-Type") header: String
                , @Field("auth_key") auth_key: String
                , @Field("name") name: String
                , @Field("phone") phone: String
                , @Field("email") email: String
                , @Field("msg") msg: String
                , @Field("type") type: String
                , @Field("profile_name") profile_name: String
                , @Field("username") username: String
                , @Header("token") token: String
                , @Field("device_id") device_id: String
                , @Field("device_brand") device_brand: String
                , @Field("device_model") device_model: String
                , @Field("device_type") device_type: String
                , @Field("operating_system")operating_system: String): Call<EliteContactPojo>

/*
    @FormUrlEncoded
    @POST("profile/profile_contact")
    fun profileContact(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("profile_id") profile_id: String
                       , @Field("user_id") user_id: String
                       , @Field("name") name: String
                       , @Field("email") email: String
                       , @Field("phone") phone: String
                       , @Field("subject") subject: String
                       , @Field("message") msg: String): Call<ContactPojo>
*/

    @FormUrlEncoded
    @POST("profile/v2_profile_contact")
    fun profileContact(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("membership_id") membership_id: String
                       , @Field("profile_id") profile_id: String
                       , @Field("user_id") user_id: String
                       , @Field("name") name: String
                       , @Field("email") email: String
                       , @Field("phone") phone: String
                       , @Field("subject") subject: String
                       , @Field("message") msg: String
                       , @Header("token") token: String
                       , @Field("device_id") device_id: String
                       , @Field("device_brand") device_brand: String
                       , @Field("device_model") device_model: String
                       , @Field("device_type") device_type: String
                       , @Field("operating_system")operating_system: String): Call<ContactPojo>

    @FormUrlEncoded
    @POST("elite/event_like")
    fun likeOrDislikeEventApi(@Header("Content-Type") header: String
                              , @Field("auth_key") auth_key: String
                              , @Field("user_id") user_id: String
                              , @Field("event_id") event_id: String
                              , @Field("status") status: String
                              , @Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system")operating_system: String): Call<LikeOrDislikePojo>


    @FormUrlEncoded
    @POST("elite/v3_profile_like")
    fun likeOrDislikeEliteApi(@Header("Content-Type") header: String
                              , @Field("auth_key") auth_key: String
                              , @Field("user_id") user_id: String
                              , @Field("profile_id") profile_id: String
                              , @Field("status") status: String
                              , @Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system")operating_system: String): Call<LikeOrDislikePojo>

    @FormUrlEncoded
    @POST("elite/v3_profile_follower")
    fun followOrUnfollowApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("profile_id") profile_id: String, @Field("status") status: String
                            , @Header("token") token: String
                            , @Field("device_id") device_id: String
                            , @Field("device_brand") device_brand: String
                            , @Field("device_model") device_model: String
                            , @Field("device_type") device_type: String
                            , @Field("operating_system")operating_system: String): Call<FollowOrUnfollowPojo>


    @FormUrlEncoded
    @POST("elite/v3_profile_shortlist")
    fun shortListApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("profile_id") profile_id: String, @Field("status") status: String
                     , @Header("token") token: String
                     , @Field("device_id") device_id: String
                     , @Field("device_brand") device_brand: String
                     , @Field("device_model") device_model: String
                     , @Field("device_type") device_type: String
                     , @Field("operating_system")operating_system: String): Call<FollowOrUnfollowPojo>


    @FormUrlEncoded
    @POST("elite/influencer_list")
    fun getInfluencersApi(@Header("Content-Type") header: String
                          , @Field("auth_key") auth_key: String
                          , @Field("page") page: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<InfluencersPojo>

    @FormUrlEncoded
    @POST("elite/influencer_profile_mobile")
    fun getInfluencerDetailsApi(@Header("Content-Type") header: String
                                , @Field("auth_key") auth_key: String
                                , @Field("user_id") user_id: String
                                , @Field("profile_id") profile_id: String
                                , @Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system")operating_system: String): Call<InfluencerDetailsPojo>

    @FormUrlEncoded
    @POST("event/event_list")
    fun getEventsApi(@Header("Content-Type") header: String
                     , @Field("auth_key") auth_key: String
                     , @Field("uname") uname: String
                     , @Field("cat_id") cat_id: String
                     , @Field("city_name") city_name: String
                     , @Field("venue_name") venue_name: String
                     , @Field("organizer_name") organizer_name: String
                     , @Field("page") page: String
                     , @Field("month") month: String
                     , @Field("year") year: String
                     , @Header("token") token: String
                     , @Field("device_id") device_id: String
                     , @Field("device_brand") device_brand: String
                     , @Field("device_model") device_model: String
                     , @Field("device_type") device_type: String
                     , @Field("operating_system")operating_system: String): Call<EventsPojo>

    @FormUrlEncoded
    @POST("event/event_detail")
    fun getEventDetailsApi(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("user_id") user_id: String
                           , @Field("event_id") event_id: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system")operating_system: String): Call<EventDetailsPojo>

    @FormUrlEncoded
    @POST("register/v4_mob_casting_register")
    fun registerCastingsApi(@Header("Content-Type") header: String
                            , @Field("auth_key") auth_key: String
                            , @Field("name") name: String
                            , @Field("email") email: String
                            , @Field("phone") phone: String
                            , @Field("user_role_id") user_role_id: String
                            , @Field("password") password: String
                            , @Field("company_name") company_name: String
                            , @Field("representer") representer: String
                            , @Field("country_id") country_id: String
                            , @Field("state_id") state_id: String
                            , @Field("city") city: String
                            , @Field("city_id") city_id: String
                            , @Field("whats_app") whats_app: String
                            , @Field("zipcode") zipcode: String
                            , @Field("about") about: String
                            , @Field("user_role") user_role: String
                            , @Field("device_id") device_id: String
                            , @Field("device_brand") device_brand: String
                            , @Field("device_model") device_model: String
                            , @Field("device_type") device_type: String
                            , @Field("operating_system") operating_system: String
                            , @Field("app_version") app_version: String): Call<RegisterPojo>

    @Multipart
    @POST("user/casting_proof")
    fun uploadCastingProof(@Part image: MultipartBody.Part
                           , @Part("auth_key") auth_key: RequestBody
                           , @Part("user_id") user_id: RequestBody
                           , @Part("type") type: RequestBody
                           , @Part("device_id") device_id: RequestBody
                           , @Part("device_brand") device_brand: RequestBody
                           , @Part("device_model") device_model: RequestBody
                           , @Part("device_type") device_type: RequestBody
                           , @Part("operating_system") operating_system: RequestBody): Call<CastingProofPojo>

    @FormUrlEncoded
    @POST("casting/v4_mob_casting_profile")
    fun getCastingProfileApi(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                             , @Field("profile_id") profile_id: String
                             , @Field("user_id") user_id: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system")operating_system: String): Call<CastingProfilePojo>

    @FormUrlEncoded
    @POST("casting/my_job_status")
    fun updateJobStatus(@Header("Content-Type") header: String
                        , @Field("auth_key") auth_key: String
                        , @Field("user_id") user_id: String
                        , @Field("call_id") page: String
                        , @Field("status") status: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system")operating_system: String): Call<UpdateJobPojo>

    @FormUrlEncoded
    @POST("casting/my_jobs_for_mobile")
    fun getMyJobs(@Header("Content-Type") header: String
                  , @Field("auth_key") auth_key: String
                  , @Field("user_id") user_id: String
                  , @Field("page") page: String
                  , @Field("status") status: String
                  , @Header("token") token: String
                  , @Field("device_id") device_id: String
                  , @Field("device_brand") device_brand: String
                  , @Field("device_model") device_model: String
                  , @Field("device_type") device_type: String
                  , @Field("operating_system")operating_system: String): Call<CastingMyJobPojo>


    @FormUrlEncoded
    @POST("job/v3_add_job")
    fun addJob(@Header("Content-Type") header: String
               , @Field("auth_key") auth_key: String
               , @Field("user_id") user_id: String
               , @Field("employer_id") employer_id: String
               , @Field("title") title: String
               , @Field("description") description: String
               , @Field("start_date") start_date: String
               , @Field("end_date") end_date: String
               , @Field("budget") budget: String
               , @Field("address") address: String
               , @Field("country_id") country_id: String
               , @Field("city") city: String
               , @Field("city_id") city_id: String
               , @Field("state_id") state_id: String
               , @Field("job_role_id") job_role_id: String
               , @Field("gender") gender: String
               , @Field("age_range") age_range: String
               , @Field("audition") audition: String
               , @Field("vacancies") vacancies: String
               , @Field("job_expiry") job_expiry: String
               , @Field("tags") tags: String
               , @Field("budget_duration") budget_duration: String
               , @Field("contact_email") contact_email: String
               , @Field("contact_mobile") contact_mobile: String
               , @Field("contact_whatsapp") contact_whatsapp: String
               , @Field("contact_person") contact_person: String
               , @Header("token") token: String
               , @Field("device_id") device_id: String
               , @Field("device_brand") device_brand: String
               , @Field("device_model") device_model: String
               , @Field("device_type") device_type: String
               , @Field("operating_system")operating_system: String): Call<AddOrEditJobPojo>

    @FormUrlEncoded
    @POST("job/v2_edit_job")
    fun updateJob(@Header("Content-Type") header: String
                  , @Field("auth_key") auth_key: String
                  , @Field("user_id") user_id: String
                  , @Field("call_id") call_id: String
                  , @Field("title") title: String
                  , @Field("description") description: String
                  , @Field("start_date") start_date: String
                  , @Field("end_date") end_date: String
                  , @Field("budget") budget: String
                  , @Field("address") address: String
                  , @Field("country_id") country_id: String
                  , @Field("city") city: String
                  , @Field("city_id") city_id: String
                  , @Field("state_id") state_id: String
                  , @Field("job_role_id") job_role_id: String
                  , @Field("gender") gender: String
                  , @Field("age_range") age_range: String
                  , @Field("audition") audition: String
                  , @Field("vacancies") vacancies: String
                  , @Field("job_expiry") job_expiry: String
                  , @Field("tags") tags: String
                  , @Field("budget_duration") budget_duration: String
                  , @Field("contact_email") contact_email: String
                  , @Field("contact_mobile") contact_mobile: String
                  , @Field("contact_whatsapp") contact_whatsapp: String
                  , @Field("contact_person") contact_person: String
                  , @Header("token") token: String
                  , @Field("device_id") device_id: String
                  , @Field("device_brand") device_brand: String
                  , @Field("device_model") device_model: String
                  , @Field("device_type") device_type: String
                  , @Field("operating_system")operating_system: String
    ): Call<AddOrEditJobPojo>


    @FormUrlEncoded
    @POST("user/v4_update_casting_general_info")
    fun updateCastingProfile(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                             , @Field("name") name: String
                             , @Field("company_name") company_name: String
                             , @Field("representer") representer: String
                             , @Field("about") about: String
                             , @Field("country_id") country_id: String
                             , @Field("state_id") state_id: String
                             , @Field("zipcode") zipcode: String
                             , @Field("phone") phone: String
                             , @Field("whats_app") whats_app: String
                             , @Field("website") website: String
                             , @Field("facebook") facebook: String
                             , @Field("twitter") twitter: String
                             , @Field("instagram") instagram: String
                             , @Field("city") city: String
                             , @Field("city_id") city_id: String
                             , @Field("user_id") user_id: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system")operating_system: String): Call<EditCastingProfilePojo>

    @FormUrlEncoded
    @POST("casting/casting_view_job")
    fun getJobProposals(@Header("Content-Type") header: String
                        , @Field("auth_key") auth_key: String
                        , @Field("call_id") call_id: String
                        , @Field("user_id") user_id: String
                        , @Field("type") type: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system")operating_system: String): Call<GetProposalsPojo>

    @FormUrlEncoded
    @POST("job/job_contact_log")
    fun getJobContact(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("call_id") call_id: String, @Field("user_id") user_id: String
                      , @Header("token") token: String
                      , @Field("device_id") device_id: String
                      , @Field("device_brand") device_brand: String
                      , @Field("device_model") device_model: String
                      , @Field("device_type") device_type: String
                      , @Field("operating_system")operating_system: String): Call<GetJobContactPojo>

    @FormUrlEncoded
    @POST("casting/job_shortlist_user")
    fun castingShortlistApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("creply_id") creply_id: String, @Field("status") status: String
                            , @Header("token") token: String
                            , @Field("device_id") device_id: String
                            , @Field("device_brand") device_brand: String
                            , @Field("device_model") device_model: String
                            , @Field("device_type") device_type: String
                            , @Field("operating_system")operating_system: String): Call<ShortlistPojo>

    @FormUrlEncoded
    @POST("casting/v3_casting_job_reply")
    fun hireOrRejectApi(@Header("Content-Type") header: String
                        , @Field("auth_key") auth_key: String
                        , @Field("request_sent") request_sent: String
                        , @Field("request_message") request_message: String
                        , @Field("creply_id") creply_id: String
                        , @Field("user_id") user_id: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system")operating_system: String): Call<HireOrRejectPojo>

    @FormUrlEncoded
    @POST("elite/v3_profile_like")
    fun CastinglikeOrDislikeApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("profile_id") profile_id: String, @Field("status") status: String
                                , @Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system")operating_system: String): Call<LikeOrDislikePojo>

    @FormUrlEncoded
    @POST("elite/v3_profile_follower")
    fun CastingFollowOrUnfollowApi(@Header("Content-Type") header: String
                                   , @Field("auth_key") auth_key: String
                                   , @Field("user_id") user_id: String
                                   , @Field("profile_id") profile_id: String
                                   , @Field("status") status: String
                                   , @Header("token") token: String
                                   , @Field("device_id") device_id: String
                                   , @Field("device_brand") device_brand: String
                                   , @Field("device_model") device_model: String
                                   , @Field("device_type") device_type: String
                                   , @Field("operating_system")operating_system: String): Call<FollowOrUnfollowPojo>
    @FormUrlEncoded
    @POST("common/get_monitize_settings")
    fun getAppConstants(
            @Header("Content-Type") header: String
            , @Field("auth_key") auth_key: String
            , @Field("user_id") user_id: String
            , @Header("token") token: String
            , @Field("device_id") device_id: String
            , @Field("device_brand") device_brand: String
            , @Field("device_model") device_model: String
            , @Field("device_type") device_type: String
            , @Field("operating_system")operating_system: String): Call<MonetizeApiPojo>

    @FormUrlEncoded
    @POST("user/app_logout")
    fun clearDeviceid(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String
                      , @Header("token") token: String
                      , @Field("device_id") device_id: String
                      , @Field("device_brand") device_brand: String
                      , @Field("device_model") device_model: String
                      , @Field("device_type") device_type: String
                      , @Field("operating_system")operating_system: String): Call<ClearDeviceIdPojo>

    @FormUrlEncoded
    @POST("common/contact_us")
    fun contactUsApi(@Header("Content-Type") header: String
                     , @Field("auth_key") auth_key: String
                     , @Field("name") name: String
                     , @Field("email") email: String
                     , @Field("phone") phone: String
                     , @Field("subject") subject: String
                     , @Field("message") msg: String
                     , @Header("token") token: String
                     , @Field("device_id") device_id: String
                     , @Field("device_brand") device_brand: String
                     , @Field("device_model") device_model: String
                     , @Field("device_type") device_type: String
                     , @Field("operating_system")operating_system: String): Call<PojoContactUs>

    @FormUrlEncoded
    @POST("talent/v2_casting_shortlisted")
    fun getShortlistedTalentsApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("page") page: String
                                 , @Header("token") token: String
                                 , @Field("device_id") device_id: String
                                 , @Field("device_brand") device_brand: String
                                 , @Field("device_model") device_model: String
                                 , @Field("device_type") device_type: String
                                 , @Field("operating_system")operating_system: String): Call<ModelsPojo>

    @FormUrlEncoded
    @POST("job/mob_applied_jobs")
    fun getAppliedJobsApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("page") page: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<AppliedJobsPojo>

    @FormUrlEncoded
    @POST("profilemob/get_profile_url")
    fun getProfileSettingsApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("user_role") user_role: String
                              , @Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system")operating_system: String): Call<GetProfileSettingsPojo>

    @FormUrlEncoded
    @POST("profilemob/user_published_setting")
    fun publisOrUnpublishProfileApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("user_role") user_role: String, @Field("status") status: String
                                    , @Header("token") token: String
                                    , @Field("device_id") device_id: String
                                    , @Field("device_brand") device_brand: String
                                    , @Field("device_model") device_model: String
                                    , @Field("device_type") device_type: String
                                    , @Field("operating_system")operating_system: String): Call<PublishOrUnpublishProfilePojo>

    @FormUrlEncoded
    @POST("user/set_username")
    fun updateUserNameApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("username") username: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<UpdateUsernamePojo>

    @FormUrlEncoded
    @POST("profilemob/set_user_security_question")
    fun setSecurityQuestionApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("question_id") question_id: String, @Field("answer") answer: String, @Field("password") password: String
                               , @Header("token") token: String
                               , @Field("device_id") device_id: String
                               , @Field("device_brand") device_brand: String
                               , @Field("device_model") device_model: String
                               , @Field("device_type") device_type: String
                               , @Field("operating_system")operating_system: String): Call<SetSecurityQuestionPojo>

    @FormUrlEncoded
    @POST("register/v2_changepassword")
    fun changePasswordApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String , @Field("old_password") old_password: String
                          ,@Field("password") user: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system") operating_system: String): Call<ChangePasswordPojo>

    @FormUrlEncoded
    @POST("user/get_user_role_services")
    fun getProfileServices(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("user_role_id") user_role_id: String
                           , @Field("user_id") user_id: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system")operating_system: String): Call<ProfileServicesPojo>

    @FormUrlEncoded
    @POST("user/update_role_services")
    fun updateServices(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("user_id") user_id: String
                       , @Field("services") services: String
                       , @Field("other_services") other_services: String
                       , @Header("token") token: String
                       , @Field("device_id") device_id: String
                       , @Field("device_brand") device_brand: String
                       , @Field("device_model") device_model: String
                       , @Field("device_type") device_type: String
                       , @Field("operating_system")operating_system: String): Call<UpdateServicePojo>

    @FormUrlEncoded
    @POST("user/get_user_role_products")
    fun getProfileProductsOrEquipments(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String
                                       , @Header("token") token: String
                                       , @Field("device_id") device_id: String
                                       , @Field("device_brand") device_brand: String
                                       , @Field("device_model") device_model: String
                                       , @Field("device_type") device_type: String
                                       , @Field("operating_system")operating_system: String): Call<ProductsAndEquipmentsPojo>

    @FormUrlEncoded
    @POST("user/update_role_products")
    fun updateProductsOrEquipments(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("products") products: String, @Field("other_products") other_products: String
                                   , @Header("token") token: String
                                   , @Field("device_id") device_id: String
                                   , @Field("device_brand") device_brand: String
                                   , @Field("device_model") device_model: String
                                   , @Field("device_type") device_type: String
                                   , @Field("operating_system")operating_system: String): Call<UpdateServicePojo>

    @FormUrlEncoded
    @POST("user/get_user_team")
    fun getTeamApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("page") page: String
                   , @Header("token") token: String
                   , @Field("device_id") device_id: String
                   , @Field("device_brand") device_brand: String
                   , @Field("device_model") device_model: String
                   , @Field("device_type") device_type: String
                   , @Field("operating_system")operating_system: String): Call<GetTeamPojo>

    @Multipart
    @POST("user/add_user_team")
    fun addTeamMemberApi(@Part image: MultipartBody.Part, @Part("auth_key") auth_key: RequestBody, @Part("user_id") user_id: RequestBody , @Part("name") name: RequestBody, @Part("role") role: RequestBody
                         , @Part("device_id") device_id: RequestBody
                         , @Part("device_brand") device_brand: RequestBody
                         , @Part("device_model") device_model: RequestBody
                         , @Part("device_type") device_type: RequestBody
                         , @Part("operating_system") operating_system: RequestBody): Call<AddOrUpdatePojo>

    @FormUrlEncoded
    @POST("home/v3_get_talent_dashboard")
    fun getTalentDashboardApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String
                              , @Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system")operating_system: String): Call<TalentDashboardPojo>

    @FormUrlEncoded
    @POST("home/v3_get_casting_dashboard")
    fun getCastingDashboardApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String
                               , @Header("token") token: String
                               , @Field("device_id") device_id: String
                               , @Field("device_brand") device_brand: String
                               , @Field("device_model") device_model: String
                               , @Field("device_type") device_type: String
                               , @Field("operating_system")operating_system: String): Call<CastingDashboardPojo>

    @FormUrlEncoded
    @POST("casting/v2_follows_by_casting")
    fun getCastingFollowingsApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("page") page: String
                                , @Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system")operating_system: String): Call<ModelsPojo>

    @FormUrlEncoded
    @POST("profilemob/v2_get_talent_follower")
    fun getTalentFollowersApi(@Header("Content-Type") header: String
                              , @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("page") page: String
                              , @Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system")operating_system: String): Call<TalentFollowersPojo>

    @FormUrlEncoded
    @POST("profilemob/v2_get_talent_likers")
    fun getTalentLikesApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("page") page: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<ModelsPojo>

    @FormUrlEncoded
    @POST("profilemob/v2_get_talent_viewers")
    fun getTalentViewsApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String, @Field("page") page: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<ModelsPojo>

    @FormUrlEncoded
    @POST("casting/v2_interested_talent_for_casting")
    fun getInterestedTalentsApi(@Header("Content-Type") header: String
                                , @Field("auth_key") auth_key: String
                                , @Field("user_id") user_id: String
                                , @Field("page") page: String
                                , @Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system")operating_system: String): Call<InterestedTalentsPojo>

    @FormUrlEncoded
    @POST("GetRSA.php")
    fun getRSAApi(@Field("access_code") access_code: String, @Field("order_id") order_id: String
                  , @Header("token") token: String
                  , @Field("device_id") device_id: String
                  , @Field("device_brand") device_brand: String
                  , @Field("device_model") device_model: String
                  , @Field("device_type") device_type: String
                  , @Field("operating_system")operating_system: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("common/report_abuse")
    fun profileReport(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("profile_id") user_name: String
                       , @Field("name") name: String
                       , @Field("email") email: String
                       , @Field("phone") phone: String
                       , @Field("subject") subject: String
                       , @Field("message") msg: String
                      , @Header("token") token: String
                      , @Field("device_id") device_id: String
                      , @Field("device_brand") device_brand: String
                      , @Field("device_model") device_model: String
                      , @Field("device_type") device_type: String
                      , @Field("operating_system")operating_system: String): Call<ReportPojo>

    @FormUrlEncoded
    @POST("common/report_portfolio")
    fun portfolioReport(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("profile_id") user_name: String
                       , @Field("name") name: String
                       , @Field("email") email: String
                       , @Field("phone") phone: String
                       , @Field("subject") subject: String
                       , @Field("message") msg: String
                       , @Field("portfolio_url") portfolio_url: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system")operating_system: String): Call<ReportPojo>

    @FormUrlEncoded
    @POST("home/add_wp_user")
    fun addWpUser(@Header("Content-Type") header: String
                  , @Field("auth_key") auth_key: String
                  , @Field("user_id") user_id: String
                  , @Header("token") token: String
                  , @Field("device_id") device_id: String
                  , @Field("device_brand") device_brand: String
                  , @Field("device_model") device_model: String
                  , @Field("device_type") device_type: String
                  , @Field("operating_system")operating_system: String): Call<BecomeFeaturedPojo>

    @FormUrlEncoded
    @POST("order/v4_order_placed")
    fun featuredCheckoutApi(@Header("Content-Type") header: String
                  , @Field("auth_key") auth_key: String
                  , @Field("user_id") user_id: String
                  , @Field("billing") billing: String
                  , @Field("shipping") shipping: String
                  , @Field("line_item") line_item: String
                  , @Field("payment_method") payment_method: String
                  , @Field("transaction_id") transaction_id: String
                  , @Field("order_total") order_total: String
                  , @Field("wp_user_id") wp_user_id: String
                  , @Header("token") token: String
                  , @Field("device_id") device_id: String
                  , @Field("device_brand") device_brand: String
                  , @Field("device_model") device_model: String
                  , @Field("device_type") device_type: String
                  , @Field("operating_system")operating_system: String): Call<CheckoutPojo>

    @FormUrlEncoded
    @POST("profile/get_profile_contact_detail")
    fun getContactDetailsApi(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                             , @Field("user_id") user_id: String
                             , @Field("profile_id") profile_id: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system")operating_system: String): Call<GetContactDetailsPojo>

    @FormUrlEncoded
    @POST("profilemob/profile_portfolio_like")
    fun likeOrDislikePortfolioApi(@Header("Content-Type") header: String
                                  , @Field("auth_key") auth_key: String
                                  , @Field("user_id") user_id: String
                                  , @Field("profile_id") profile_id: String
                                  , @Field("portfolio_id") portfolio_id: String
                                  , @Field("status") status: String
                                  , @Field("status_type") status_type: String
                                  , @Header("token") token: String
                                  , @Field("device_id") device_id: String
                                  , @Field("device_brand") device_brand: String
                                  , @Field("device_model") device_model: String
                                  , @Field("device_type") device_type: String
                                  , @Field("operating_system")operating_system: String): Call<ImageSliderPojo>

    @FormUrlEncoded
    @POST("blog/recent_posts")
    fun getRecentBlogsApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String , @Field("page") page: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<BlogListPojo>

    @FormUrlEncoded
    @POST("blog/featured_posts")
    fun getFeaturedBlogsApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String , @Field("page") page: String
                            , @Header("token") token: String
                            , @Field("device_id") device_id: String
                            , @Field("device_brand") device_brand: String
                            , @Field("device_model") device_model: String
                            , @Field("device_type") device_type: String
                            , @Field("operating_system")operating_system: String): Call<BlogListPojo>

    @FormUrlEncoded
    @POST("blog/popular_posts")
    fun getPopularBlogsApi(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String , @Field("page") page: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system")operating_system: String): Call<BlogListPojo>

    @FormUrlEncoded
    @POST("blog/similar_posts")
    fun getSimilarBlogsApi(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("cat_id") cat_id: String
                           , @Field("page") page: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system")operating_system: String): Call<BlogListPojo>

    @FormUrlEncoded
    @POST("blog/blog_detail")
    fun getBlogDetailsApi(@Header("Content-Type") header: String
                          , @Field("auth_key") auth_key: String
                          , @Field("blog_id") blog_id: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<BlogDetailsPojo>

    @FormUrlEncoded
    @POST("blog/blog_list")
    fun getAllCategoryBlogApi(@Header("Content-Type") header: String
                              , @Field("auth_key") auth_key: String
                              , @Field("cat_id") cat_id: String
                              , @Field("tag_id") tag_id: String
                              , @Field("uname") uname: String
                              , @Field("page") page: String
                              , @Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system")operating_system: String): Call<AllCategoryBlogsPojo>

    @FormUrlEncoded
    @POST("blog/get_blog_categories")
    fun getAllBlogCategoriesApi(@Header("Content-Type") header: String
                                , @Field("auth_key") auth_key: String
                                ,@Field("page") page: String
                                , @Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system")operating_system: String): Call<BlogCategoriesPojo>

    @FormUrlEncoded
    @POST("user/v2_get_user_transactions")
    fun getMyTransactionsApi(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                             , @Field("page") page: String
                             , @Field("user_id") user_id: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system")operating_system: String): Call<TransactionsPojo>

    @FormUrlEncoded
    @POST("event/featured_events")
    fun getFeaturedEventsApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String , @Field("page") page: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system")operating_system: String): Call<EventsListPojo>

    @FormUrlEncoded
    @POST("event/popular_events")
    fun getPopularEventsApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String , @Field("page") page: String
                            , @Header("token") token: String
                            , @Field("device_id") device_id: String
                            , @Field("device_brand") device_brand: String
                            , @Field("device_model") device_model: String
                            , @Field("device_type") device_type: String
                            , @Field("operating_system")operating_system: String): Call<EventsListPojo>

    @FormUrlEncoded
    @POST("event/recent_events")
    fun getLatestEventsApi(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("page") page: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system")operating_system: String): Call<EventsListPojo>

    @FormUrlEncoded
    @POST("event/promote_event")
    fun promoteEventsApi(@Header("Content-Type") header: String
                         , @Field("auth_key") auth_key: String
                         , @Field("event_cat_id") event_cat_id: String
                         , @Field("event_title") event_title: String
                         , @Field("event_date") event_date: String
                         , @Field("event_venue") event_venue: String
                         , @Field("organizer_name") organizer_name: String
                         , @Field("organizer_phone") organizer_phone: String
                         , @Field("organizer_email") organizer_email: String
                         , @Header("token") token: String
                         , @Field("device_id") device_id: String
                         , @Field("device_brand") device_brand: String
                         , @Field("device_model") device_model: String
                         , @Field("device_type") device_type: String
                         , @Field("operating_system")operating_system: String): Call<PromoteEventPojo>

    @FormUrlEncoded
    @POST("event/get_event_categories")
    fun getEventsCategoryApi(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system")operating_system: String): Call<EventsCategoryPojo>


    @FormUrlEncoded
    @POST("event/event_filter_data")
    fun getEventFilterDataApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String
                              , @Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system")operating_system: String): Call<EventFilterPojo>

    @FormUrlEncoded
    @POST("common/profile_stats_detail")
    fun getProfileStats(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String, @Field("user_id") user_id: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system")operating_system: String): Call<ProfileStatsPojo>

    @FormUrlEncoded
    @POST("common/verify_user_password")
    fun verifyPasswordApi(@Header("Content-Type") header: String
                          , @Field("auth_key") auth_key: String
                          , @Field("user_id") user_id: String
                          , @Field("password") password: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<UpdateUsernamePojo>


    @FormUrlEncoded
    @POST("common/v4_get_membership_data")
    fun getMembershipDetailsApi(@Header("Content-Type") header: String
                                , @Field("auth_key") auth_key: String
                                , @Field("user_id") user_id: String
                                , @Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system")operating_system: String): Call<MembershipPojo>

    @FormUrlEncoded
    @POST("user/my_membership_plan")
    fun getMyMembershipApi(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("user_id") user_id: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system")operating_system: String): Call<MyMembershipPojo>


    @FormUrlEncoded
    @POST("order/v4_membership_payment")
    fun membershipPaymentApi(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                            , @Field("user_id") user_id: String
                            , @Field("membership_id") membership_id: String
                            , @Field("payment_method_nonce") payment_method_nonce: String
                            , @Field("discount_total") discount_total: String
                            , @Field("total") total: String
                            , @Field("referred_by") referred_by: String
                            , @Field("payment_method") payment_method: String
                            , @Field("status") status: String
                            , @Field("membership_type") membership_type: String
                            , @Field("discount") discount: String
                            , @Field("address") address: String
                            , @Field("country") country: String
                            , @Field("city") city: String
                            , @Field("state") state: String
                            , @Field("zipcode") zipcode: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system")operating_system: String): Call<MembershipSuccessPojo>

    @FormUrlEncoded
    @POST("register/v4_active_free_user")
    fun freeMembershipApi(@Header("Content-Type") header: String
                             , @Field("auth_key") auth_key: String
                            , @Field("user_id") user_id: String
                             , @Header("token") token: String
                             , @Field("device_id") device_id: String
                             , @Field("device_brand") device_brand: String
                             , @Field("device_model") device_model: String
                             , @Field("device_type") device_type: String
                             , @Field("operating_system")operating_system: String): Call<FreeMembershipPojo>

    @FormUrlEncoded
    @POST("profilemob/secondary_role_id")
    fun updateSecondaryRoleApi(@Header("Content-Type") header: String
                               , @Field("auth_key") auth_key: String
                               , @Field("user_id") user_id: String
                               , @Field("secondary_role_id") secondary_role_id: String
                               , @Header("token") token: String
                               , @Field("device_id") device_id: String
                               , @Field("device_brand") device_brand: String
                               , @Field("device_model") device_model: String
                               , @Field("device_type") device_type: String
                               , @Field("operating_system")operating_system: String): Call<SetSecurityQuestionPojo>


    @FormUrlEncoded
    @POST("user/v2_update_user_device_info")
    fun setPrimaryDevice(@Header("Content-Type") header: String
                      , @Field("auth_key") auth_key: String
                      , @Field("user_id") user_name: String
                         , @Field("device_id") device_id: String
                         , @Field("device_brand") device_brand: String
                         , @Field("device_model") device_model: String
                         , @Field("device_type") device_type: String
                         , @Field("operating_system") operating_system: String): Call<SetPrimaryDevicePojo>

    @FormUrlEncoded
    @POST("user/v2_verify_user_device_change_otp")
    fun verifyDeviceOtp(@Header("Content-Type") header: String
                      , @Field("auth_key") auth_key: String
                      , @Field("user_id") user_name: String
                      , @Field("otp") otp: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system")operating_system: String): Call<SetPrimaryDevicePojo>



    @FormUrlEncoded
    @POST("user/verify_update_user_email")
    fun verifyEmailOtp(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("user_id") user_id: String
                       , @Field("email") email: String
                       , @Field("otp") otp: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system") operating_system: String): Call<ValidateOtpPojo>


    @FormUrlEncoded
    @POST("user/check_email_update")
    fun sendEmailOtp(@Header("Content-Type") header: String
                     , @Field("auth_key") auth_key: String
                     , @Field("user_id") user_id: String
                     , @Field("email") email: String
                     , @Header("token") token: String
                     , @Field("device_id") device_id: String
                     , @Field("device_brand") device_brand: String
                     , @Field("device_model") device_model: String
                     , @Field("device_type") device_type: String
                     , @Field("operating_system") operating_system: String): Call<ValidateOtpPojo>


    @FormUrlEncoded
    @POST("user/check_username_availability")
    fun checkUsernameApi(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String
                         , @Field("user_id") user_id: String
                         , @Field("username") username: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<UpdateUsernamePojo>


    @FormUrlEncoded
    @POST("event/event_contact_request")
    fun eventContact(@Header("Content-Type") header: String
                , @Field("auth_key") auth_key: String
                , @Field("name") name: String
                , @Field("phone") phone: String
                , @Field("email") email: String
                , @Field("msg") msg: String
                , @Field("event_id") type: String
                , @Field("event_title") event_title: String
                , @Field("event_slug") event_slug: String
                , @Header("token") token: String
                , @Field("device_id") device_id: String
                , @Field("device_brand") device_brand: String
                , @Field("device_model") device_model: String
                , @Field("device_type") device_type: String
                , @Field("operating_system")operating_system: String): Call<EventContactPojo>



    @Multipart
    @POST("profile/upload_contact_message_image")
    fun uploadMultimediaChat(@Part image: MultipartBody.Part
                             , @Part("auth_key") auth_key: RequestBody
                             , @Part("thread_id") thread_id: RequestBody
                             , @Part("sender_id") sender_id: RequestBody
                             , @Part("name") name: RequestBody
                             , @Part("email") email: RequestBody
                             , @Part("phone") phone: RequestBody
                             , @Part("device_id") device_id: RequestBody
                             , @Part("device_brand") device_brand: RequestBody
                             , @Part("device_model") device_model: RequestBody
                             , @Part("device_type") device_type: RequestBody
                             , @Part("operating_system") operating_system: RequestBody): Call<MultimediaChatPojo>


    @FormUrlEncoded
    @POST("user/user_block_status")
    fun blockUser(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("sender_id") sender_id: String
                       , @Field("thread_id") thread_id: String
                       , @Field("status") status: String
                       , @Header("token") token: String
                       , @Field("device_id") device_id: String
                       , @Field("device_brand") device_brand: String
                       , @Field("device_model") device_model: String
                       , @Field("device_type") device_type: String
                       , @Field("operating_system") operating_system: String): Call<BlockUserPojo>

    @FormUrlEncoded
    @POST("profile/generate_thread")
    fun generateThread(@Header("Content-Type") header: String
                  , @Field("auth_key") auth_key: String
                  , @Field("user_id") user_id: String
                  , @Field("profile_id") profile_id: String
                  , @Header("token") token: String
                  , @Field("device_id") device_id: String
                  , @Field("device_brand") device_brand: String
                  , @Field("device_model") device_model: String
                  , @Field("device_type") device_type: String
                  , @Field("operating_system") operating_system: String): Call<GenerateThraedPojo>

    @FormUrlEncoded
    @POST("common/get_state_by_country")
    fun getStatesByCountry(@Header("Content-Type") header: String
                           , @Field("auth_key") auth_key: String
                           , @Field("country_id") user_id: String): Call<StatesPojo>

    @GET("common/get_countries")
    fun getCountries(@Header("Content-Type") header: String
    ): Call<CountryPojo>

    @GET("job/get_job_cities")
    fun getJobCities(@Header("Content-Type") header: String): Call<JobFilterPojo>



    @FormUrlEncoded
    @POST("institute/institute_data")
    fun getInstitutesApi(@Header("Content-Type") header: String
                          , @Field("auth_key") auth_key: String
                          , @Field("city") city: String
                          , @Field("cat_id") cat_id: String
                          , @Field("page") page: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system")operating_system: String): Call<InstitutePojo>


    @FormUrlEncoded
    @POST("institute/institute_detail")
    fun getInstituteDetailsApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String
                               , @Field("institute_id") institute_id: String
                           , @Header("token") token: String
                           , @Field("device_id") device_id: String
                           , @Field("device_brand") device_brand: String
                           , @Field("device_model") device_model: String
                           , @Field("device_type") device_type: String
                           , @Field("operating_system")operating_system: String): Call<InstituteDetailsPojo>

    @Multipart
    @POST("user/add_user_audio")
    fun UploadPortfolioAudioApi(@Part image: MultipartBody.Part, @Part("auth_key") auth_key: RequestBody
                              , @Part("user_id") user_id: RequestBody
                              , @Part("title") title: RequestBody
                              , @Part("device_id") device_id: RequestBody
                              , @Part("device_brand") device_brand: RequestBody
                              , @Part("device_model") device_model: RequestBody
                              , @Part("device_type") device_type: RequestBody
                              , @Part("operating_system") operating_system: RequestBody): Call<UploadAudioPojo>


    @FormUrlEncoded
    @POST("user/delete_user_audio")
    fun deletePortfolioAudio(@Header("Content-Type") header: String
                                , @Field("auth_key") auth_key: String
                                , @Field("user_id") user_id: String
                                , @Field("audio_id") audio_id: String
                                ,@Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system") operating_system: String): Call<DeletePojo>

    @FormUrlEncoded
    @POST("profilemob/getaudios")
    fun getPortfolioAudios(@Header("Content-Type") header: String, @Field("auth_key") auth_key: String
                           , @Field("user_id") user_id: String
                           , @Field("page") page: String
                              ,@Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system") operating_system: String): Call<PortfolioAudiosPojo>


    @FormUrlEncoded
    @POST("notification/user_notifications")
    fun getNotificationsApi(@Header("Content-Type") header: String
                               , @Field("auth_key") auth_key: String
                               , @Field("user_id") user_id: String
                               , @Field("page") page: String
                               , @Header("token") token: String
                               , @Field("device_id") device_id: String
                               , @Field("device_brand") device_brand: String
                               , @Field("device_model") device_model: String
                               , @Field("device_type") device_type: String
                               , @Field("operating_system")operating_system: String): Call<NotificationsPojo>



    @FormUrlEncoded
    @POST("job/v3_job_shortlist")
    fun shortlistJobApi(@Header("Content-Type") header: String
                        , @Field("auth_key") auth_key: String
                        , @Field("user_id") user_id: String
                        , @Field("job_id") job_id: String
                        , @Field("status") status: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system")operating_system: String): Call<ShortlistJobPojo>

    @FormUrlEncoded
    @POST("job/recommended_jobs")
    fun getRecommendedJobsApi(@Header("Content-Type") header: String , @Field("auth_key") auth_key: String
                            , @Field("user_id") user_id: String
                            , @Field("page") page: String
                            , @Header("token") token: String
                            , @Field("device_id") device_id: String
                            , @Field("device_brand") device_brand: String
                            , @Field("device_model") device_model: String
                            , @Field("device_type") device_type: String
                            , @Field("operating_system")operating_system: String): Call<JobsPojo>

    @FormUrlEncoded
    @POST("event/recommended_events")
    fun getRecommendedEventsApi(@Header("Content-Type") header: String
                            , @Field("auth_key") auth_key: String
                            , @Field("user_id") user_id: String
                            , @Field("page") page: String
                            , @Header("token") token: String
                            , @Field("device_id") device_id: String
                            , @Field("device_brand") device_brand: String
                            , @Field("device_model") device_model: String
                            , @Field("device_type") device_type: String
                            , @Field("operating_system")operating_system: String): Call<EventsListPojo>

    @FormUrlEncoded
    @POST("home/user_dashboard_activity_count")
    fun getTalentActivitySummaryApi(@Header("Content-Type") header: String
                                    , @Field("auth_key") auth_key: String
                                    , @Field("user_id") user_id: String
                                    , @Field("count_type") count_type: String
                              , @Header("token") token: String
                              , @Field("device_id") device_id: String
                              , @Field("device_brand") device_brand: String
                              , @Field("device_model") device_model: String
                              , @Field("device_type") device_type: String
                              , @Field("operating_system")operating_system: String): Call<ActivitySummaryPojo>


    @FormUrlEncoded
    @POST("job/v3_view_job_application")
    fun viewApplication(@Header("Content-Type") header: String
                        , @Field("auth_key") auth_key: String
                        , @Field("user_id") user_id: String
                        , @Field("call_id") call_id: String
                        , @Field("profile_id") profile_id: String
                        , @Header("token") token: String
                        , @Field("device_id") device_id: String
                        , @Field("device_brand") device_brand: String
                        , @Field("device_model") device_model: String
                        , @Field("device_type") device_type: String
                        , @Field("operating_system")operating_system: String): Call<ViewApplicationPojo>



    @FormUrlEncoded
    @POST("logs/add_call_log")
    fun addCallLogsApi(@Header("Content-Type") header: String
                       , @Field("auth_key") auth_key: String
                       , @Field("user_id") user_id: String
                       , @Field("caller_name") caller_name: String
                          ,@Field("caller_id") caller_id: String
                          ,@Field("call_duration") call_duration: String
                          ,@Field("call_type") call_type: String
                          ,@Field("date_time") date_time: String
                          ,@Field("is_caller") is_caller: String
                          , @Header("token") token: String
                          , @Field("device_id") device_id: String
                          , @Field("device_brand") device_brand: String
                          , @Field("device_model") device_model: String
                          , @Field("device_type") device_type: String
                          , @Field("operating_system") operating_system: String): Call<AddCallLogPojo>


    @FormUrlEncoded
    @POST("logs/get_call_log")
    fun getCallLogsApi(@Header("Content-Type") header: String
                                , @Field("auth_key") auth_key: String
                                , @Field("user_id") user_id: String
                                , @Header("token") token: String
                                , @Field("device_id") device_id: String
                                , @Field("device_brand") device_brand: String
                                , @Field("device_model") device_model: String
                                , @Field("device_type") device_type: String
                                , @Field("operating_system")operating_system: String): Call<CallLogsPojo>


    @GET("payment/get_payment_token")
    fun getTokenApi(): Call<GetTokenPojo>

}
