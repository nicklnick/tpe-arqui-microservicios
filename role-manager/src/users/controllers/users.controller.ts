import {
  Body,
  ConflictException,
  Controller,
  Get,
  HttpStatus,
  Inject,
  Post,
} from '@nestjs/common';
import { UserRequestDto } from '../dtos/userRequestDto';
import {
  IUsersService,
  IUsersServiceID,
} from '../services/interface/users.interface';
import { UserRegisterDto } from '../dtos/userRegisterDto';

@Controller('users')
export class UsersController {
    
    constructor (@Inject(IUsersServiceID) private readonly userService: IUsersService ){}

    @Get("signIn")
    async signIn(@Body() user: UserRequestDto){
        const userData = await this.userService.signIn(user.email,user.password)
        if (!userData){
            throw new ConflictException();
        }
        return {
            email: userData.email,
            role : userData.role
        }
    }

    @Post("register")
    async register(@Body() user: UserRegisterDto){
       
        const inserted = await this.userService.register(user.email,user.password,user.name,user.role);
        if (!inserted) {
            throw new ConflictException();
        }
        return {
            email: inserted.email
        }
    }


}
