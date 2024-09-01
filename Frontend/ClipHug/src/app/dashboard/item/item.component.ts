import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {VideoModel} from "../../shared/models/Login/video.model";

@Component({
  selector: 'app-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './item.component.html',
  styleUrl: './item.component.scss'
})
export class ItemComponent {
  @Input() video?: VideoModel;

}
