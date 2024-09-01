export interface ApiResponse<T> {
  payload: T;
  message: string;
  statusCode: string;
}
