import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {VideoModel} from "../../shared/models/Login/video.model";
import {VideoService} from "../../shared/service/requests/video.service";

@Component({
  selector: 'app-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './item.component.html',
  styleUrl: './item.component.scss'
})
export class ItemComponent {
  @Input() video?: VideoModel;

  constructor(private videoService: VideoService) {
  }

  getVideoUrl(video: VideoModel) {
    return this.videoService.getVideoUrl(this.video!);
  }

  getThumbnailUrl(video: VideoModel) {
    return this.videoService.getThumbnailUrl(this.video!);

  }

  onVideoClick() {
    window.open(this.getVideoUrl(this.video!));
  }
}
