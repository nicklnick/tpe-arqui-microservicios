import { IsEmail, IsEnum, IsNotEmpty, IsString } from "class-validator";
import { Role } from "../utils/roleEnum";



export class UserRegisterDto {


    
    @IsEmail()
    readonly email: string;

    @IsString()
    @IsNotEmpty()
    readonly name: string;

    @IsString()
    @IsNotEmpty()
    readonly password: string;

    @IsNotEmpty()
    @IsEnum(Role)
    readonly role : Role;
}