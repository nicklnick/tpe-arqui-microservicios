import { Module } from '@nestjs/common';
import { UsersModule } from './users/users.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ConfigModule } from '@nestjs/config';
import { User } from './users/models/user';


@Module({
  imports: [
    TypeOrmModule.forRoot({
      type : "postgres",
      host : "localhost",
      port: 5432,
      username: "postgres",
      password: "postgres",
      database: "db-role-manager",
      entities: [User],
      synchronize: true
    }),
    UsersModule
  ],
  
})
export class AppModule {}
