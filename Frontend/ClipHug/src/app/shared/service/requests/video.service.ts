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

  getVideoById(videoId: string): Observable<ApiResponse<VideoModel>> {
    return this.apiService
      .get<ApiResponse<VideoModel>>(`/video/data/${videoId}`)
      .pipe(
        catchError((error) => {
          console.error('Error fetching video: ', error);
          throw error;
        })
      );
  }

  uploadVideo(file: File) {
    const formData = new FormData();
    formData.append('video', file);

    return this.apiService.post(`/video/create`, { body: formData});
  }

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

  getAllVideosFromUser(id: string): Observable<ApiResponse<VideoModel[]>> {
    return this.apiService
      .get<ApiResponse<VideoModel[]>>('/video/user/' + id.replace("@", "").trim())
      .pipe(
        catchError((error) => {
          console.error('Error fetching videos: ', error);
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

  setVideoVisibility(videoId: string, visibility: string) {
    return this.apiService.patch(`/video/${videoId}/${visibility}`, { body: {visibility: visibility}});
  }

}
