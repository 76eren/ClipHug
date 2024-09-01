import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from "@angular/forms";
import {VideoModel} from "../shared/models/Login/video.model";
import {ToastrService} from "ngx-toastr";
import {VideoService} from "../shared/service/requests/video.service";

@Component({
  selector: 'app-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manager.component.html',
  styleUrl: './manager.component.scss'
})
export class ManagerComponent {
  public videoUrl?: string = "";
  public video?: VideoModel;

  constructor(private toastr: ToastrService, private videoService: VideoService) {
  }

  onButtonClick() {
    if (!this.videoUrl) {
      this.toastr.error("Please enter a video link");
      return;
    }

    const uuid = this.videoUrl!.match(/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/);

    if (!uuid) {
      this.toastr.error("Invalid video link");
      return;
    }

    this.videoService.getVideoById(uuid[0]).subscribe({
      next: (response) => {
        this.toastr.success("Video fetched successfully");
        this.video = response.payload;
      },
      error: (err) => {
        this.toastr.error("Error fetching video");
      }
    });


  }
}
