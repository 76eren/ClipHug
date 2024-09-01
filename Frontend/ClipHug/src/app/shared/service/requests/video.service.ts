import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";

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


}
