package com.attendance.tracker.network;


import com.attendance.tracker.activity.AgentList.AgentListResponse;
import com.attendance.tracker.activity.AttendanceReport.AttendanceReportResponse;
import com.attendance.tracker.activity.AttendanceReport.DailyAttendReportResponse;
import com.attendance.tracker.activity.NewCompanyList.CompanyDetailsData;
import com.attendance.tracker.activity.NewCompanyList.CompanyListResponse;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsData;
import com.attendance.tracker.agent.SelfDueModel;
import com.attendance.tracker.data.BlocklistResponse;
import com.attendance.tracker.data.ChnagePassResponse;
import com.attendance.tracker.data.CommissionModel;
import com.attendance.tracker.data.ControllingModel;
import com.attendance.tracker.data.DateSearchGeoData;
import com.attendance.tracker.data.DeuData;
import com.attendance.tracker.data.DueModel;
import com.attendance.tracker.data.ForgetPasswordModel;
import com.attendance.tracker.data.GeoFanceReport;
import com.attendance.tracker.data.GeoReport;
import com.attendance.tracker.data.GeoSubmitResponse;
import com.attendance.tracker.data.HelpModel;
import com.attendance.tracker.data.JoinResponse;
import com.attendance.tracker.data.LoginData;
import com.attendance.tracker.data.PaymentModel;
import com.attendance.tracker.data.ProfileData;
import com.attendance.tracker.data.SalesReport;
import com.attendance.tracker.data.SalesReportDetails;
import com.attendance.tracker.data.SearchResponse;
import com.attendance.tracker.data.TaskDetailsResponse;
import com.attendance.tracker.data.TaskListResponse;
import com.attendance.tracker.data.TaskinputResponse;
import com.attendance.tracker.data.UserData;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {

    //For submit geo
    @POST("api/geo-fance-set.php")
    @FormUrlEncoded
    Call<GeoSubmitResponse> submitGeo(@Field("user_id") String user_id,
                                      @Field("geo") String geo,
                                      @Field("radius") String radius,
                                      @Field("timestamp") String timestamp

    );

    @POST("api/geo-fance-report.php")
    @FormUrlEncoded
    Call<GeoFanceReport> getAllGeo(@Field("user_id") String user_id
    );


    @POST("api/geo-attendance.php")
    @FormUrlEncoded
    Call<GeoSubmitResponse> submitAttend(@Field("user_id") String user_id,
                                         @Field("id") String id,
                                         @Field("timestamp") String timestamp,
                                         @Field("geo") String geo,
                                         @Field("type") String type,
                                         @Field("user_geo_id") String userGeoId

    );

    @POST("api/geo-attendance-report.php")
    @FormUrlEncoded
    Call<GeoReport> showGeoReport(@Field("user_id") String user_id
    );

    @POST("api/attendance_search.php")
    @FormUrlEncoded
    Call<DateSearchGeoData> showDateGeoReport(
            @Field("user_id") String user_id,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("type") String type
    );

    //daily attendance
    @POST("api/daily_attendance_report.php")
    @FormUrlEncoded
    Call<DailyAttendReportResponse> showDailyAttReport(
            @Field("user_id") String user_id,
            @Field("day") String day,
            @Field("month") String month,
            @Field("year") String year
    );

    //monthly attendance
    @POST("api/attendance_report.php")
    @FormUrlEncoded
    Call<AttendanceReportResponse> showAttReport(
            @Field("user_id") String user_id,
            @Field("week") String week,
            @Field("month") String month,
            @Field("year") String year
    );

    //monthly attendance
    @POST("api/block_list.php")
    @FormUrlEncoded
    Call<BlocklistResponse> showblockuser(
            @Field("user_id") String user_id

    );


    @POST("api/profile_list.php")
    @FormUrlEncoded
    Call<UserData> getUserList(@Field("user_id") String user_id
    );

    @POST("api/block_id.php")
    @FormUrlEncoded
    Call<JsonObject> submitBlockList(
            @Field("user_id") String user_id,
            @Field("id") String id,
            @Field("condition") String condition
    );

    @POST("api/login.php")
    @FormUrlEncoded
    Call<LoginData> goLogin(@Field("user_name") String user_name,
                            @Field("password") String password
    );

    @POST("api/delete_profile.php")
    @FormUrlEncoded
    Call<JoinResponse> deleteProfile(@Field("user_id") String user_id,
                                     @Field("id") String id
    );

    @POST("api/delete_geo.php")
    @FormUrlEncoded
    Call<JoinResponse> deleteGeo(@Field("user_id") String user_id,
                                 @Field("id") String id
    );

    // leader join
    @POST("api/join.php")
    @FormUrlEncoded
    Call<JoinResponse> goJoin(@Field("user_name") String user_name,
                              @Field("password") String password,
                              @Field("mobile") String mobile,
                              @Field("type") String type,
                              @Field("leader") String leader,
                              @Field("name") String name,
                              @Field("email") String email,
                              @Field("nid") String nid,
                              @Field("father") String father,
                              @Field("mother") String mother,
                              @Field("country") String country,
                              @Field("division") String division,
                              @Field("distict") String distict,
                              @Field("thana") String thana,
                              @Field("first_password") String first_password,
                              @Field("re_password") String re_password);


    //Profile Edit

    @Multipart
    @POST("api/profile_edit.php")
    Call<JsonObject> ProfileEdit(@Part("user_name") RequestBody user_name,
                                 @Part("password") RequestBody password,
                                 @Part("id") RequestBody user_id,
                                 @Part("name") RequestBody name,
                                 @Part("email") RequestBody email,
                                 @Part("mobile") RequestBody mobile,
                                 @Part("nid") RequestBody nid,
                                 @Part("address") RequestBody address,
                                 @Part("father_name") RequestBody father_name,
                                 @Part("mother_name") RequestBody mother_name,
                                 @Part("gender") RequestBody gender,
                                 @Part MultipartBody.Part image,
                                 @Part("mobile2") RequestBody mobile2

                                 );

    //get leader list
    @POST("api/leader_list.php")
    Call<JsonObject> getLeader();


    // make Company join
    @POST("api/company_make.php")
    @FormUrlEncoded
    Call<JoinResponse> makeCompany(@Field("user_name") String user_name,
                                   @Field("password") String password,
                                   @Field("mobile") String mobile,
                                   @Field("mobile2") String mobile2,
                                   @Field("name") String name,
                                   @Field("email") String email,
                                   @Field("country") String country,
                                   @Field("division") String division,
                                   @Field("distict") String distict,
                                   @Field("thana") String thana,
                                   @Field("address") String address,
                                   @Field("first_password") String first_password,
                                   @Field("re_password") String re_password,
                                   @Field("company") String company,
                                   @Field("cost") String cost,
                                   @Field("employee") String employee
    );
    // make Company join
    @POST("api/company_edit.php")
    @FormUrlEncoded
    Call<JsonObject> EditCompanyProfile(@Field("user_name") String user_name,
                                   @Field("password") String password,
                                   @Field("user_id") String user_id,
                                   @Field("name") String name,
                                   @Field("email") String email,
                                   @Field("address") String address,
                                   @Field("mobile") String mobile,
                                   @Field("mobile2") String mobile2,
                                   @Field("cost") String cost,
                                   @Field("employee") String employee
    );

    @POST("api/profile_list.php")
    @FormUrlEncoded
    Call<ProfileData> getProfileList(@Field("user_id") String user_id,
                                     @Field("type") String type,
                                     @Field("search") String search
    );

    //new Company list api
    @POST("api/company_list.php")
    @FormUrlEncoded
    Call<CompanyListResponse> getCompanyList(@Field("user_id") String user_id,
                                             @Field("limit") String limit,
                                             @Field("search") String search
    );

    //new Company list api
    @POST("api/company_list.php")
    @FormUrlEncoded
    Call<CompanyListResponse> getAgentCompanyList(@Field("agent_id") String user_id,
                                             @Field("limit") String limit,
                                             @Field("search") String search
    );

    //new agent list api
    @POST("api/agent_list.php")
    @FormUrlEncoded
    Call<AgentListResponse> getAgentList(@Field("user_id") String user_id,
                                         @Field("limit") String limit,
                                         @Field("search") String search
    );

    //search api
    @POST("api/search_user.php")
    @FormUrlEncoded
    Call<SearchResponse> GetSearchList(
            @Field("user_id") String user_id,
            @Field("search") String search

    );

    //Get Country List
    @POST("api/rbp_list.php")
    @FormUrlEncoded
    Call<JsonObject> getCountryList(@Field("country_check") String identifyNull);

    //Get Division List
    @POST("api/rbp_list.php")
    @FormUrlEncoded
    Call<JsonObject> getDivisionList(@Field("division_check") String identifyNull,
                                     @Field("Country_ID") String countryID);

    //Get District List
    @POST("api/rbp_list.php")
    @FormUrlEncoded
    Call<JsonObject> getDistrictList(@Field("district_check") String identifyNull,
                                     @Field("Division_ID") String divisionID,
                                     @Field("Country_ID") String countryID);


    //Get Thana List
    @POST("api/rbp_list.php")
    @FormUrlEncoded
    Call<JsonObject> getThanaList(@Field("thana_check") String identifyNull,
                                  @Field("District_ID") String districtID,
                                  @Field("Division_ID") String divisionID,
                                  @Field("Country_ID") String countryID);

    //Get union List
    @POST("api/rbp_list.php")
    @FormUrlEncoded
    Call<JsonObject> getUnionList(@Field("union_check") String identifyNull,
                                  @Field("Thana_ID") String thanaID,
                                  @Field("District_ID") String districtID,
                                  @Field("Division_ID") String divisionID,
                                  @Field("Country_ID") String countryID);

    //For submit geo
    @POST("api/common_geo.php")
    @FormUrlEncoded
    Call<GeoSubmitResponse> submitCompanyGeo(@Field("user_id") String user_id,
                                             @Field("geo") String geo,
                                             @Field("timestamp") String timestamp,
                                             @Field("radius") String radius,
                                             @Field("type") String type

    );

    //For chnage password
    @POST("api/password_change.php")
    @FormUrlEncoded
    Call<ChnagePassResponse> ChangePass(@Field("user_name") String user_name,
                                        @Field("password") String password,
                                        @Field("old_password") String old_password,
                                        @Field("new_password") String new_password,
                                        @Field("again_password") String again_password

    );


    //Profile details
    @FormUrlEncoded
    @POST("api/profile.php")
    Call<ProfileDetailsData> getProfiledetails(
            @Field("user_id") String user_id

    );
    //company details
    @FormUrlEncoded
    @POST("api/company_details.php")
    Call<CompanyDetailsData> getCompanydetails(
            @Field("user_id") String user_id,
            @Field("id") String id

    );

    @FormUrlEncoded
    @POST("api/task_input.php")
    Call<TaskinputResponse> getTaskinput(
            @Field("user_id") String user_id,
            @Field("employee_id") String employee_id,
            @Field("note") String note

    );

    @FormUrlEncoded
    @POST("api/task_details.php")
    Call<TaskDetailsResponse> getTaskDetails(
            @Field("user_id") String user_id,
            @Field("task_id") String task_id

    );

    @FormUrlEncoded
    @POST("api/task_complete.php")
    Call<JsonObject> TaskComplete(
            @Field("user_id") String user_id,
            @Field("task_id") String task_id,
            @Field("mark") String mark
    );

    //Profile details
    @FormUrlEncoded
    @POST("api/task_list.php")
    Call<TaskListResponse> getTaskList(
            @Field("user_id") String user_id

    );

    //For submit geo  //old
    @POST("api/manual-work.php")  //replace new response
    @FormUrlEncoded
    Call<GeoSubmitResponse> submitManual(@Field("user_id") String user_id,
                                         @Field("geo") String geo,
                                         @Field("address") String address,
                                         @Field("hints") String hints

    );

    //forget password -
    @POST("api/email_check.php")
    @FormUrlEncoded
    Call<ForgetPasswordModel> sendVerifyCode(@Field("user_check") String userCheck);


    //forget password -
    @POST("api/email_check.php")
    @FormUrlEncoded
    Call<JsonObject> submitVerifyCode(@Field("user") String user,
                                      @Field("code") String code);

    //Forget password
    @POST("api/email_check.php")
    @FormUrlEncoded
    Call<JsonObject> submitForgetPassword(@Field("user") String user,
                                          @Field("new") String newPassword,
                                          @Field("again") String againPassword);
    //due list
    @POST("api/due_list.php")
    @FormUrlEncoded
    Call<DueModel> getDueList(@Field("user_id") String user_id,
                              @Field("limit") String limit,
                              @Field("search") String search);

    //due list
    @POST("api/commission_list.php")
    @FormUrlEncoded
    Call<CommissionModel> getCommissionList(@Field("user_id") String user_id,
                                            @Field("limit") String limit,
                                            @Field("search") String search);

    //due list
    @FormUrlEncoded
    @POST("api/self_due.php")
    Call<DeuData> getDeuList(@Field("user_id") String user_id);
    //due list
    @POST("api/help.php")
    @FormUrlEncoded
    Call<HelpModel> getHelpList(@Field("user_id") String user_id);

    //due list
    @POST("api/self_due_list.php")
    @FormUrlEncoded
    Call<SelfDueModel> getSelfDueList(@Field("user_id") String user_id,
                                      @Field("limit") String limit,
                                      @Field("type") String type);
    //payment list
    @POST("api/monthly_sales.php")
    @FormUrlEncoded
    Call<SalesReport> getMonthlyList(@Field("user_name") String user_name,
                                     @Field("password") String password,
                                     @Field("month") String month,
                                     @Field("year") String year);
    //sales report details
    @POST("api/collect_list.php")
    @FormUrlEncoded
    Call<SalesReportDetails> getSalesReportDetails(@Field("user_id") String user_id,
                                                   @Field("limit") String limit,
                                                   @Field("search") String search,
                                                   @Field("day") String day,
                                                   @Field("month") String month,
                                                   @Field("year") String year);

    @POST("api/payment_method.php")
    @FormUrlEncoded
    Call<PaymentModel> getPaymentList(@Field("user_id") String user_id);

    //due list
    @POST("api/due_paid.php")
    @FormUrlEncoded
    Call<JsonObject> submitDue(@Field("user_id") String user_id,
                               @Field("method") String method,
                               @Field("sender_number") String sender_number,
                               @Field("invoice") String invoice,
                               @Field("due_id") String due_id,
                               @Field("account_title") String account_title,
                               @Field("tranx_id") String tranx_id);

    @POST("api/controlling.php")
    @FormUrlEncoded
    Call<ControllingModel> getServerStatus(@Field("user_id") String user_id);
}
