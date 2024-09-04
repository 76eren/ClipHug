import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {VideoModel} from "../../shared/models/Login/video.model";
import {VideoService} from "../../shared/service/requests/video.service";
import {ThumbnailsService} from "../../shared/service/requests/thumbnails.service";
import {SafeUrl} from "@angular/platform-browser";

@Component({
  selector: 'app-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './item.component.html',
  styleUrl: './item.component.scss'
})
export class ItemComponent {
  @Input() video?: VideoModel;
  @Input() index = 0;
  @Input() thumbnail?: SafeUrl | null= ""

  constructor(private videoService: VideoService, private thumbnailService: ThumbnailsService) {
  }


  getVideoUrl(video: VideoModel) {
    return this.videoService.getVideoUrl(this.video!);
  }


  onVideoClick() {
    window.open(this.getVideoUrl(this.video!));
  }
}
