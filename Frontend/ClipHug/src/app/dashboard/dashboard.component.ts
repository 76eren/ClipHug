import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LucideAngularModule} from "lucide-angular";
import {FormsModule} from "@angular/forms";



@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})

export class DashboardComponent {

}
