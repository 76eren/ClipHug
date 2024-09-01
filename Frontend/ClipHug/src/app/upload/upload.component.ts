import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

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


      if (this.file.type === 'video/mp4') {
        this.generateThumbnail(this.file);
      } else {
        alert('Only MP4 videos are supported.');
      }
    }
  }

  generateThumbnail(file: File) {
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
        const thumbnail = canvas.toDataURL('image/png');
        this.fileName = file.name;
        this.thumbnail = thumbnail;
        URL.revokeObjectURL(url);
      }
    });
  }

  onSubmit() {
    if (this.file) {
      console.log('Submitting file:', this.file);



    }
  }
}
