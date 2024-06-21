import { Inject, Injectable } from '@nestjs/common';
import { IUsersService } from '../interface/users.interface';
import { User } from 'src/users/models/user';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { createHash } from 'crypto';
import { Role } from 'src/users/utils/roleEnum';

import { randomBytes, scrypt, timingSafeEqual } from "crypto"
import { promisify } from "util"

const scryptPromise = promisify(scrypt)



@Injectable()
export class UsersService implements IUsersService {

    constructor(@InjectRepository(User) private readonly userRepository : Repository<User>){}
    async register(email: string, password: string, name: string,role: Role): Promise<User | null> {

        const maybeExistingUser = await this.userRepository.findOneBy({email});
        if (maybeExistingUser !== null){
            return null;
        }
        
        
        const passwordHash = await this.hash(password);
        const user = this.userRepository.create({email, password: passwordHash, name ,role});
        await this.userRepository.insert({email, password: passwordHash, name ,role});
        

        return user;

    }

    async signIn(email: string, password: string) :  Promise<User | null> {
        const user = await this.userRepository.findOneBy({email})
        if (!user || !this.verify(password,user.password)){
            return null;
        }
        return user;
    }

    private async hash(password: string) {
        const salt = randomBytes(16).toString("hex")
        const derivedKey = await scryptPromise(password, salt, 64)
        return salt + ":" + (derivedKey as Buffer).toString("hex")
    }
    
    private async verify(password: string, hash: string) {
        const [salt, key] = hash.split(":")
        const keyBuffer = Buffer.from(key, "hex")
        const derivedKey = await scryptPromise(password, salt, 64)
        return timingSafeEqual(keyBuffer, derivedKey as Buffer)
    }

}