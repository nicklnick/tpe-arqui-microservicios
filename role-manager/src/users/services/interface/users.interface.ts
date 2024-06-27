import { UserEntity } from "src/users/models/user";
import { Role } from "src/users/utils/roleEnum";

export const IUsersServiceID = 'IUsersService';

export interface IUsersService {


    register(email: String, password: string, name: string,role : Role) : Promise<UserEntity | null>

    signIn(email: string, password: string) :  Promise<UserEntity | null>
}
