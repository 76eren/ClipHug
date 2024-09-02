import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root',
})
export class FrameService {
  generateThumbnail(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const url = URL.createObjectURL(file);
      const video = document.createElement('video');
      const canvas = document.createElement('canvas');
      const context = canvas.getContext('2d');

      video.src = url;
      video.load();

      video.addEventListener('loadeddata', () => {
        video.currentTime = 1;
      });

      video.addEventListener('seeked', () => {
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        if (context) {
          context.drawImage(video, 0, 0, canvas.width, canvas.height);
          const myThumbnail = canvas.toDataURL('image/png');
          URL.revokeObjectURL(url);
          resolve(myThumbnail);
        } else {
          reject(new Error('Unable to get canvas context'));
        }
      });

      video.addEventListener('error', () => {
        reject(new Error('Error loading video'));
      });
    });
  }
}
