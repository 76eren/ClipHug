import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {VideoService} from "../shared/service/requests/video.service";
import {HttpErrorResponse} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";
import {FrameService} from "../shared/service/frame.service";

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.scss']
})
export class UploadComponent {
  fileName: string = "";
  thumbnail: string = "";
  file?: File;

  constructor(private videoService: VideoService, private toastr: ToastrService, private frameService: FrameService) {

  }

  onDrop($event: DragEvent) {
    $event.preventDefault();
    this.handleFile($event.dataTransfer!.files);
  }

  onDragOver($event: DragEvent) {
    $event.preventDefault();
  }

  onDragLeave($event: DragEvent) {
  }

  onFileSelected($event: Event) {
    const input = $event.target as HTMLInputElement;
    if (input.files) {
      this.handleFile(input.files);
    }
  }

  handleFile(files: FileList | null) {
    if (files && files.length > 0) {
      this.file = files[0];
      this.fileName = this.file.name;

      if (this.file.type === 'video/mp4') {

        this.frameService.generateThumbnail(this.file).then(thumbnail => {
          this.thumbnail = thumbnail;
        }).catch(error => {
          console.error('Failed to generate thumbnail', error);
        });
      }
      else {
        this.toastr.error('Invalid file type');
        this.thumbnail = "";
        this.fileName = "";
        this.file = undefined;
      }
    }
  }

  onSubmit() {
    if (this.file) {
      this.videoService.uploadVideo(this.file).subscribe((response) => {
        this.toastr.success('Video uploaded successfully');
      },
      (error: HttpErrorResponse) => {
        this.toastr.error('Failed to upload video');
      });
    }
  }
}
