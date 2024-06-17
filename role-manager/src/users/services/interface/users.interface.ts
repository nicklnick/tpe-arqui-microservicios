import { User } from "src/users/models/user";
import { Role } from "src/users/utils/roleEnum";


export const IUsersServiceID = "IUsersService";

export interface IUsersService {


    register(email: String, password: string, name: string,role : Role) : Promise<User | null>

    signIn(email: string, password: string) :  Promise<User | null>
}