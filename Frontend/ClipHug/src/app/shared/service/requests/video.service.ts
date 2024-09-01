import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {Observable} from "rxjs";
import {ApiResponse} from "../../models/ApiResponse";
import {VideoModel} from "../../models/Login/video.model";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root',
})
export class VideoService {
  constructor(private apiService: ApiService) {
  }


  uploadVideo(file: File) {
    const formData = new FormData();
    formData.append('video', file);

    return this.apiService.post(`/video/create`, { body: formData});
  }

  // Right now the API only supports getting all videos from its own user, in the future I will add a possibility to get all videos from other users
  getAllVideosFromSelf(): Observable<ApiResponse<VideoModel[]>> {
    return this.apiService
      .get<ApiResponse<VideoModel[]>>('/video')
      .pipe(
        catchError((error) => {
          console.error('Error fetching vi: ', error);
          throw error;
        })
      );
  }


  getVideoUrl(video: VideoModel) {
    return ApiService.API_URL + "/video/" + video.videoId;
  }

  getThumbnailUrl(video: VideoModel) {
    console.log( ApiService.API_URL + "/video/frame/" + video.videoId);
    return ApiService.API_URL + "/video/frame/" + video.videoId;
  }
}
