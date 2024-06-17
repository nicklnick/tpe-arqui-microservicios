import { User } from "src/users/models/user";



export interface IUsersService {

    signIn(email: String, password: String) : User

}