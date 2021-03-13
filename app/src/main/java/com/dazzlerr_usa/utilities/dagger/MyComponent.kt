package com.dazzlerr_usa.utilities.dagger

import com.dazzlerr_usa.utilities.firebasemessaging.MyFirebaseMessagingService
import com.dazzlerr_usa.utilities.imageslider.ImagePreviewActivity
import com.dazzlerr_usa.utilities.sinchcalling.SinchService
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.location.LocationActivity
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.UserInActiveActivity
import com.dazzlerr_usa.views.activities.WelcomeActivity
import com.dazzlerr_usa.views.activities.settings.SettingsActivity
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.activities.addjob.AddOrEditJobActivity
import com.dazzlerr_usa.views.activities.becomefeatured.BecomeFeaturedActivity
import com.dazzlerr_usa.views.activities.blogs.activities.AllCategoryBlogsActivity
import com.dazzlerr_usa.views.activities.blogs.activities.BlogDetailsActivity
import com.dazzlerr_usa.views.activities.blogs.activities.BlogListActivity
import com.dazzlerr_usa.views.activities.calllogs.CallLogsActivity
import com.dazzlerr_usa.views.activities.castingfollowings.CastingFollowingsActivity
import com.dazzlerr_usa.views.activities.featuredccavenue.AddressActivity
import com.dazzlerr_usa.views.activities.changepassword.ChangePasswordActivity
import com.dazzlerr_usa.views.activities.contact.ProfileContactActivity
import com.dazzlerr_usa.views.activities.contactus.ContactUsActivity
import com.dazzlerr_usa.views.activities.editcastingprofile.EditCastingProfileActivity
import com.dazzlerr_usa.views.activities.editprofile.*
import com.dazzlerr_usa.views.activities.elitecontact.EliteContactActivity
import com.dazzlerr_usa.views.activities.elitememberdetails.EliteMemberDetailsActivity
import com.dazzlerr_usa.views.activities.emailverification.EmailVerificationActivity
import com.dazzlerr_usa.views.activities.emailverification.IndividualEmailVerificationActivity
import com.dazzlerr_usa.views.activities.eventcontact.EventContactActivity
import com.dazzlerr_usa.views.activities.eventdetails.EventDetailsActivity
import com.dazzlerr_usa.views.activities.events.activities.EventListActivity
import com.dazzlerr_usa.views.activities.influencerdetails.InfluencerDetailsActivity
import com.dazzlerr_usa.views.activities.institute.InstituteListActivity
import com.dazzlerr_usa.views.activities.institute.InstitutesAdapter
import com.dazzlerr_usa.views.activities.interestedtalents.InterestedTalentsActivity
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.dazzlerr_usa.views.activities.messages.MessagesActivity
import com.dazzlerr_usa.views.activities.messagewindow.MessageWindowActivity
import com.dazzlerr_usa.views.activities.mobileverification.MobileVerificationActivity
import com.dazzlerr_usa.views.activities.myJobs.MyJobsActivity
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.activities.portfolio.addproject.PortfolioProjectActivity
import com.dazzlerr_usa.views.activities.portfolio.addvideo.AddVideoActivity
import com.dazzlerr_usa.views.activities.profileteam.AddTeamMemberActivity
import com.dazzlerr_usa.views.activities.profileteam.ProfileTeamActivity
import com.dazzlerr_usa.views.activities.report.ProfileReportActivity
import com.dazzlerr_usa.views.activities.shortlistedtalents.ShortListedTalentsActivity
import com.dazzlerr_usa.views.activities.talentfollowers.TalentFollowersActivity
import com.dazzlerr_usa.views.activities.talentlikes.TalentLikesActivity
import com.dazzlerr_usa.views.activities.talentviews.TalentViewsActivity
import com.dazzlerr_usa.views.activities.transactions.TransactionsActivity
import com.dazzlerr_usa.views.fragments.castingdashboard.CastingDashboardFragment
import com.dazzlerr_usa.views.fragments.castingprofile.localfragments.CastingProfileFragment
import com.dazzlerr_usa.views.activities.membership.MembershipActivity
import com.dazzlerr_usa.views.activities.membership.MembershipCCEvenuePaymentActivity
import com.dazzlerr_usa.views.activities.membership.MembershipSuccessActivity
import com.dazzlerr_usa.views.activities.membership.MemershipAddressActivity
import com.dazzlerr_usa.views.activities.mymembership.MyMembershipActivity
import com.dazzlerr_usa.views.activities.notifications.NotificationsActivity
import com.dazzlerr_usa.views.activities.recommendedevents.RecommendedEventsActivity
import com.dazzlerr_usa.views.activities.recommendedjobs.RecommendedJobsActivity
import com.dazzlerr_usa.views.activities.registersuccess.RegisterSuccessActivity
import com.dazzlerr_usa.views.activities.setprimarydevice.SetPrimaryDeviceActivity
import com.dazzlerr_usa.views.activities.synchcalling.CallScreenActivity
import com.dazzlerr_usa.views.activities.synchcalling.IncomingCallScreenActivity
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioImagesFragment
import com.dazzlerr_usa.views.fragments.profile.FeaturedProfileFragment
import com.dazzlerr_usa.views.fragments.profile.ProfileFragment
import com.dazzlerr_usa.views.fragments.profile.childfragments.ProfileAboutFragment
import com.dazzlerr_usa.views.fragments.profile.childfragments.ProfileImagesFragment
import com.dazzlerr_usa.views.fragments.profile.childfragments.ProfileServicesFragment
import com.dazzlerr_usa.views.fragments.profileemailphoneverification.CastingProfileVerificationSuccessfullFragment
import com.dazzlerr_usa.views.fragments.profileemailphoneverification.CastingProfileVideoProofFragment
import com.dazzlerr_usa.views.fragments.profileemailphoneverification.CastingProfleImageProofFragment
import com.dazzlerr_usa.views.fragments.talentdashboard.TalentDashboardFragment
import com.dazzlerr_usa.views.activities.weblinks.WebLinksActivity

