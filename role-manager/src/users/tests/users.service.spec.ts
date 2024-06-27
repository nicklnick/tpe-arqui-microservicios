import { Test, TestingModule } from '@nestjs/testing';
import { UsersService } from '../services/implementation/users.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Role } from '../utils/roleEnum';
import { UserEntity } from '../models/user';

const mockUserRepository = () => ({
  findOneBy: jest.fn(),
  create: jest.fn(),
  insert: jest.fn(),
});

describe('UsersService', () => {
  let service: UsersService;
  let userRepository: Repository<UserEntity>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UsersService,
        {
            provide: getRepositoryToken(UserEntity), useFactory: mockUserRepository
        }
      ],
    }).compile();
    userRepository = module.get(getRepositoryToken(UserEntity));


    service = new UsersService(userRepository);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('register', () => {
    it('should return null if user already exists', async () => {
      const email = 'test@example.com';
      const password = 'password';
      const name = 'Test User';
      const role = Role.STUDENT;

      jest.spyOn(userRepository, 'findOneBy').mockResolvedValueOnce({ email, password, name, role, id:null });

      const result = await service.register(email, password, name, role);

      expect(result).toBeNull();
      expect(userRepository.findOneBy).toHaveBeenCalledWith({ email });
    });

    it('should create and return a user if not existing', async () => {
      const email = 'sranucci3@example.com';
      const password = 'sranucci3';
      const name = 'sranucci3';
      const role = Role.STUDENT;

      jest.spyOn(userRepository, 'findOneBy').mockResolvedValueOnce(null);
      jest.spyOn(userRepository, 'create').mockReturnValueOnce({ email, password, name, role, id:null });
      jest.spyOn(userRepository, 'insert').mockResolvedValueOnce(undefined);

      const result = await service.register(email, password, name, role);

      expect(result).toEqual({ email, password, name, role, id: null });
      expect(userRepository.findOneBy).toHaveBeenCalledWith({ email });
      expect(userRepository.create).toHaveBeenCalledWith({ email, password: expect.any(String), name, role });
      expect(userRepository.insert).toHaveBeenCalledWith({ email, password: expect.any(String), name, role });
    });
  });

  describe('signIn', () => {

    it('should return null if passwords do not match', async () => {
      const email = 'test@example.com';
      const password = 'password';
      const user = new UserEntity();
      user.email = email;
      user.password = "lalala"

      jest.spyOn(userRepository,'findOneBy').mockResolvedValueOnce(user);
      jest.spyOn(service as any, 'verify').mockResolvedValueOnce(false);

      const result = await service.signIn(email,password);

      expect(result).toBe(null);
      expect(userRepository.findOneBy).toHaveBeenCalledWith({ email });
      expect(service['verify']).toHaveBeenCalledWith(password, user.password);
    });

    it('should return null if user does not exist', async () => {
      const email = 'test@example.com';
      const password = 'password';

      jest.spyOn(userRepository, 'findOneBy').mockResolvedValueOnce(null);

      const result = await service.signIn(email, password);

      expect(result).toBeNull();
      expect(userRepository.findOneBy).toHaveBeenCalledWith({ email });
    });

    it('should return user if password matches', async () => {
      const email = 'test@example.com';
      const password = 'password';
      const user = new UserEntity();
      user.email = email;
      user.password = await service['hash'](password);

      jest.spyOn(userRepository, 'findOneBy').mockResolvedValueOnce(user);
      jest.spyOn(service as any, 'verify').mockResolvedValueOnce(true);

      const result = await service.signIn(email, password);

      expect(result).toBe(user);
      expect(userRepository.findOneBy).toHaveBeenCalledWith({ email });
      expect(service['verify']).toHaveBeenCalledWith(password, user.password);
    });

    
  });
});
