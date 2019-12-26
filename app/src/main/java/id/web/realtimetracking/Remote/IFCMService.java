package id.web.realtimetracking.Remote;


import id.web.realtimetracking.Model.MyResponse;
import id.web.realtimetracking.Model.Request;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key = AAAAuLHY04I:APA91bG8vBvk9IweJMGsEpLARx-YmBSUb2BmkT-32rmJH2nfxfYHSm__FHluQ5GNJDIIz2kt-rN94xlNAPYhcKNNFzbJ4OxGbL8WUDpoL6YBms4-IzoXwfHmC46fHogSzZQlHaQpV2qR"

    })
    @POST("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);
}
