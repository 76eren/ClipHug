export class LoginModel {
  constructor(
    public username: string,
    public password: string,
    public email?: string,
    public firstName?: string,
    public lastName?: string,
  ) {}
}
