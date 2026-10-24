import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Student, Employee, Invoice, TimetableSlot, IssueTicket, WhatsAppMessage } from '../models/csms.models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('oxford_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  getDashboardMetrics(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/admin/dashboard/overview`, { headers: this.getHeaders() });
  }

  getStudents(grade?: string): Observable<ApiResponse<Student[]>> {
    const url = grade ? `${this.baseUrl}/admin/students?grade=${encodeURIComponent(grade)}` : `${this.baseUrl}/admin/students`;
    return this.http.get<ApiResponse<Student[]>>(url, { headers: this.getHeaders() });
  }

  getEmployees(): Observable<ApiResponse<Employee[]>> {
    return this.http.get<ApiResponse<Employee[]>>(`${this.baseUrl}/admin/employees`, { headers: this.getHeaders() });
  }

  getInvoices(): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(`${this.baseUrl}/finance/fees/invoices`, { headers: this.getHeaders() });
  }

  getTimetable(grade: string, section: string): Observable<ApiResponse<TimetableSlot[]>> {
    return this.http.get<ApiResponse<TimetableSlot[]>>(`${this.baseUrl}/academic/timetable/class?grade=${encodeURIComponent(grade)}&section=${encodeURIComponent(section)}`, { headers: this.getHeaders() });
  }

  autoGenerateTimetable(grade: string, section: string): Observable<ApiResponse<TimetableSlot[]>> {
    return this.http.post<ApiResponse<TimetableSlot[]>>(`${this.baseUrl}/academic/timetable/auto-generate`, { grade, section, roomNumber: 'Room 101' }, { headers: this.getHeaders() });
  }

  getTickets(): Observable<ApiResponse<IssueTicket[]>> {
    return this.http.get<ApiResponse<IssueTicket[]>>(`${this.baseUrl}/communication/tickets`, { headers: this.getHeaders() });
  }

  getWhatsAppLogs(): Observable<ApiResponse<WhatsAppMessage[]>> {
    return this.http.get<ApiResponse<WhatsAppMessage[]>>(`${this.baseUrl}/communication/whatsapp/logs`, { headers: this.getHeaders() });
  }
}
