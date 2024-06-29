import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import { UsersController } from '../src/users/controllers/users.controller';
import { UsersService } from '../src/users/services/implementation/users.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UserEntity } from '../src/users/models/user';
import { Role } from '../src/users/utils/roleEnum';
import { UserRequestDto } from '../src/users/dtos/userRequestDto';
import { UserRegisterDto } from '../src/users/dtos/userRegisterDto';
import { IUsersServiceID } from '../src/users/services/interface/users.interface';
import { Repository } from 'typeorm';
import { ConfigModule, ConfigService } from '@nestjs/config';



describe('UsersController (E2E)', () => {
  let app: INestApplication;
  let repository: Repository<UserEntity>;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [
        ConfigModule.forRoot({
          isGlobal: true,
        }),
        TypeOrmModule.forRootAsync({
          imports: [ConfigModule],
          useFactory: async (configService: ConfigService) => ({
            type: 'postgres',
            host: configService.get<string>('TYPEORM_HOST'),
            port: configService.get<number>('TYPEORM_PORT'),
            username: configService.get<string>('TYPEORM_USERNAME'),
            password: configService.get<string>('TYPEORM_PASSWORD'),
            database: configService.get<string>('TYPEORM_NAME'),
            entities: [UserEntity],
            synchronize: true,
          }),
          inject: [ConfigService],
        }),
        TypeOrmModule.forFeature([UserEntity]),
      ],
      controllers: [UsersController],
      providers: [
        UsersService,
        {
          provide: IUsersServiceID,
          useClass: UsersService,
        },
      ],
    }).compile();

    app = moduleFixture.createNestApplication();
    await app.init();

    repository = moduleFixture.get('UserEntityRepository');
  });

  afterAll(async () => {
    await repository.query('DELETE FROM user_entity;');
    await app.close();
  });


  beforeEach(async () => {
    await repository.query('DELETE FROM user_entity;');
  });

  

  describe('/users/register (POST)', () => {
    it('should register a user successfully', async () => {
      const userRegisterDto: UserRegisterDto = {
        email: 'test@example.com',
        password: 'password',
        name: 'Test User',
        role: Role.STUDENT,
      };

      const response = await request(app.getHttpServer())
        .post('/users/register')
        .send(userRegisterDto)
        .expect(201);

      expect(response.body).toHaveProperty('email');
      //Chequear aca que este en la DB.
      
    });

    it('should return 409 Conflict if user already exists', async () => {
      const userRegisterDto: UserRegisterDto = {
        email: 'test@example.com',
        password: 'password',
        name: 'Test User',
        role: Role.STUDENT,
      };

      await repository.save({
        email: 'test@example.com',
        password: await ((new UsersService(repository)) as any).hash('password'),
        name: 'Test User',
        role: Role.STUDENT,
      });

      await request(app.getHttpServer())
        .post('/users/register')
        .send(userRegisterDto)
        .expect(409);
    });
  });

  describe('/users/signIn (POST)', () => {
    it('should sign in a user successfully', async () => {
      const user = {
        email: 'test@example.com',
        password: await ((new UsersService(repository)) as any).hash('password'),
        name: 'Test User',
        role: Role.STUDENT,
      };

      await repository.save(user);

      const userRequestDto: UserRequestDto = {
        email: 'test@example.com',
        password: 'password',
      };

      const response = await request(app.getHttpServer())
        .post('/users/signIn')
        .send(userRequestDto)
        .expect(200);

      expect(response.body).toEqual({
        email: user.email,
        role: user.role,
      });
    });

    it('should return 409 Conflict if user not found', async () => {
      const userRequestDto: UserRequestDto = {
        email: 'nonexistent@example.com',
        password: 'password',
      };

      await request(app.getHttpServer())
        .post('/users/signIn')
        .send(userRequestDto)
        .expect(409);
    });

    it('should return 409 Conflict if password is incorrect', async () => {
      const user = {
        email: 'test@example.com',
        password: await ((new UsersService(repository)) as any).hash('password'),
        name: 'Test User',
        role: Role.STUDENT,
      };

      await repository.save(user);

      const userRequestDto: UserRequestDto = {
        email: 'test@example.com',
        password: 'wrongpassword',
      };

      await request(app.getHttpServer())
        .post('/users/signIn')
        .send(userRequestDto)
        .expect(409);
    });
  });
});