import javax.inject.Singleton

import dagger.Component


@Singleton
@Component(modules = [DaggerModule::class])
interface MyComponent
{
    fun inject(adapter: InstitutesAdapter)
    fun inject(service: SinchService)

    fun inject(activity: MainActivity)
    fun inject(activity: HomeActivity)
    fun inject(activity: SettingsActivity)
    fun inject(activity: LocationActivity)
    fun inject(activity: JobsDetailsActivity)
    fun inject(activity: MessageWindowActivity)
    fun inject(activity: PortfolioProjectActivity)
    fun inject(activity: AddVideoActivity)
    fun inject(activity: EditGeneralInformationActivity)
    fun inject(activity: EditAppearanceActivity)
    fun inject(activity: EditRatesAndTravelActivity)
    fun inject(activity: EliteMemberDetailsActivity)
    fun inject(activity: EliteContactActivity)
    fun inject(activity: InfluencerDetailsActivity)
    fun inject(activity: EventDetailsActivity)
    fun inject(activity: ProfileContactActivity)
    fun inject(activity: OthersProfileActivity)
    fun inject(activity: AddOrEditJobActivity)
    fun inject(activity: MyJobsActivity)
    fun inject(activity: EditCastingProfileActivity)
    fun inject(activity: AccountVerification)
    fun inject(activity: PortfolioActivity)
    fun inject(activity: ShortListedTalentsActivity)
    fun inject(activity: ChangePasswordActivity)
    fun inject(activity: WelcomeActivity)
    fun inject(activity: EditProfileActivity)
    fun inject(activity: EditServicesActivity)
    fun inject(activity: MessagesActivity)
    fun inject(activity: EditProductsActivity)
    fun inject(activity: EditEquipmentsActivity)
    fun inject(activity: ProfileTeamActivity)
    fun inject(activity: EditTeamActivity)
    fun inject(activity: AddTeamMemberActivity)
    fun inject(activity: CastingFollowingsActivity)
    fun inject(activity: InterestedTalentsActivity)
    fun inject(activity: TalentFollowersActivity)
    fun inject(activity: TalentLikesActivity)
    fun inject(activity: TalentViewsActivity)
    fun inject(activity: UserInActiveActivity)
    fun inject(activity: ProfileReportActivity)
    fun inject(activity: BecomeFeaturedActivity)
    fun inject(activity: AddressActivity)
    fun inject(activity: TransactionsActivity)
    fun inject(activity: ImagePreviewActivity)
    fun inject(activity: EditProfileImageActivity)
    fun inject(activity: MobileVerificationActivity)
    fun inject(activity: MembershipActivity)
    fun inject(activity: MembershipSuccessActivity)
    fun inject(activity: EmailVerificationActivity)
    fun inject(activity: SetPrimaryDeviceActivity)
    fun inject(activity: MyMembershipActivity)
    fun inject(activity: IndividualEmailVerificationActivity)
    fun inject(activity: ContactUsActivity)
    fun inject(activity: EventContactActivity)
    fun inject(activity: MembershipCCEvenuePaymentActivity)
    fun inject(activity: MemershipAddressActivity)
    fun inject(activity: NotificationsActivity)
    fun inject(activity: RecommendedJobsActivity)
    fun inject(activity: RecommendedEventsActivity)
    fun inject(activity: RegisterSuccessActivity)
    fun inject(activity: BlogDetailsActivity)
    fun inject(activity: BlogListActivity)
    fun inject(activity: AllCategoryBlogsActivity)
    fun inject(activity: EventListActivity)
    fun inject(activity: InstituteListActivity)
    fun inject(activity: WebLinksActivity)
    fun inject(activity: IncomingCallScreenActivity)
    fun inject(activity: CallScreenActivity)
    fun inject(activity: CallLogsActivity)

    fun inject(fragment: ProfileFragment)
    fun inject(fragment: CastingProfileFragment)
    fun inject(fragment: CastingProfleImageProofFragment)
    fun inject(fragment: CastingProfileVideoProofFragment)
    fun inject(fragment: CastingProfileVerificationSuccessfullFragment)
    fun inject(fragment: MyFirebaseMessagingService)
    fun inject(fragment: TalentDashboardFragment)
    fun inject(fragment: CastingDashboardFragment)
    fun inject(fragment: ProfileImagesFragment)
    fun inject(fragment: ProfileAboutFragment)
    fun inject(fragment: ProfileServicesFragment)
    fun inject(fragment: PortfolioImagesFragment)
    fun inject(fragment: FeaturedProfileFragment)
}
