export class UserModel {
  constructor(
    public id: string,
    public username: string,
    public role: string,
    public email?: string,
    public firstName?: string,
    public lastName?: string,
    
  ) {}
}
