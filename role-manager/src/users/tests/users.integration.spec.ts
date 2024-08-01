import { Test, TestingModule } from '@nestjs/testing';
import { UsersController } from '../controllers/users.controller';
import { UsersService } from '../services/implementation/users.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { UserEntity } from '../models/user';
import { Repository } from 'typeorm';
import { ConflictException } from '@nestjs/common';
import { UserRequestDto } from '../dtos/userRequestDto';
import { UserRegisterDto } from '../dtos/userRegisterDto';
import { Role } from '../utils/roleEnum';
import { IUsersServiceID } from '../services/interface/users.interface';

describe('UsersController (Integration)', () => {
  let controller: UsersController;
  let service: UsersService;
  let repository: Repository<UserEntity>;

  const mockUserRepository = {
    findOneBy: jest.fn(),
    create: jest.fn(),
    insert: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UsersController],
      providers: [
        UsersService,
        {
            provide: IUsersServiceID,
            useClass: UsersService
        },
        {
          provide: getRepositoryToken(UserEntity),
          useValue: mockUserRepository,
        },
      ],
    }).compile();

    controller = module.get<UsersController>(UsersController);
    service = module.get<UsersService>(UsersService);
    repository = module.get<Repository<UserEntity>>(getRepositoryToken(UserEntity));

  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('signIn', () => {
    it('should return user data if signIn is successful', async () => {
      const user = { id: 1, email: 'test@example.com', password: '2893b25fdf2461076486ce95b25a12bd:4f488f779ff511d610c0d938452d316fb385b72905cec7201705ee4f0efc3b40879a44d7713715c5e33ee9cdf676134bea7b00875d030c6f251c6c18dfd266cb', name: 'Test User', role: Role.STUDENT };
      const userDto: UserRequestDto = { email: 'test@example.com', password: 'sranucci' };

      mockUserRepository.findOneBy.mockResolvedValue(user);

      const result = await controller.signIn(userDto);

      expect(result).toEqual({  email: user.email, role: user.role , name: "Test User", userId: 1});
    });

    it('should throw ConflictException if user is not found', async () => {
      const userDto: UserRequestDto = { email: 'test@example.com', password: 'password' };

      mockUserRepository.findOneBy.mockResolvedValue(null);

      await expect(controller.signIn(userDto)).rejects.toThrow(ConflictException);
    });

    it('should throw ConflictException if password is incorrect', async () => {
      const user = { id: '1', email: 'test@example.com', password: '2893b25fdf2461076486ce95b25a12bd:4f488f779ff511d610c0d938452d316fb385b72905cec7201705ee4f0efc3b40879a44d7713715c5e33ee9cdf676134bea7b00875d030c6f251c6c18dfd266cb', name: 'Test User', role: Role.STUDENT };
      const userDto: UserRequestDto = { email: 'test@example.com', password: 'wrongpassword' };

      mockUserRepository.findOneBy.mockResolvedValue(user);

      await expect(controller.signIn(userDto)).rejects.toThrow(ConflictException);
    });
  });

  describe('register', () => {
    it('should return user id if registration is successful', async () => {
      const userDto: UserRegisterDto = { email: 'test@example.com', password: 'password', name: 'Test User', role: Role.STUDENT };
      const user = { id: '1', email: 'test@example.com', password: 'hashedpassword', name: 'Test User', role: Role.STUDENT };

      mockUserRepository.findOneBy.mockResolvedValue(null);
      mockUserRepository.create.mockReturnValue(user);
      mockUserRepository.insert.mockResolvedValue(user);

      const result = await controller.register(userDto);

      expect(result).toEqual({ email: user.email });
    
    });

    it('should throw ConflictException if user already exists', async () => {
      const userDto: UserRegisterDto = { email: 'test@example.com', password: 'password', name: 'Test User', role: Role.STUDENT };
      const existingUser = { id: '1', email: 'test@example.com', password: '2893b25fdf2461076486ce95b25a12bd:4f488f779ff511d610c0d938452d316fb385b72905cec7201705ee4f0efc3b40879a44d7713715c5e33ee9cdf676134bea7b00875d030c6f251c6c18dfd266cb', name: 'Test User', role: Role.STUDENT };

      mockUserRepository.findOneBy.mockResolvedValue(existingUser);

      await expect(controller.register(userDto)).rejects.toThrow(ConflictException);
    });
  });
});
