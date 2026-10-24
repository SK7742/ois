import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from './core/services/api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Oxford International School CSMS';
  institution = {
    name: 'Oxford International School',
    location: 'Harpur, Raxaul, East Champaran, Bihar',
    authorizedBy: 'Anil Kumar Gupt (Management)',
    leadArchitect: 'Sundram Kumar',
    targetCapacity: 2000
  };

  activeTab: string = 'dashboard';
  dashboardMetrics: any = null;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadOverview();
  }

  loadOverview(): void {
    this.apiService.getDashboardMetrics().subscribe({
      next: (res) => {
        if (res.success) {
          this.dashboardMetrics = res.data;
        }
      },
      error: (err) => console.error('Dashboard load error', err)
    });
  }

  switchTab(tab: string): void {
    this.activeTab = tab;
  }
}
