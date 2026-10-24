export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface Student {
  id: number;
  admissionNo: string;
  rollNo?: number;
  firstName: string;
  lastName: string;
  fullName: string;
  dateOfBirth?: string;
  gender: string;
  currentClass: string;
  section: string;
  guardianName: string;
  guardianPhone: string;
  guardianEmail?: string;
  bloodGroup?: string;
  residentialAddress?: string;
  busRouteId?: number;
  attendanceRate: number;
  feeStatus: 'CLEARED' | 'PENDING' | 'OVERDUE';
  active: boolean;
}

export interface Employee {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  phone: string;
  department: string;
  designation: string;
  qualification?: string;
  staffCategory: 'TEACHING' | 'NON_TEACHING';
  basicSalary: number;
  allowances: number;
  active: boolean;
}

export interface Invoice {
  id: number;
  invoiceNumber: string;
  studentId: number;
  studentName: string;
  admissionNo: string;
  grade: string;
  billingPeriod: string;
  grossAmount: number;
  discountAmount: number;
  netPayable: number;
  paidAmount: number;
  status: 'PENDING' | 'PARTIALLY_PAID' | 'PAID' | 'CANCELLED';
  dueDate: string;
  scholarshipApplied?: string;
}

export interface TimetableSlot {
  id: number;
  grade: string;
  section: string;
  dayOfWeek: string;
  periodNumber: number;
  periodTime: string;
  subject: string;
  teacherId?: number;
  teacherName?: string;
  roomNumber?: string;
}

export interface IssueTicket {
  id: number;
  ticketNumber: string;
  title: string;
  description: string;
  category: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  slaHours: number;
  deadline: string;
  reportedBy: string;
  status: 'OPEN' | 'IN_PROGRESS' | 'ESCALATED_TO_MANAGEMENT' | 'RESOLVED' | 'CLOSED';
  escalated: boolean;
  escalatedTo?: string;
}

export interface WhatsAppMessage {
  id: number;
  recipientPhone: string;
  recipientName: string;
  templateName: string;
  messageBody: string;
  status: 'QUEUED' | 'SENT' | 'DELIVERED' | 'FAILED';
  providerMessageId?: string;
  createdAt: string;
}
