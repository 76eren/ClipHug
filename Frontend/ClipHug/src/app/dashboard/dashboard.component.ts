import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LucideAngularModule} from "lucide-angular";
import {FormsModule} from "@angular/forms";
import {VideoService} from "../shared/service/requests/video.service";
import {VideoModel} from "../shared/models/Login/video.model";
import {ItemComponent} from "./item/item.component";
import {ToastrService} from "ngx-toastr";
import {ThumbnailsService} from "../shared/service/requests/thumbnails.service";


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule, ItemComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})

export class DashboardComponent {
  public videos: VideoModel[] = [];
  public videosFiltered: VideoModel[] = [];

  videoUrlInputBar: string = "";

  // Videos will be loaded in bulks because the server can't handle all the videos at once
  bulkAmount: number = 6;
  bulks: number = 0;
  page = 1;

  public images: string[] = []

  constructor(private videoService: VideoService, private toastr: ToastrService, private thumbnailService: ThumbnailsService) {
  }

  ngOnInit() {
    this.videoService
      .getAllVideosFromSelf()
      .subscribe(response => {
        this.videos = response.payload;

        this.bulks = Math.ceil(this.videos.length / this.bulkAmount)

        this.videosFiltered = this.videos.slice(0, this.bulkAmount);
        this.changeThumbnails();
      })
  }

  onInputChange() {
    if (this.videoUrlInputBar === "") {
      this.videosFiltered = this.videos;
      return;
    }

    if (this.videoUrlInputBar.startsWith("@")) {
      this.videoService
        .getAllVideosFromUser(this.videoUrlInputBar)
        .subscribe({
          next: (response) => {
            this.videosFiltered = response.payload;
          },
          error: (err) => {
            this.toastr.error("User not found");
          }
        });

      return;
    }


    this.videosFiltered = [];
    for (let i of this.videos) {
      if (i.fileName.includes(this.videoUrlInputBar) || i.videoId.includes(this.videoUrlInputBar)) {
        this.videosFiltered.push(i);
      }
    }
  }

  changeThumbnails() {
    let idsToGet: string[] = this.videosFiltered.map((video) => video.videoId);
    console.log(idsToGet);
    this.videoService.getMultipleThumbnailsUrl(idsToGet).subscribe({ next: (response) => {
        this.thumbnailService.setThumbnails(response.payload);
      }});
  }

  previousPage() {
    if (this.page === 1) {
      return;
    }
    this.page --;
    this.videosFiltered = this.videos.slice(this.page * this.bulkAmount - this.bulkAmount, this.page * this.bulkAmount);
    this.changeThumbnails();
  }

  nextPage() {
    if (this.page === this.bulks-1) {
      return;
    }
    this.page ++;
    this.videosFiltered = this.videos.slice(this.page * this.bulkAmount, this.page * this.bulkAmount + this.bulkAmount);
    this.changeThumbnails();
  }
}
