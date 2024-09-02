import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from "@angular/forms";
import {VideoModel} from "../shared/models/Login/video.model";
import {ToastrService} from "ngx-toastr";
import {VideoService} from "../shared/service/requests/video.service";
import {UserService} from "../shared/service/requests/user.service";
import {FrameService} from "../shared/service/frame.service";

@Component({
  selector: 'app-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manager.component.html',
  styleUrl: './manager.component.scss'
})
export class ManagerComponent {
  public videoUrl?: string = "";

  public video?: VideoModel; // This does not get updated after changing visibilities
  public thumbnail: string = "";

  hidden: boolean = true;
  videoVisibility: string = ""; // This does change with the visibilities in life time

  constructor(private toastr: ToastrService, private videoService: VideoService, private userService: UserService, private frameService: FrameService) {
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


        // Now we check if the video we want to edit is actually ours
        this.userService.getCurrentSignedInUser().subscribe({
          next: (userResponse) => {
            if (this.checkIfVideoIsMine(response, userResponse)) {
              this.applyThumb();
            }
          },
          error: (err) => {
            this.toastr.error("Error fetching user");
          }
        });

      },
      error: (err) => {
        this.toastr.error("Error fetching video");
      }
    });
  }

  checkIfVideoIsMine(response: any, userResponse: any): boolean {
    if (response.payload.authorId !== userResponse.payload.id) {
      this.toastr.error("You can only edit your own videos");
      return false;
    }
    else {
      this.toastr.success("Video fetched successfully");
      this.video = response.payload;
      this.videoVisibility = this.video!.visibility;
      return true;
    }
  }

  applyThumb() {
    this.thumbnail = this.videoService.getThumbnailUrl(this.video!);
    this.hidden = false;
  }

  onDeleteClick() {
    this.toastr.info("This feature is not implemented yet")
  }

  onPrivateClick() {
    this.videoService.setVideoVisibility(this.video!.videoId, "private").subscribe({
      next: () => {
        this.toastr.success("Video is now private");
        this.videoVisibility = "private";
      }, error: () => {
        this.toastr.error("Failed to set video visibility")
      }
    });
  }

  onPublicClick() {
    this.videoService.setVideoVisibility(this.video!.videoId, "public").subscribe({
      next: () => {
        this.toastr.success("Video is now public");
        this.videoVisibility = "public";
      }, error: () => {
        this.toastr.error("Failed to set video visibility")
      }
    });
  }

  onUnlistedClick() {
    this.videoService.setVideoVisibility(this.video!.videoId, "unlisted").subscribe({
      next: () => {
        this.toastr.success("Video is now unlisted");
        this.videoVisibility = "unlisted";
      }, error: () => {
        this.toastr.error("Failed to set video visibility")
      }
    });
  }
}
