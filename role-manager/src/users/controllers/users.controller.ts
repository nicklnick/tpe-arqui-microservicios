import {
  Body,
  ConflictException,
  Controller,
  Get,
  HttpCode,
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

@Controller('/api/users')
export class UsersController {
    
    constructor (@Inject(IUsersServiceID) private readonly userService: IUsersService ){}

    @HttpCode(200)
    @Post("signIn")
    async signIn(@Body() user: UserRequestDto){
        const userData = await this.userService.signIn(user.email,user.password)
        if (!userData){
            throw new ConflictException();
        }
        return {
            userId: userData.id,
            name: userData.name,
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
            email: inserted.email,
        }
    }


}
