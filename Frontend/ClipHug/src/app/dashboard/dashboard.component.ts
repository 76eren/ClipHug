import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LucideAngularModule} from "lucide-angular";
import {FormsModule} from "@angular/forms";
import {VideoService} from "../shared/service/requests/video.service";
import {VideoModel} from "../shared/models/Login/video.model";
import {ItemComponent} from "./item/item.component";



@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule, ItemComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})

export class DashboardComponent {
  public videos: VideoModel[] = [];

  constructor(private videoService: VideoService) {
  }

  ngOnInit() {
    this.videoService
      .getAllVideosFromSelf()
      .subscribe(response => {
        this.videos = response.payload;
      })
  }

}
