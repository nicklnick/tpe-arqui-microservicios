import { Module } from '@nestjs/common';
import { UsersController } from './controllers/users.controller';
import { UsersService } from './services/implementation/users.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './models/user';
import { IUsersServiceID } from './services/interface/users.interface';

@Module({
  imports : [TypeOrmModule.forFeature([User])],
  controllers: [UsersController],
  providers: [{
    provide: IUsersServiceID,
    useClass: UsersService,
  }]
})
export class UsersModule {}
