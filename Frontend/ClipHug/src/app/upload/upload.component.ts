import {Component, ElementRef, ViewChild} from '@angular/core';
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

  @ViewChild('fileInput') fileInput!: ElementRef;
  private canSubmit: boolean = true;

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
      if (files[0].size > 150000000) {
        this.toastr.error('You can only upload files up to 150MB in size.');
        return;
      }

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
    if (!this.canSubmit) {
      this.toastr.warning('Please wait for the previous upload to finish');
      return;
    }

    if (this.file) {
      this.canSubmit = false;
      this.videoService.uploadVideo(this.file).subscribe((response) => {
        this.toastr.success('Video uploaded successfully');
        this.canSubmit = true;
        },
      (error: HttpErrorResponse) => {
        this.toastr.error('Failed to upload video');
        this.canSubmit = true;
      });
    }
  }

  onPreviewClick() {
    this.fileInput.nativeElement.click();
  }
}
