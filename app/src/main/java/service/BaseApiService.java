package service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by christian on 14/02/18.
 */

public interface BaseApiService {

    @FormUrlEncoded
    @POST("countUserRate.php")
    Call<ResponseBody> countUser(@Field("idKos") Integer idKos,
                                 @Field("idUser") Integer idUser
    );

    @FormUrlEncoded
    @POST("dialogRate.php")
    Call<ResponseBody> saveRating(@Field("idKos") Integer idKos,
                                  @Field("idUser") Integer idUser,
                                  @Field("isi") String isi,
                                  @Field("rate") double rate
    );

    @FormUrlEncoded
    @POST("notif2.php")
    Call<ResponseBody> notif(@Field("topics") String id,
                                  @Field("message") String pesan,
                                  @Field("judul") String judul
    );

}