import { Test, TestingModule } from '@nestjs/testing';
import { UsersController } from '../controllers/users.controller';
import { IUsersService, IUsersServiceID } from '../services/interface/users.interface';
import { UserRequestDto } from '../dtos/userRequestDto';
import { UserRegisterDto } from '../dtos/userRegisterDto';
import { ConflictException } from '@nestjs/common';
import { Role } from '../utils/roleEnum';

describe('UsersController', () => {
  let controller: UsersController;
  let userService: IUsersService;

  const mockUserService = {
    signIn: jest.fn(),
    register: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UsersController],
      providers: [
        {
          provide: IUsersServiceID,
          useValue: mockUserService,
        },
      ],
    }).compile();

    controller = module.get<UsersController>(UsersController);
    userService = module.get<IUsersService>(IUsersServiceID);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('signIn', () => {
    it('should return user data if signIn is successful', async () => {
      const userDto: UserRequestDto = { email: 'test@example.com', password: 'password' };
      const userResponse = { id: '1', email: 'test@example.com', role: Role.STUDENT };

      mockUserService.signIn.mockResolvedValue(userResponse);

      const result = await controller.signIn(userDto);

      expect(result).toEqual({ email: 'test@example.com', role: Role.STUDENT });
      expect(mockUserService.signIn).toHaveBeenCalledWith('test@example.com', 'password');
    });

    it('should throw ConflictException if user is not found', async () => {
      const userDto: UserRequestDto = { email: 'test@example.com', password: 'password' };

      mockUserService.signIn.mockResolvedValue(null);

      await expect(controller.signIn(userDto)).rejects.toThrow(ConflictException);
      expect(mockUserService.signIn).toHaveBeenCalledWith('test@example.com', 'password');
    });
  });

  describe('register', () => {
    it('should return user email if registration is successful', async () => {
      const userDto: UserRegisterDto = { email: 'test@example.com', password: 'password', name: 'Test User', role: Role.STUDENT };
      const userResponse = { email: 'test@example.com' };

      mockUserService.register.mockResolvedValue(userResponse);

      const result = await controller.register(userDto);

      expect(result).toEqual({ email: 'test@example.com' });
      expect(mockUserService.register).toHaveBeenCalledWith('test@example.com', 'password', 'Test User', Role.STUDENT);
    });

    it('should throw ConflictException if registration fails', async () => {
      const userDto: UserRegisterDto = { email: 'test@example.com', password: 'password', name: 'Test User', role: Role.STUDENT };

      mockUserService.register.mockResolvedValue(null);

      await expect(controller.register(userDto)).rejects.toThrow(ConflictException);
      expect(mockUserService.register).toHaveBeenCalledWith('test@example.com', 'password', 'Test User', Role.STUDENT);
    });
  });
});
