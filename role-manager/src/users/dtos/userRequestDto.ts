import { IsEmail, IsString } from "class-validator";



export class userRequestDto {


    @IsEmail()
    readonly email: String;

    @IsString()
    readonly password: String;
}