import { Inject, Injectable } from '@nestjs/common';
import { IUsersService } from '../interface/users.interface';
import { User } from 'src/users/models/user';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { createHash } from 'crypto';

@Injectable()
export class UsersService implements IUsersService {

    constructor(@InjectRepository(User) private readonly userRepository : Repository<User>){}

    signIn(email: String, password: String) : User | null {

        // const hashPassword = createHash("sha256").update(password).digest("hex");

        // const user = this.userRepository.
        

        return null;
    }

}
