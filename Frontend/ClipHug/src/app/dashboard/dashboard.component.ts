import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { FormsModule } from '@angular/forms';
import { VideoService } from '../shared/service/requests/video.service';
import { VideoModel } from '../shared/models/Login/video.model';
import { ItemComponent } from './item/item.component';
import { ToastrService } from 'ngx-toastr';
import { ThumbnailsService } from '../shared/service/requests/thumbnails.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule, ItemComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  public videos: VideoModel[] = [];
  public videosFiltered: VideoModel[] = [];
  private videosOriginal: VideoModel[] = [];
  videoUrlInputBar: string = "";
  bulkAmount: number = 6;
  bulks: number = 0;
  page = 1;

  constructor(
    private videoService: VideoService,
    private toastr: ToastrService,
    public thumbnailService: ThumbnailsService
  ) {}

  ngOnInit() {
    this.loadVideos();
  }

  loadVideos(searchTerm: string = '') {
    let videoObservable = this.videoUrlInputBar.startsWith('@') ?
      this.videoService.getAllVideosFromUser(this.videoUrlInputBar) :
      this.videoService.getAllVideosFromSelf();

    videoObservable.subscribe({
      next: response => {
        this.handleVideoResponse(response.payload);
      },
      error: () => {
        this.toastr.error("User not found");
      }
    });
  }

  handleVideoResponse(videos: VideoModel[]) {
    this.videos = videos;
    this.videosOriginal = videos;
    this.updatePagination();
  }

  onInputChange() {
    if (this.videoUrlInputBar === '') {
      this.loadVideos();
    } else if (this.videoUrlInputBar.startsWith('@')) {
      this.loadVideos(this.videoUrlInputBar);
    } else {
      this.filterVideos();
    }
  }

  filterVideos() {
    this.videos = this.videosOriginal.filter(video =>
      video.fileName.includes(this.videoUrlInputBar) || video.videoId.includes(this.videoUrlInputBar)
    );
    this.updatePagination();
  }

  updatePagination() {
    this.page = 1;
    this.bulks = Math.ceil(this.videos.length / this.bulkAmount);
    this.updateFilteredVideos();
  }

  updateFilteredVideos() {
    const startIndex = (this.page - 1) * this.bulkAmount;
    this.videosFiltered = this.videos.slice(startIndex, startIndex + this.bulkAmount);
    this.changeThumbnails();
  }

  changeThumbnails() {
    let idsToGet = this.videosFiltered.map((video) => video.videoId);
    this.videoService.getMultipleThumbnailsUrl(idsToGet).subscribe({
      next: (response) => {
        this.thumbnailService.setThumbnails(response.payload);
      }
    });
  }

  previousPage() {
    if (this.page === 1) return;
    this.page--;
    this.updateFilteredVideos();
  }

  nextPage() {
    if (this.page === this.bulks) return;
    this.page++;
    this.updateFilteredVideos();
  }
}
