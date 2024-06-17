import { Body, Controller, Get } from '@nestjs/common';
import { userRequestDto } from '../dtos/userRequestDto';
import { IUsersService } from '../services/interface/users.interface';

@Controller('users')
export class UsersController {

    constructor (private readonly userService: IUsersService ){}

    @Get("signIn")
    async signIn(@Body() user: userRequestDto){


    }
}
