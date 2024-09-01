export class RegisterModel {
  constructor(
    public username: string,
    public password: string,
    public pin: string,
    public email?: string,
    public firstName?: string,
    public lastName?: string,
  ) {
  }
}
